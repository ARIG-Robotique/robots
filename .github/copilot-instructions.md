# Copilot Instructions

## Project Overview

Eurobot competition robots codebase (ARIG Association). Java/Spring Boot multi-module Gradle project targeting Raspberry Pi hardware. Active modules: **nerell** (main robot), **pami** (small secondary robots — 4 variants: triangle, carre, rond, star).

## Build & Test Commands

```bash
# Full build + tests
./gradlew build

# Build without tests (used before deployment)
./gradlew assemble

# Run tests for a specific module
./gradlew :robot-system-lib-parent:robot-system-lib-core:test

# Run a single test class
./gradlew :robot-system-lib-parent:robot-system-lib-core:test --tests "org.arig.robot.filters.common.ChangeFilterTest"

# Run pre-commit checks
pre-commit run --all-files
```

CI builds against **Java 21 and 23** using the Liberica JDK distribution.

## Module Architecture

```
robot-system-lib-parent/          # Core technical library (hardware-agnostic)
  robot-system-lib-core/          # Filters, PID, pathfinding, strategy engine, models
  robot-system-lib-raspi/         # Raspberry Pi bindings (Pi4J, I2C, CAN bus)
  robot-system-lib-bouchon/       # Mock/stub hardware for simulator/dev (bouchon = stub)
  robot-system-lib-joycon/        # Joy-Con controller support

eurobot-system-lib-parent/        # Eurobot-competition-specific, shared across robots
  eurobot-system-lib-core/        # Table pathfinding, Lidar, Eurobot rules
  eurobot-system-lib-bouchon/     # Mock implementations for eurobot layer

nerell-parent/                    # Main robot
  nerell-common/                  # Shared logic: model, services, strategy actions, Spring config
  nerell-robot/                   # Spring Boot executable (depends on raspi + common)
  nerell-simulator/               # Simulator executable (depends on bouchon + common)
  nerell-utils/                   # Utility shell application

pami-parent/                      # PAMI small robots (same structure as nerell)
```

**`odin-parent` and `tinker-parent` are commented out** in `settings.gradle.kts` — do not uncomment unless intentionally re-enabling them.

## Key Conventions

### Lombok
All classes use Lombok extensively. Prefer `@Slf4j`, `@Data`, `@Getter`, `@Setter`. Models use `@Accessors(fluent = true)` — accessor methods have **no `get`/`set` prefix** (e.g., `rs.mainRobot()` not `rs.getMainRobot()`).

### Strategy / Action Pattern
Robot behaviour is driven by a list of `Action` beans managed by `StrategyManager`. To add a game action:
1. Extend `AbstractNerellAction` (or `AbstractPamiAction`) — which extends `AbstractAction implements Action`
2. Place in `strategy/actions/active/` (enabled) or `strategy/actions/disabled/` (disabled)
3. Implement `name()`, `order()`, `isValid()`, `execute()`, and `entryPoint()`
4. Register as a Spring bean (via `@Component` or in the `*StrategyContext` config class)

`StrategyManager.execute()` sorts valid actions by `order()` descending and picks the highest priority one each cycle.

### Spring Configuration Pattern
Config is split into multiple `@Configuration` classes per concern:
- `*Context` — core beans (robot status, config)
- `*ServicesContext` — service beans
- `*StrategyContext` — action/strategy beans
- `*SchedulerContext` — scheduled task configuration
- `*WebAppContext` — REST controller configuration

### Bouchon (Mock) vs Raspi
Hardware interfaces have two implementations:
- `robot-system-lib-raspi`: real Pi hardware (I2C, GPIO, CAN)
- `robot-system-lib-bouchon`: no-op stubs for simulator/local dev

Robot executables (`nerell-robot`) depend on `raspi`. Simulator executables (`nerell-simulator`) depend on `bouchon`.

### Versioning & Build
- All artifacts use version `BUILD-SNAPSHOT`, group `org.arig.robot`
- Dependency versions are in `gradle/libs.versions.toml` (version catalog)
- All modules inherit the `org.arig.robots.common-conventions` plugin (defined in `buildSrc/`) which sets Java 21 toolchain, Spring Boot BOM, Lombok, JUnit Jupiter, and WebFlux

### Deployment
Use `deploy.sh` (interactive, requires `fzf`) to build and SCP the executable jar + scripts to a robot over SSH (`{robot-name}.local`). The jar is named `{project}-robot-BUILD-SNAPSHOT-exec.jar`.

Use `robots-action.sh` to remotely trigger actions (`run`, `monitoring`, `poweroff`, `reboot`) on deployed robots via SSH.

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
./gradlew :eurobot-system-lib-parent:eurobot-system-lib-core:test

# Run a single test class
./gradlew :robot-system-lib-parent:robot-system-lib-core:test --tests "org.arig.robot.filters.common.ChangeFilterTest"

# Run pre-commit checks
pre-commit run --all-files
```

CI builds against **Java 21 and 23** using the Liberica JDK distribution.

Gradle parallel builds are enabled (`org.gradle.parallel=true`).

Pre-commit hooks enforce: no trailing whitespace, end-of-file newline, valid JSON/XML/YAML formatting (run `pre-commit run --all-files` to check locally).

## Architecture

This is a multi-robot Java monorepo for Eurobot competition robots, organized in layers:

```
robot-system-lib-parent/          # Core technical library (hardware-agnostic)
  robot-system-lib-core/       ← Core: filters (PID, ramp, average), motion, pathfinding,
                               │  strategy engine, communication (I2C, CAN, socket), servos
  robot-system-lib-raspi/         # Raspberry Pi bindings (Pi4J, I2C, CAN bus)
  robot-system-lib-bouchon/    ← Stub/mock implementations (no hardware needed)
  robot-system-lib-joycon/        # Joy-Con controller support

eurobot-system-lib-parent/        # Eurobot-competition-specific, shared across robots
  eurobot-system-lib-core/     ← Game logic, shared status model (EurobotStatus), pathfinding
  eurobot-system-lib-bouchon/     # Mock implementations for eurobot layer

nerell-parent/                 ← Main robot ("Nerell")
  nerell-common/               ← Shared game logic (strategy actions, services, Spring contexts)
  nerell-robot/                ← Spring Boot app → deploys on physical Raspberry Pi
  nerell-simulator/            ← Spring Boot app → runs locally with bouchon impls
  nerell-utils/                ← Spring Shell CLI for hardware diagnostics

pami-parent/                   ← PAMI robots (4 variants: triangle, carre, rond, star)
  pami-common/                 ← Shared PAMI logic
  pami-robot/                  ← Physical robot app
  pami-simulator/              ← Simulator app
  pami-utils/                  ← Spring Shell CLI for diagnostics
```

`odin-parent` and `tinker-parent` are commented out in `settings.gradle.kts` (inactive).

### Robot–Simulator duality

Every robot has two Spring Boot apps with the same business logic:
- `*-robot`: uses `robot-system-lib-raspi` (Pi4J GPIO, CAN bus)
- `*-simulator`: uses `*-bouchon` modules (no-op/simulated hardware)

The `-common` module contains all game logic; `-robot`/`-simulator` only wire up different Spring beans.

## Key Conventions

- **All modules** apply the `org.arig.robots.common-conventions` plugin (Spring Boot BOM 3.5, Java 21 toolchain, JUnit Jupiter, WebFlux, Lombok).
- **Group/version**: `org.arig.robot` / `BUILD-SNAPSHOT` everywhere.
- **Bouchon** = stub/mock for hardware components. Naming: `*Bouchon` suffix for classes, `*-bouchon` for modules.
- **French naming**: domain objects, packages, and variable names frequently use French (e.g., `capteurs` = sensors, `bras` = arm, `gradin` = bleacher, `ordonanceur` = scheduler/orchestrator).
- **Lombok**: `@Slf4j`, `@Data`, `@Getter/@Setter`, `@Accessors(fluent = true)` are standard. Fluent accessors mean getters/setters have no `get`/`set` prefix.
- **Constants classes**: named `*Constantes*` or `*Config` in the `constants/` package.
- **Deployable JARs**: `*-robot` modules produce an `-exec` classifier JAR via `BootJar`. The plain `jar` task uses `""` classifier to avoid conflicts.
- **Dependency versions**: managed via `gradle/libs.versions.toml` (version catalog) + Spring Boot BOM.

### Lombok
All classes use Lombok extensively. Prefer `@Slf4j`, `@Data`, `@Getter`, `@Setter`. Models use `@Accessors(fluent = true)` — accessor methods have **no `get`/`set` prefix** (e.g., `rs.mainRobot()` not `rs.getMainRobot()`).

### Strategy / Action Pattern
Robot behaviour is driven by a list of `Action` beans managed by `StrategyManager`. To add a game action:
1. Extend `AbstractNerellAction` (or `AbstractPamiAction`) — which extends `AbstractAction implements Action`
2. Place in `strategy/actions/active/` (enabled) or `strategy/actions/disabled/` (disabled)
3. Implement `name()`, `order()`, `isValid()`, `execute()`, and `entryPoint()`
4. Register as a Spring bean (via `@Component` or in the `*StrategyContext` config class)

`StrategyManager.execute()` sorts valid actions by `order()` descending and picks the highest priority one each cycle.

Concrete actions extend `AbstractAction` (or specific robot one like `AbstractNerellAction` / `AbstractNerellMacroAction`). `StrategyManager` picks the highest-priority valid non-blocked action each cycle.
### Spring Configuration Pattern
Config is split into multiple `@Configuration` classes per concern:
- `*Context` — core beans (robot status, config)
- `*ServicesContext` — service beans
- `*StrategyContext` — action/strategy beans
- `*SchedulerContext` — scheduled task configuration
- `*WebAppContext` — REST controller configuration

### Robot status model

`AbstractRobotStatus` → `EurobotStatus` → `NerellRobotStatus` / `PamiRobotStatus`. Uses Lombok `@Data @Accessors(fluent = true)` throughout — access fields as methods (`status.team()`, not `status.getTeam()`).

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

```bash
./deploy.sh          # interactive: select robots + whether to deploy utils shell
./robots-action.sh   # interactive: run/stop/reboot/poweroff selected robots via SSH
./getLogs.sh         # fetch logs from robots
```

Use `deploy.sh` (interactive, requires `fzf`) to build and SCP the executable jar + scripts to a robot over SSH (`{robot-name}.local`). The jar is named `{project}-robot-BUILD-SNAPSHOT-exec.jar`.

Use `robots-action.sh` to remotely trigger actions (`run`, `monitoring`, `poweroff`, `reboot`) on deployed robots via SSH.

Robots are addressed as `{name}.local` over SSH (mDNS). Robots: `nerell`, `pami-triangle`, `pami-carre`, `pami-rond`, `pami-star`. Requires `fzf` for interactive selection.

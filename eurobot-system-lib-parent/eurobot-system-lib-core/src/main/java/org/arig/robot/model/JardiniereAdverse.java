package org.arig.robot.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

@Slf4j
@Getter
@Setter
@Accessors(fluent = true)
@RequiredArgsConstructor
public class JardiniereAdverse implements Serializable {

    private final String name;

    private boolean done = false;
    private boolean blocked = false;
    private boolean contacted = false;

    private long validTime = 0;

    public void blocked(boolean blocked) {
        log.warn("[rs] La {} sud n'est pas accessible", name);
        this.blocked = blocked;
    }

    public void contacted(boolean contacted) {
        log.info("[rs] L'adversaire a été à sa {}", name);
        this.contacted = contacted;
    }

    public boolean volable() {
        return contacted && !blocked && !done && validTime < System.currentTimeMillis() - 2000;
    }
}

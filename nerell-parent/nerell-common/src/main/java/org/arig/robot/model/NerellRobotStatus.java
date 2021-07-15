package org.arig.robot.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Data
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper = true)
public class NerellRobotStatus extends EurobotStatus {

    public NerellRobotStatus() {
        super(true);
    }

    private boolean etalonageBaliseOk = false;

    @Setter(AccessLevel.NONE)
    private boolean baliseEnabled = false;

    public void enableBalise() {
        log.info("[RS] activation de la balise");
        baliseEnabled = true;
    }

    public void disableBalise() {
        log.info("[RS] désactivation de la balise");
        baliseEnabled = false;
    }

    @Setter(AccessLevel.NONE)
    private boolean pincesAvantEnabled = false;

    @Setter(AccessLevel.NONE)
    private boolean pincesAvantForceOn = false;

    public void enablePincesAvant() {
        enablePincesAvant(false);
    }

    public void enablePincesAvant(boolean modeForce) {
        log.info("[RS] activation des pinces avant (mode : {})", modeForce ? "FORCE" : "STANDARD");
        pincesAvantEnabled = true;
        pincesAvantForceOn = modeForce;
    }

    public void disablePincesAvant() {
        log.info("[RS] désactivation des pinces avant");
        pincesAvantEnabled = false;
    }

    /**
     * STATUT
     */

    // De gauche à droite, dans le sens du robot
    @Accessors(fluent = true)
    private ECouleur[] pincesAvant = new ECouleur[]{null, null, null, null};

    public void pinceAvant(int pos, ECouleur bouee) {
        log.info("[RS] pince avant {} {}", pos, bouee == null ? "null" : bouee.name());
        pincesAvant[pos] = bouee;
    }

    public void clearPincesAvant() {
        log.info("[RS] clear pince avant");
        Arrays.fill(pincesAvant, null);
    }

    public boolean pincesAvantEmpty() {
        return Arrays.stream(pincesAvant).noneMatch(Objects::nonNull);
    }

    @Override
    public Map<String, Object> gameStatus() {
        Map<String, Object> r = super.gameStatus();
        r.put("pincesAvant", pincesAvant);
        return r;
    }
}

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
public class OdinRobotStatus extends EurobotStatus {

    public OdinRobotStatus() {
        super(false);
    }

    @Setter(AccessLevel.NONE)
    private boolean pincesAvantEnabled = false;

    public void enablePincesAvant() {
        log.info("[RS] activation des pinces avant");
        pincesAvantEnabled = true;
    }

    public void disablePincesAvant() {
        log.info("[RS] désactivation des pinces avant");
        pincesAvantEnabled = false;
    }


    @Setter(AccessLevel.NONE)
    private boolean pincesArriereEnabled = false;

    public void enablePincesArriere() {
        log.info("[RS] activation des pinces arriere");
        pincesArriereEnabled = true;
    }

    public void disablePincesArriere() {
        log.info("[RS] désactivation des pinces arriere");
        pincesArriereEnabled = false;
    }

    /**
     * STATUT
     */

    @Accessors(fluent = true)
    private CouleurEchantillon[] pincesArriere = new CouleurEchantillon[]{null, null};

    // De gauche à droite, dans le sens du robot
    @Accessors(fluent = true)
    private CouleurEchantillon[] pincesAvant = new CouleurEchantillon[]{null, null};

    public void pinceArriere(int pos, CouleurEchantillon bouee) {
        log.info("[RS] pince arrière {} {}", pos, bouee == null ? "null" : bouee.name());
        pincesArriere[pos] = bouee;
    }

    public void pinceAvant(int pos, CouleurEchantillon bouee) {
        log.info("[RS] pince avant {} {}", pos, bouee == null ? "null" : bouee.name());
        pincesAvant[pos] = bouee;
    }

    public void clearPincesArriere() {
        log.info("[RS] clear pince arriere");
        Arrays.fill(pincesArriere, null);
    }

    public void clearPincesAvant() {
        log.info("[RS] clear pince avant");
        Arrays.fill(pincesAvant, null);
    }

    public boolean pincesArriereEmpty() {
        return Arrays.stream(pincesArriere).noneMatch(Objects::nonNull);
    }

    public boolean pincesAvantEmpty() {
        return Arrays.stream(pincesAvant).noneMatch(Objects::nonNull);
    }

    @Override
    public Map<String, Object> gameStatus() {
        Map<String, Object> r = super.gameStatus();
        r.put("pincesAvant", pincesAvant);
        r.put("pincesArriere", pincesArriere);
        return r;
    }
}

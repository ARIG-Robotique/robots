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

    @Override
    public void stopMatch() {
        super.stopMatch();
        this.disableBalise();
    }

    private boolean etalonageBaliseOk = false;

    @Setter(AccessLevel.NONE)
    private boolean pincesAvantEnabled = false;

    public void enablePincesAvant() {
        pincesAvantEnabled = true;
    }

    public void disablePincesAvant() {
        pincesAvantEnabled = false;
    }

    @Setter(AccessLevel.NONE)
    private boolean baliseEnabled = false;

    public void enableBalise() {
        baliseEnabled = true;
    }

    public void disableBalise() {
        baliseEnabled = false;
    }

    /**
     * STATUT
     */

    private boolean deposePartielleDone = false;

    // De gauche a droite, dans le sens du robot
    @Accessors(fluent = true)
    private ECouleurBouee[] pincesArriere = new ECouleurBouee[]{null, null, null, null, null};

    // De gauche à droite, dans le sens du robot
    @Accessors(fluent = true)
    private ECouleurBouee[] pincesAvant = new ECouleurBouee[]{null, null, null, null};

    public void pinceArriere(int pos, ECouleurBouee bouee) {
        log.info("[RS] pince arrière {} {}", pos, bouee == null ? "null" : bouee.name());
        pincesArriere[pos] = bouee;
    }

    public void pinceAvant(int pos, ECouleurBouee bouee) {
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

    public boolean deposeArriereGrandChenalPossible() {
        return grandChenaux.chenalRouge.size() < 5 || grandChenaux.chenalVert.size() < 5;
    }

    @Override
    public Map<String, Object> gameStatus() {
        Map<String, Object> r = super.gameStatus();
        r.put("pincesAvant", pincesAvant);
        r.put("pincesArriere", pincesArriere);
        return r;
    }
}

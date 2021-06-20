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

    private EOdinStrategy strategy = EOdinStrategy.BASIC_NORD;

    @Override
    public void stopMatch() {
        super.stopMatch();
    }

    public void setStrategy(int value) {
        switch (value) {
            case 0:
                strategy = EOdinStrategy.BASIC_NORD;
                break;
            case 1:
                strategy = EOdinStrategy.BASIC_SUD;
                break;
            case 2:
                strategy = EOdinStrategy.AGGRESSIVE;
                break;
            case 3:
                strategy = EOdinStrategy.FINALE;
                break;
            default:
                throw new IllegalArgumentException("Strategy invalide");
        }
    }

    private boolean doubleDepose;
    private boolean deposePartielle;

    @Setter(AccessLevel.NONE)
    private boolean pincesAvantEnabled = false;

    public void enablePincesAvant() {
        pincesAvantEnabled = true;
    }

    public void disablePincesAvant() {
        pincesAvantEnabled = false;
    }


    @Setter(AccessLevel.NONE)
    private boolean pincesArriereEnabled = false;

    public void enablePincesArriere() {
        pincesArriereEnabled = true;
    }

    public void disablePincesArriere() {
        pincesArriereEnabled = false;
    }

    /**
     * STATUT
     */

    @Accessors(fluent = true)
    private ECouleurBouee[] pincesArriere = new ECouleurBouee[]{null, null};

    // De gauche à droite, dans le sens du robot
    @Accessors(fluent = true)
    private ECouleurBouee[] pincesAvant = new ECouleurBouee[]{null, null};

    public void pinceArriere(int pos, ECouleurBouee bouee) {
        pincesArriere[pos] = bouee;
    }

    public void pinceAvant(int pos, ECouleurBouee bouee) {
        pincesAvant[pos] = bouee;
    }

    public void clearPincesArriere() {
        Arrays.fill(pincesArriere, null);
    }

    public void clearPincesAvant() {
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

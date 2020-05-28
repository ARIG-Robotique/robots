package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.arig.robot.model.ECouleurBouee;
import org.arig.robot.model.NerellStatus;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public abstract class AbstractPincesAvantService implements IPincesAvantService {

    public enum Side {
        LEFT, RIGHT
    }

    @Autowired
    private IIOService io;

    @Autowired
    private NerellStatus rs;

    @Autowired
    private ServosService servosService;

    private MutablePair<ECouleurBouee, ECouleurBouee> expected = new MutablePair<>();

    private boolean[] enabled = new boolean[]{true, true, true, true};

    private boolean[] previousState = new boolean[]{false, false, false, false};

    @Override
    public boolean deposeGrandChenal(ECouleurBouee couleurChenal) {

        return true;
    }

    @Override
    public boolean deposePetitPort() {
        servosService.ascenseurAvantBas(true);
        servosService.pincesAvantOuvert(true);
        rs.petitChenaux().addRouge(ArrayUtils.subarray(rs.pincesAvant(), 0, 2));
        rs.petitChenaux().addVert(ArrayUtils.subarray(rs.pincesAvant(), 2, 4));
        rs.clearPincesAvant();

        return true;
    }

    @Override
    public void finaliseDepose() {
        servosService.pincesAvantFerme(false);
    }

    @Override
    public void setExpected(Side cote, ECouleurBouee bouee, int pos) {
        // Dans cette implémentation pos ne sert a rien c'est normal.
        // C'est utilisé pour le pilotage des IOs en mode bouchon
        if (cote == Side.RIGHT) {
            expected.setRight(bouee);
        } else {
            expected.setLeft(bouee);
        }
    }

    @Override
    public void setEnabled(boolean ena1, boolean ena2, boolean ena3, boolean ena4) {
        enabled = new boolean[]{ena1, ena2, ena3, ena4};
    }

    @Override
    public void activate() {
        if (servosService.isMoustachesOuvert()) {
            servosService.moustachesFerme(true);
        }
        for (int i = 0; i < 4; i++) {
            if (rs.pincesAvant()[i] == null && enabled[i]) {
                servosService.pinceAvantOuvert(i, false);
            }
        }
        servosService.ascenseurAvantBas(true);
    }

    @Override
    public void disable() {
        clearExpected();

        for (int i = 0; i < 4; i++) {
            if (rs.pincesAvant()[i] == null) {
                servosService.pinceAvantFerme(i, false);
            }
        }
        servosService.ascenseurAvantRoulage(true);
    }

    /**
     * Prise automatique sur la table
     */
    @Override
    public void process() {
        final boolean[] newState = new boolean[]{
                io.presencePinceAvant1(),
                io.presencePinceAvant2(),
                io.presencePinceAvant3(),
                io.presencePinceAvant4()
        };

        for (int i = 0; i < newState.length; i++) {
            if (rs.pincesAvant()[i] == null && !previousState[i] && newState[i]) {
                servosService.pinceAvantPrise(i, false);
                registerBouee(i);
            }
        }

        previousState = newState;
    }

    private void clearExpected() {
        expected.setRight(null);
        expected.setLeft(null);
    }

    private void registerBouee(int index) {
        if (index < 2) {
            if (expected.getLeft() != null) {
                rs.pinceAvant(index, expected.getLeft());
                expected.setLeft(null);
            } else {
                rs.pinceAvant(index, ECouleurBouee.INCONNU);
            }
        } else {
            if (expected.getRight() != null) {
                rs.pinceAvant(index, expected.getRight());
                expected.setRight(null);
            } else {
                rs.pinceAvant(index, ECouleurBouee.INCONNU);
            }
        }
    }
}

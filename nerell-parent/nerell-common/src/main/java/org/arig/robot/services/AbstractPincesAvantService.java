package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.MutablePair;
import org.arig.robot.model.ECouleurBouee;
import org.arig.robot.model.RobotStatus;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public abstract class AbstractPincesAvantService implements IPincesAvantService {

    public enum Side {
        LEFT, RIGHT
    }

    @Autowired
    private IIOService io;

    @Autowired
    private RobotStatus rs;

    @Autowired
    private ServosService servosService;

    private MutablePair<ECouleurBouee, ECouleurBouee> expected = new MutablePair<>();

    private boolean[] previousState = new boolean[]{false, false, false, false};

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
    public void activate() {
        if (servosService.isMoustachesOuvert()) {
            servosService.moustachesFerme(true);
        }
        for (int i = 0; i < 4; i++) {
            if (rs.getPincesAvant()[i] == null) {
                servosService.pinceAvantOuvert(i, false);
            }
        }
        servosService.ascenseurAvantBas(true);
    }

    @Override
    public void disable() {
        clearExpected();

        for (int i = 0; i < 4; i++) {
            if (rs.getPincesAvant()[i] == null) {
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
            if (rs.getPincesAvant()[i] == null && !previousState[i] && newState[i]) {
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

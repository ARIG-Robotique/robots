package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.MutablePair;
import org.arig.robot.model.ECouleurBouee;
import org.arig.robot.model.RobotStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PincesAvantService {

    public static enum Side {
        RIGHT, LEFT
    }

    @Autowired
    private IIOService io;

    @Autowired
    private RobotStatus rs;

    @Autowired
    private ServosService servosService;

    private MutablePair<ECouleurBouee, ECouleurBouee> expected = new MutablePair<>();

    private boolean[] previousState = new boolean[]{false, false, false, false};

    public void setExpected(Side cote, ECouleurBouee bouee) {
        if (cote == Side.RIGHT) {
            expected.setRight(bouee);
        } else {
            expected.setLeft(bouee);
        }
    }

    public void clearExpected() {
        expected.setRight(null);
        expected.setLeft(null);
    }

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

    public void disable() {
        servosService.ascenseurAvantOuvertureMoustache(true);
    }

    /**
     * Prise automatique sur la table
     */
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

    private void registerBouee(int index) {
        if (index < 2) {
            if (expected.getLeft() != null) {
                rs.setPinceAvant(index, expected.getLeft());
                expected.setLeft(null);
            } else {
                rs.setPinceAvant(index, ECouleurBouee.INCONNU);
            }
        } else {
            if (expected.getRight() != null) {
                rs.setPinceAvant(index, expected.getRight());
                expected.setRight(null);
            } else {
                rs.setPinceAvant(index, ECouleurBouee.INCONNU);
            }
        }
    }

}

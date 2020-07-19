package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.arig.robot.model.ECouleurBouee;
import org.arig.robot.model.NerellRobotStatus;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public abstract class AbstractPincesAvantService implements IPincesAvantService {

    public enum Side {
        LEFT, RIGHT
    }

    @Autowired
    private IIOService io;

    @Autowired
    private NerellRobotStatus rs;

    @Autowired
    private ServosService servosService;

    private MutablePair<ECouleurBouee, ECouleurBouee> expected = new MutablePair<>();

    private boolean[] enabled = new boolean[]{true, true, true, true};

    private boolean[] previousStateLat = new boolean[]{false, false, false, false};
    private boolean[] previousStateSup = new boolean[]{false, false, false, false};

    @Override
    public boolean deposeGrandChenal(ECouleurBouee couleurChenal) {
        servosService.ascenseurAvantRoulage(true);
        servosService.pincesAvantOuvert(true);
        if (couleurChenal == ECouleurBouee.ROUGE) {
            rs.grandChenaux().addRouge(rs.pincesAvant());
        } else {
            rs.grandChenaux().addVert(rs.pincesAvant());
        }
        rs.clearPincesAvant();

        return true;
    }

    @Override
    public boolean deposePetitPort() {
        servosService.ascenseurAvantRoulage(true);
        servosService.pincesAvantOuvert(true);
        rs.petitChenaux().addRouge(ArrayUtils.subarray(rs.pincesAvant(), 0, 2));
        rs.petitChenaux().addVert(ArrayUtils.subarray(rs.pincesAvant(), 2, 4));
        rs.clearPincesAvant();

        return true;
    }

    @Override
    public void finaliseDepose() {
        boolean hasBouee = false;
        for (int i = 0; i < 4; i++) {
            if (rs.pincesAvant()[i] == null) {
                // TODO Gestion de la fermeture avec le capteur supérieur
                servosService.pinceAvantFerme(i, false);
            } else {
                hasBouee = true;
            }
        }

        if (hasBouee) {
            servosService.ascenseurAvantOuvertureMoustache(false);
        } else {
            servosService.ascenseurAvantBas(false);
        }
    }

    @Override
    public void setExpected(Side cote, ECouleurBouee bouee, int pinceNumber) {
        // Dans cette implémentation pinceNumber ne sert a rien c'est normal.
        // C'est utilisé pour le pilotage des IOs en mode bouchon
        if (cote == Side.RIGHT) {
            expected.setRight(bouee);
        } else {
            expected.setLeft(bouee);
        }
    }

    @Override
    public void setEnabled(boolean pince1, boolean pince2, boolean pince3, boolean pince4) {
        enabled = new boolean[]{pince1, pince2, pince3, pince4};
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
        setEnabled(true, true, true, true);

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
        final boolean[] newStateLat = new boolean[]{
                io.presencePinceAvantLat1(),
                io.presencePinceAvantLat2(),
                io.presencePinceAvantLat3(),
                io.presencePinceAvantLat4()
        };

        final boolean[] newStateSup = new boolean[]{
                io.presencePinceAvantSup1(),
                io.presencePinceAvantSup2(),
                io.presencePinceAvantSup3(),
                io.presencePinceAvantSup4()
        };

        for (int i = 0; i < newStateLat.length; i++) {
            if (rs.pincesAvant()[i] == null && ((!previousStateLat[i] && newStateLat[i]) || (!previousStateSup[i] && newStateSup[i]))) {
                servosService.pinceAvantPrise(i, false);
                registerBouee(i);
            }
        }

        previousStateLat = newStateLat;
        previousStateSup = newStateSup;
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

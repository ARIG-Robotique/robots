package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.arig.robot.constants.IConstantesServosNerell;
import org.arig.robot.model.ECouleurBouee;
import org.arig.robot.model.NerellRobotStatus;
import org.arig.robot.system.pathfinding.IPathFinder;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;

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

    @Autowired
    private IPathFinder pathFinder;

    private MutablePair<ECouleurBouee, ECouleurBouee> expected = new MutablePair<>();

    private boolean[] enabled = new boolean[]{true, true, true, true};

    private boolean[] previousStateLat = new boolean[]{false, false, false, false};

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

        pathFinder.setObstacles(new ArrayList<>());

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
    public void deposeGrandPort() {
        servosService.ascenseurAvantRoulage(true);
        servosService.pincesAvantOuvert(true);

        for (ECouleurBouee eCouleurBouee : rs.pincesAvant()) {
            if (eCouleurBouee != null) {
                rs.grandPort().add(eCouleurBouee);
            }
        }
        rs.clearPincesAvant();
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
        // on bloque les pinces qu'il faut
        final boolean[] firstState = getNewState();
        for (int i = 0; i < firstState.length; i++) {
            if (firstState[i]) {
                servosService.pinceAvantPrise(i, false);
            }
        }

        // après attente on regarde ce qui est vraiment là
        ThreadUtils.sleep(IConstantesServosNerell.WAIT_PINCE_AVANT);
        final boolean[] newState = getNewState();

        for (int i = 0; i < newState.length; i++) {
            if (!newState[i] && firstState[i]) {
                // finalement y'a rien...
                log.info("Perte d'une bouée en position {}", i);
                servosService.pinceAvantFerme(i, false);
            } else if (newState[i] && !previousStateLat[i]) {
                registerBouee(i);
            }
        }

        previousStateLat = newState;
    }

    private boolean[] getNewState() {
        return new boolean[]{
                io.presencePinceAvantLat1() || io.presencePinceAvantSup1(),
                io.presencePinceAvantLat2() || io.presencePinceAvantSup2(),
                io.presencePinceAvantLat3() || io.presencePinceAvantSup3(),
                io.presencePinceAvantLat4() || io.presencePinceAvantSup4()
        };
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

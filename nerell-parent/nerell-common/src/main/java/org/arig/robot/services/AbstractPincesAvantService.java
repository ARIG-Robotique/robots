package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.arig.robot.constants.IConstantesNerellConfig;
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

    private boolean[] previousState = new boolean[]{false, false, false, false};

    @Override
    public boolean deposeGrandChenal(final ECouleurBouee couleurChenal, final boolean partielle) {
        servosService.ascenseurAvantRoulage(true);

        if (partielle) {
            for (int i = 0; i < 4; i++) {
                final ECouleurBouee couleurPince = rs.pincesAvant()[i];
                if (couleurPince == couleurChenal || couleurPince == ECouleurBouee.INCONNU) {
                    disablePompe(i);

                    if (couleurChenal == ECouleurBouee.ROUGE) {
                        rs.grandChenaux().addRouge(couleurPince);
                    } else {
                        rs.grandChenaux().addVert(couleurPince);
                    }
                    rs.pinceAvant(i, null);
                }
            }

            ThreadUtils.sleep(IConstantesNerellConfig.WAIT_EXPIRATION);

        } else {
            for (int i = 0; i < 4; i++) {
                disablePompe(i);
            }
            ThreadUtils.sleep(IConstantesNerellConfig.WAIT_EXPIRATION);

            if (couleurChenal == ECouleurBouee.ROUGE) {
                rs.grandChenaux().addRouge(rs.pincesAvant());
            } else {
                rs.grandChenaux().addVert(rs.pincesAvant());
            }
            rs.clearPincesAvant();
        }

        pathFinder.setObstacles(new ArrayList<>());

        return true;
    }

    @Override
    public boolean deposePetitPort() {
        servosService.ascenseurAvantRoulage(true);
        for (int i = 0; i < 4; i++) {
            disablePompe(i);
        }
        ThreadUtils.sleep(IConstantesNerellConfig.WAIT_EXPIRATION);

        rs.petitChenaux().addRouge(ArrayUtils.subarray(rs.pincesAvant(), 0, 2));
        rs.petitChenaux().addVert(ArrayUtils.subarray(rs.pincesAvant(), 2, 4));
        rs.clearPincesAvant();

        return true;
    }

    @Override
    public void deposeGrandPort() {
        servosService.ascenseurAvantRoulage(true);
        for (int i = 0; i < 4; i++) {
            disablePompe(i);
        }
        ThreadUtils.sleep(IConstantesNerellConfig.WAIT_EXPIRATION);

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
            if (rs.pincesAvant()[i] != null) {
                hasBouee = true;
                break;
            }
        }

        if (hasBouee) {
            servosService.ascenseurAvantOuvertureMoustache(false);
        } else {
            servosService.ascenseurAvantBas(false);
        }
    }

    @Override
    public void activate() {
        if (servosService.isMoustachesOuvert()) {
            servosService.moustachesFerme(true);
        }

        servosService.ascenseurAvantBas(true);
    }

    @Override
    public void disable() {
        servosService.ascenseurAvantRoulage(true);
    }

    /**
     * Prise automatique sur la table
     */
    @Override
    public void process() {
        // on bloque les pinces qu'il faut
        final boolean[] firstState = getNewState();

        if (io.presencePinceAvantSup1()) {
            io.enablePompe1();
        }
        if (io.presencePinceAvantSup2()) {
            io.enablePompe2();
        }
        if (io.presencePinceAvantSup3()) {
            io.enablePompe3();
        }
        if (io.presencePinceAvantSup4()) {
            io.enablePompe4();
        }

        // après attente on regarde ce qui est vraiment là
        ThreadUtils.sleep(IConstantesNerellConfig.WAIT_ASPIRATION);
        final boolean[] newState = getNewState();

        for (int i = 0; i < newState.length; i++) {
            if (!newState[i] && firstState[i]) {
                // finalement y'a rien...
                log.info("Perte d'une bouée en position {}", i);

                disablePompe(i);
            } else if (newState[i] && !previousState[i]) {
                registerBouee(i);
            }
        }

        previousState = newState;
    }

    private void disablePompe(int i) {
        // @formatter:off
        switch (i) {
            case 0: io.disablePompe1(); break;
            case 1: io.disablePompe2(); break;
            case 2: io.disablePompe3(); break;
            case 3: io.disablePompe4(); break;
        }
        // @formatter:on
    }

    private boolean[] getNewState() {
        return new boolean[]{
                io.presenceVentouse1() || io.presencePinceAvantSup1(),
                io.presenceVentouse2() || io.presencePinceAvantSup2(),
                io.presenceVentouse3() || io.presencePinceAvantSup3(),
                io.presenceVentouse4() || io.presencePinceAvantSup4()
        };
    }

    // FIXME gestion de la lecture couleur dans un autre thread ?
    private void registerBouee(int index) {
        ECouleurBouee couleurBouee = ECouleurBouee.INCONNU;

        // @formatter:off
        switch (index) {
            case 0: couleurBouee = io.couleurBouee1(); break;
            case 1: couleurBouee = io.couleurBouee2(); break;
            case 2: couleurBouee = io.couleurBouee3(); break;
            case 3: couleurBouee = io.couleurBouee4(); break;
        }
        // @formatter:on

        rs.pinceAvant(index, couleurBouee);
    }
}

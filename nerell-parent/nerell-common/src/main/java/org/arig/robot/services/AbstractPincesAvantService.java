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
        rs.disablePincesAvant();

        servosService.ascenseursAvantBas(true);

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
        rs.disablePincesAvant();

        servosService.ascenseursAvantBas(true);
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
        rs.disablePincesAvant();

        servosService.ascenseursAvantBas(true);
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
        rs.enablePincesAvant();
    }

    /**
     * Sur activation en descends tous les ascenseurs vides
     */
    @Override
    public void activate() {
        if (servosService.isMoustachesOuvert()) {
            servosService.moustachesFerme(true);
        }

        previousState = getNewState();

        for (int i = 0; i < 4; i++) {
            if (previousState[i]) {
                servosService.ascenseurAvantHaut(i, false);
            } else {
                servosService.ascenseurAvantBas(i, false);
            }
        }
    }

    /**
     * Prise automatique sur la table
     */
    @Override
    public void process() {
        // première lecteur des capteurs
        final boolean[] firstState = getNewState();
        final boolean hasSome = firstState[0] || firstState[1] || firstState[2] || firstState[3];

        if (hasSome) {
            // aspiration des nouvelles bouéés
            for (int i = 0; i < 4; i++) {
                if (!previousState[i] && firstState[i]) {
                    enablePompe(i);
                }
            }

            // après attente on regarde ce qui est vraiment là
            ThreadUtils.sleep(IConstantesNerellConfig.WAIT_ASPIRATION);

            final boolean[] newState = getNewState();
            final boolean needLed = newState[0] && !previousState[0] ||
                    newState[1] && !previousState[1] ||
                    newState[2] && !previousState[2] ||
                    newState[3] && !previousState[3];

            // allume la led une fois pour toutes
            if (needLed) {
                io.enableLedCapteurCouleur();
                ThreadUtils.sleep(IConstantesNerellConfig.WAIT_LED);
            }

            for (int i = 0; i < 4; i++) {
                if (!newState[i] && firstState[i]) {
                    // perte pendant la prise
                    log.info("Perte d'une bouée en position {}", i);
                    disablePompe(i);

                } else if (previousState[i] && !newState[i]) {
                    // perte pendant deux executions
                    log.info("Perte d'une bouée en position {}", i);
                    disablePompe(i);
                    rs.pinceAvant(0, null);

                } else if (!previousState[i] && newState[i]) {
                    // nouvelles bouée
                    registerBouee(i);
                    servosService.ascenseurAvantHaut(i, false);
                }
            }

            io.disableLedCapteurCouleur();

            previousState = newState;

        } else {
            for (int i = 0; i < 4; i++) {
                if (previousState[i]) {
                    // perte pendant deux executions
                    log.info("Perte d'une bouée en position {}", i);
                    disablePompe(i);
                    rs.pinceAvant(0, null);
                }
            }

            previousState = firstState;
        }
    }

    private void enablePompe(int i) {
        // @formatter:off
        switch (i) {
            case 0: io.enablePompe1(); break;
            case 1: io.enablePompe2(); break;
            case 2: io.enablePompe3(); break;
            case 3: io.enablePompe4(); break;
        }
        // @formatter:on
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

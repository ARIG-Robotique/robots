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
import java.util.Arrays;
import java.util.Objects;

@Slf4j
public abstract class AbstractNerellPincesAvantService implements INerellPincesAvantService {

    @Autowired
    private INerellIOService io;

    @Autowired
    private NerellRobotStatus rs;

    @Autowired
    private NerellServosService servosService;

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
                        rs.deposeGrandChenalRouge(couleurPince);
                    } else {
                        rs.deposeGrandChenalVert(couleurPince);
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
                rs.deposeGrandChenalRouge(rs.pincesAvant());
            } else {
                rs.deposeGrandChenalVert(rs.pincesAvant());
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

        rs.deposePetitChenalRouge(ArrayUtils.subarray(rs.pincesAvant(), 0, 2));
        rs.deposePetitChenalVert(ArrayUtils.subarray(rs.pincesAvant(), 2, 4));
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
                rs.deposeGrandPort(eCouleurBouee);
            }
        }
        rs.clearPincesAvant();
    }

    @Override
    public void finaliseDepose() {
        rs.enablePincesAvant();
    }

    /**
     * Sur activation on descends tous les ascenseurs vides
     */
    @Override
    public void activate() {
        if (servosService.isMoustachesOuvert()) {
            servosService.moustachesFerme(true);
        }

        // Aspiration des nouvelles bouéés
        for (int i = 0; i < 4; i++) {
            enablePompe(i);
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
    public void processBouee() {
        // première lecteur des capteurs
        final boolean[] firstState = getNewState();
        final boolean hasSome = firstState[0] || firstState[1] || firstState[2] || firstState[3];

        if (hasSome) {
            final boolean[] newState = getNewState();
            for (int i = 0; i < 4; i++) {
                if (!newState[i] && firstState[i]) {
                    // perte pendant la prise
                    log.warn("Perte d'une bouée en position {}", i);
                    disablePompe(i);

                } else if (previousState[i] && !newState[i]) {
                    // perte pendant deux executions
                    log.warn("Perte d'une bouée en position {}", i);
                    disablePompe(i);
                    rs.pinceAvant(0, null);

                } else if (!previousState[i] && newState[i]) {
                    // nouvelles bouée
                    registerBouee(i, ECouleurBouee.INCONNU);
                    servosService.ascenseurAvantHaut(i, false);
                }
            }

            previousState = newState;

        } else {
            for (int i = 0; i < 4; i++) {
                if (previousState[i]) {
                    // perte pendant deux executions
                    log.warn("Perte d'une bouée en position {}", i);
                    disablePompe(i);
                    rs.pinceAvant(0, null);
                }
            }

            previousState = firstState;
        }
    }

    @Override
    public void processCouleurBouee() {
        if (Arrays.stream(rs.pincesAvant()).filter(c -> c == ECouleurBouee.INCONNU).count() > 0) {
            io.enableLedCapteurCouleur();
            ThreadUtils.sleep(IConstantesNerellConfig.WAIT_LED);

            for (int i = 0; i < 4; i++) {
                if (rs.pincesAvant()[i] == ECouleurBouee.INCONNU) {
                    registerBouee(i, getCouleurBouee(i));
                }
            }
            io.disableLedCapteurCouleur();
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
                io.presenceVentouse1(),
                io.presenceVentouse2(),
                io.presenceVentouse3(),
                io.presenceVentouse4()
        };
    }

    private void registerBouee(int index, ECouleurBouee couleurBouee) {
        rs.pinceAvant(index, couleurBouee);
    }

    private ECouleurBouee getCouleurBouee(int index) {
        // @formatter:off
        switch (index) {
            case 0: return io.couleurBouee1();
            case 1: return io.couleurBouee2();
            case 2: return io.couleurBouee3();
            case 3: return io.couleurBouee4();
            default: return ECouleurBouee.INCONNU;
        }
        // @formatter:on
    }
}

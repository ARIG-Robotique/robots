package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.model.ECouleurBouee;
import org.arig.robot.model.NerellRobotStatus;
import org.arig.robot.system.pathfinding.IPathFinder;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.stream.Stream;

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

    @Autowired
    private RobotGroupService group;

    private MutablePair<ECouleurBouee, ECouleurBouee> expected = new MutablePair<>();

    private boolean[] previousState = new boolean[]{false, false, false, false};

    @Override
    public boolean deposeGrandChenal(final ECouleurBouee couleurChenal, final boolean partielle) {
        rs.disablePincesAvant();

        servosService.ascenseursAvantBas(true);

        if (partielle) {
            for (int i = 0; i < 4; i++) {
                final ECouleurBouee couleurPince = rs.pincesAvant()[i];
                if (couleurPince == couleurChenal || couleurPince == ECouleurBouee.INCONNU) {
                    releasePompe(i);

                    if (couleurChenal == ECouleurBouee.ROUGE) {
                        group.deposeGrandChenalRouge(couleurPince);
                    } else {
                        group.deposeGrandChenalVert(couleurPince);
                    }
                    rs.pinceAvant(i, null);
                }
            }

            ThreadUtils.sleep(IConstantesNerellConfig.WAIT_POMPES);

        } else {
            io.releaseAllPompes();
            ThreadUtils.sleep(IConstantesNerellConfig.WAIT_POMPES);

            if (couleurChenal == ECouleurBouee.ROUGE) {
                group.deposeGrandChenalRouge(rs.pincesAvant());
            } else {
                group.deposeGrandChenalVert(rs.pincesAvant());
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
        io.releaseAllPompes();
        ThreadUtils.sleep(IConstantesNerellConfig.WAIT_POMPES);

        group.deposePetitChenalRouge(ArrayUtils.subarray(rs.pincesAvant(), 0, 2));
        group.deposePetitChenalVert(ArrayUtils.subarray(rs.pincesAvant(), 2, 4));
        rs.clearPincesAvant();

        return true;
    }

    @Override
    public void deposeGrandPort() {
        rs.disablePincesAvant();

        servosService.ascenseursAvantBas(true);
        io.releaseAllPompes();
        ThreadUtils.sleep(IConstantesNerellConfig.WAIT_POMPES);

        group.deposeGrandPort(rs.pincesAvant());
        rs.clearPincesAvant();
    }

    /**
     * Sur activation on descends tous les ascenseurs vides
     */
    @Override
    public void activate() {
        if (servosService.isMoustachesOuvert()) {
            servosService.moustachesFerme(true);
        }

        // Aspiration des nouvelles bouées
        io.enableAllPompes();

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
    public boolean processBouee() {
        // première lecture des capteurs
        final boolean[] newState = getNewState();
        boolean needReadColor = false;
        for (int i = 0; i < 4; i++) {
            if (previousState[i] && !newState[i]) {
                // perte bouée
                registerBouee(i, null);
                servosService.ascenseurAvantBas(i, false);

            } else if (!previousState[i] && newState[i]) {
                // nouvelle bouée
                registerBouee(i, getExpected(i));
                servosService.ascenseurAvantHaut(i, false);
                needReadColor = true;
            }
        }

        previousState = newState;
        return needReadColor;
    }

    @Override
    public void processCouleurBouee() {
        if (Stream.of(rs.pincesAvant()).filter(c -> c == ECouleurBouee.INCONNU).count() == 0) {
            // Pas d'inconnu, pas de lecture
            return;
        }

        io.enableLedCapteurCouleur();
        ThreadUtils.sleep(IConstantesNerellConfig.WAIT_LED);

        for (int i = 0; i < 4; i++) {
            if (rs.pincesAvant()[i] == ECouleurBouee.INCONNU) {
                registerBouee(i, getCouleurBouee(i));
            }
        }
        io.disableLedCapteurCouleur();
    }

    @Override
    public void setExpected(ECouleurBouee gauche, ECouleurBouee droite) {
        expected.setRight(gauche);
        expected.setLeft(droite);
    }

    private ECouleurBouee getExpected(int index) {
        ECouleurBouee couleur = ECouleurBouee.INCONNU;
        if (index < 2) {
            if (expected.getLeft() != null) {
                couleur = expected.getLeft();
                expected.setLeft(null);
            }
        } else {
            if (expected.getRight() != null) {
                couleur = expected.getRight();
                expected.setRight(null);
            }
        }
        return couleur;
    }

    private void releasePompe(int i) {
        // @formatter:off
        switch (i) {
            case 0: io.releasePompe1(); break;
            case 1: io.releasePompe2(); break;
            case 2: io.releasePompe3(); break;
            case 3: io.releasePompe4(); break;
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

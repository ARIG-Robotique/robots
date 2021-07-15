package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.INerellConstantesConfig;
import org.arig.robot.model.ECouleur;
import org.arig.robot.model.NerellRobotStatus;
import org.arig.robot.system.pathfinding.IPathFinder;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.beans.factory.annotation.Autowired;

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

    private ECouleur expected = null;

    private boolean[] previousState = new boolean[]{false, false, false, false};

    /**
     * Sur activation on descends tous les ascenseurs vides
     */
    @Override
    public void activate() {
        // Aspiration des nouvelles bouées
        if (!rs.pincesAvantForceOn()) {
            io.enableAllPompes();
        }

        previousState = getNewState();

        for (int i = 0; i < 4; i++) {
            if (previousState[i]) {

            } else {
                if (rs.pincesAvantForceOn()) {
                    forceOnPompe(i);
                }
            }
        }
    }

    /**
     * Sur désactivation on éteins toutes les pompes vides
     */
    @Override
    public void deactivate() {
        for (int i = 0; i < 4; i++) {
            if (rs.pincesAvant()[i] == null) {
                releasePompe(i);
            }
        }
    }

    /**
     * Prise automatique sur la table
     */
    @Override
    public boolean process() {
        final boolean[] newState = getNewState();
        boolean needReadColor = false;
        for (int i = 0; i < 4; i++) {
            if (previousState[i] && !newState[i]) {
                // perte
                register(i, null);

            } else if (!previousState[i] && newState[i]) {
                // nouvelle
                register(i, getExpected());
                needReadColor = true;
            }
        }

        previousState = newState;
        return needReadColor;
    }

    @Override
    public void processCouleur() {
        if (Stream.of(rs.pincesAvant()).noneMatch(c -> c == ECouleur.INCONNU)) {
            // Pas d'inconnu, pas de lecture
            return;
        }

        io.enableLedCapteurCouleur();
        ThreadUtils.sleep(INerellConstantesConfig.WAIT_LED);

        for (int i = 0; i < 4; i++) {
            if (rs.pincesAvant()[i] == ECouleur.INCONNU) {
                register(i, getCouleur(i));
            }
        }
        io.disableLedCapteurCouleur();
    }

    @Override
    public void setExpected(ECouleur expected) {
        this.expected = expected;
    }

    protected ECouleur getExpected() {
        ECouleur couleur = ECouleur.INCONNU;
        if (expected != null) {
            couleur = expected;
            expected = null;
        }
        return couleur;
    }

    private void forceOnPompe(int i) {
        // @formatter:off
        switch (i) {
            case 0: io.enableForcePompe1(); break;
            case 1: io.enableForcePompe2(); break;
            case 2: io.enableForcePompe3(); break;
            case 3: io.enableForcePompe4(); break;
        }
        // @formatter:on
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

    private void register(int index, ECouleur couleur) {
        rs.pinceAvant(index, couleur);
    }

    private ECouleur getCouleur(int index) {
        // @formatter:off
        switch (index) {
            case 0: return io.couleurAvant1();
            case 1: return io.couleurAvant2();
            case 2: return io.couleurAvant3();
            case 3: return io.couleurAvant4();
            default: return ECouleur.INCONNU;
        }
        // @formatter:on
    }
}

package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.NerellConstantesConfig;
import org.arig.robot.model.CouleurEchantillon;
import org.arig.robot.model.NerellRobotStatus;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.stream.Stream;

@Slf4j
public abstract class AbstractNerellPincesAvantService implements NerellPincesAvantService {

    @Autowired
    private NerellIOService io;

    @Autowired
    private NerellRobotStatus rs;

    private CouleurEchantillon expected = null;

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
        if (Stream.of(rs.pincesAvant()).noneMatch(c -> c == CouleurEchantillon.ROCHER)) {
            // Pas d'inconnu, pas de lecture
            return;
        }

        io.enableLedCapteurCouleur();
        ThreadUtils.sleep(NerellConstantesConfig.WAIT_LED);

        for (int i = 0; i < 4; i++) {
            if (rs.pincesAvant()[i] == CouleurEchantillon.ROCHER) {
                register(i, getCouleur(i));
            }
        }
        io.disableLedCapteurCouleur();
    }

    @Override
    public void setExpected(CouleurEchantillon expected) {
        this.expected = expected;
    }

    protected CouleurEchantillon getExpected() {
        CouleurEchantillon couleur = CouleurEchantillon.ROCHER;
        if (expected != null) {
            couleur = expected;
            expected = null;
        }
        return couleur;
    }

    private void forceOnPompe(int i) {
        // @formatter:off
        switch (i) {
            case 0: io.enableForcePompeVentouseHaut(); break;
            case 1: io.enableForcePompeVentouseBas(); break;
        }
        // @formatter:on
    }

    private void releasePompe(int i) {
        // @formatter:off
        switch (i) {
            case 0: io.releasePompeVentouseBas(); break;
            case 1: io.releasePompeVentouseHaut(); break;
        }
        // @formatter:on
    }

    private boolean[] getNewState() {
        return new boolean[]{
                io.presenceVentouseBas(),
                io.presenceVentouseHaut(),
        };
    }

    private void register(int index, CouleurEchantillon couleur) {
        rs.pinceAvant(index, couleur);
    }

    private CouleurEchantillon getCouleur(int index) {
        // @formatter:off
        switch (index) {
            case 0: return io.couleurVentouseBas();
            case 1: return io.couleurVentouseHaut();
            default: return CouleurEchantillon.ROCHER;
        }
        // @formatter:on
    }
}

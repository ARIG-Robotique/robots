package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.OdinConstantesConfig;
import org.arig.robot.model.CouleurEchantillon;
import org.arig.robot.model.OdinRobotStatus;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.stream.Stream;

@Slf4j
public abstract class AbstractOdinPincesService implements OdinPincesService {

    @Autowired
    protected OdinIOService io;

    @Autowired
    protected OdinRobotStatus rs;

    @Autowired
    protected OdinServosService servos;

    @Autowired
    private RobotGroupService group;

    private CouleurEchantillon expected = null;

    private boolean[] previousState = new boolean[]{false, false};

    protected abstract void disableServicePinces();

    protected abstract void releasePompes();

    protected abstract void enablePompes();

    protected abstract CouleurEchantillon[] currentState();

    protected abstract void clearPinces();

    protected abstract boolean[] getNewState();

    protected abstract void register(int index, CouleurEchantillon couleur);

    protected abstract CouleurEchantillon getCouleur(int index);

    @Override
    public void setExpected(CouleurEchantillon expected, int indexPince) {
        this.expected = expected;
    }

    /**
     * Sur activation on actives toutes les pompes
     */
    @Override
    public void activate() {
        // Aspiration des nouvelles bouées
        enablePompes();
        previousState = getNewState();
    }

    /**
     * Sur désactivation on désactive les pompes sans bouées
     */
    @Override
    public void deactivate() {
        for (int i = 0; i < 2; i++) {
            if (currentState()[i] == null) {
                releasePompe(i == 0, i == 1);
            }
        }
    }

    /**
     * Prise automatique sur la table
     */
    @Override
    public boolean process() {
        // première lecture des capteurs
        final boolean[] newState = getNewState();
        boolean needReadColor = false;
        for (int i = 0; i < 2; i++) {
            if (previousState[i] && !newState[i]) {
                // perte bouée
                register(i, null);

            } else if (!previousState[i] && newState[i]) {
                // nouvelle bouée
                register(i, getExpected());
                needReadColor = true;
            }
        }

        previousState = newState;
        return needReadColor;
    }

    @Override
    public void processCouleur() {
        if (Stream.of(currentState()).noneMatch(c -> c == CouleurEchantillon.ROCHER)) {
            // Pas d'inconnu, pas de lecture
            return;
        }

        io.enableLedCapteurCouleur();
        ThreadUtils.sleep(OdinConstantesConfig.WAIT_LED);

        for (int i = 0; i < 2; i++) {
            if (currentState()[i] == CouleurEchantillon.ROCHER) {
                register(i, getCouleur(i));
            }
        }
        io.disableLedCapteurCouleur();
    }

    protected CouleurEchantillon getExpected() {
        CouleurEchantillon couleur = CouleurEchantillon.ROCHER;
        if (expected != null) {
            couleur = expected;
            expected = null;
        }
        return couleur;
    }
}

package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IOdinConstantesConfig;
import org.arig.robot.model.ECouleur;
import org.arig.robot.model.OdinRobotStatus;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.stream.Stream;

@Slf4j
public abstract class AbstractOdinPincesService implements IOdinPincesService {

    @Autowired
    protected IOdinIOService io;

    @Autowired
    protected OdinRobotStatus rs;

    @Autowired
    protected OdinServosService servos;

    @Autowired
    private RobotGroupService group;

    private ECouleur expected = null;

    private boolean[] previousState = new boolean[]{false, false};

    protected abstract void disableServicePinces();

    protected abstract void releasePompes();

    protected abstract void enablePompes();

    protected abstract ECouleur[] currentState();

    protected abstract void clearPinces();

    protected abstract boolean[] getNewState();

    protected abstract void register(int index, ECouleur couleur);

    protected abstract ECouleur getCouleur(int index);

    @Override
    public void setExpected(ECouleur expected, int indexPince) {
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
        if (Stream.of(currentState()).noneMatch(c -> c == ECouleur.INCONNU)) {
            // Pas d'inconnu, pas de lecture
            return;
        }

        io.enableLedCapteurCouleur();
        ThreadUtils.sleep(IOdinConstantesConfig.WAIT_LED);

        for (int i = 0; i < 2; i++) {
            if (currentState()[i] == ECouleur.INCONNU) {
                register(i, getCouleur(i));
            }
        }
        io.disableLedCapteurCouleur();
    }

    protected ECouleur getExpected() {
        ECouleur couleur = ECouleur.INCONNU;
        if (expected != null) {
            couleur = expected;
            expected = null;
        }
        return couleur;
    }
}

package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesOdinConfig;
import org.arig.robot.model.ECouleurBouee;
import org.arig.robot.model.OdinRobotStatus;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public abstract class AbstractOdinPincesService implements IOdinPincesService {

    @Autowired
    protected IOdinIOService io;

    @Autowired
    protected OdinRobotStatus rs;

    @Autowired
    private RobotGroupService group;

    private boolean[] previousState = new boolean[]{false, false};

    protected abstract void disableServicePinces();
    protected abstract void releasePompes();
    protected abstract void enablePompes();
    protected abstract ECouleurBouee[] bouees();
    protected abstract void clearPinces();
    protected abstract boolean[] getNewState();
    protected abstract void registerBouee(int index, ECouleurBouee couleurBouee);
    protected abstract ECouleurBouee getCouleurBouee(int index);

    @Override
    public void deposeGrandPort() {
        disableServicePinces();

        releasePompes();
        ThreadUtils.sleep(IConstantesOdinConfig.WAIT_EXPIRATION);

        group.deposeGrandPort(bouees());
        clearPinces();
    }

    /**
     * Sur activation on descends tous les ascenseurs vides
     */
    @Override
    public void activate() {
        // Aspiration des nouvelles bouées
        enablePompes();
        previousState = getNewState();
    }

    /**
     * Prise automatique sur la table
     */
    @Override
    public boolean processBouee() {
        // première lecture des capteurs
        final boolean[] newState = getNewState();
        boolean needReadColor = false;
        for (int i = 0; i < 2; i++) {
            if (previousState[i] && !newState[i]) {
                // perte bouée
                registerBouee(i, null);

            } else if (!previousState[i] && newState[i]) {
                // nouvelles bouée
                registerBouee(i, ECouleurBouee.INCONNU);
                needReadColor = true;
            }
        }

        previousState = newState;
        return needReadColor;
    }

    @Override
    public void processCouleurBouee() {
        io.enableLedCapteurCouleur();
        ThreadUtils.sleep(IConstantesOdinConfig.WAIT_LED);

        for (int i = 0; i < 2; i++) {
            if (bouees()[i] == ECouleurBouee.INCONNU) {
                registerBouee(i, getCouleurBouee(i));
            }
        }
        io.disableLedCapteurCouleur();
    }
}
package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesOdinConfig;
import org.arig.robot.model.ECouleurBouee;
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
    private RobotGroupService group;

    @Autowired
    private EurobotTableService eurobotTableService;

    private boolean[] previousState = new boolean[]{false, false};

    protected abstract void disableServicePinces();
    protected abstract void releasePompes();
    protected abstract void enablePompes();
    protected abstract ECouleurBouee[] bouees();
    protected abstract void clearPinces();
    protected abstract boolean[] getNewState();
    protected abstract void registerBouee(int index, ECouleurBouee couleurBouee);
    protected abstract ECouleurBouee getCouleurBouee(int index);

    private void depose() {
        disableServicePinces();
        releasePompes();
        ThreadUtils.sleep(IConstantesOdinConfig.WAIT_POMPES);
    }

    @Override
    public void deposeGrandPort() {
        depose();
        group.deposeGrandPort(bouees());
        clearPinces();
    }

    @Override
    public void deposeGrandChenalRouge() {
        depose();
        group.deposeGrandChenalRouge(bouees());
        clearPinces();
    }

    @Override
    public void deposeGrandChenalVert() {
        depose();
        group.deposeGrandChenalVert(bouees());
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
                // nouvelle bouée
                final ECouleurBouee couleur;
                final Integer bouee = eurobotTableService.estimateBoueeFromPosition();
                if (bouee != null) {
                    couleur = rs.boueeCouleur(bouee);
                    group.boueePrise(bouee);
                } else {
                    couleur = ECouleurBouee.INCONNU;
                }

                registerBouee(i, couleur);
                needReadColor = true;
            }
        }

        previousState = newState;
        return needReadColor;
    }

    @Override
    public void processCouleurBouee() {
        if (Stream.of(bouees()).filter(c -> c == ECouleurBouee.INCONNU).count() == 0) {
            // Pas d'inconnu, pas de lecture
            return;
        }

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

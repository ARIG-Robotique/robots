package org.arig.robot.services;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IOdinConstantesConfig;
import org.arig.robot.model.ECouleurBouee;
import org.arig.robot.model.GrandChenaux;
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

    private ECouleurBouee expected = null;

    private boolean[] previousState = new boolean[]{false, false};

    protected abstract void disableServicePinces();

    protected abstract void releasePompes();

    protected abstract void releasePompe(boolean gauche, boolean droite);

    protected abstract void enablePompes();

    protected abstract ECouleurBouee[] bouees();

    protected abstract void clearPinces();

    protected abstract boolean[] getNewState();

    protected abstract void registerBouee(int index, ECouleurBouee couleurBouee);

    protected abstract void pousser(boolean gauche, boolean droite);

    protected abstract ECouleurBouee getCouleurBouee(int index);

    @Override
    public void setExpected(ECouleurBouee expected, int pinceNumber) {
        this.expected = expected;
    }

    private void depose() {
        disableServicePinces();
        releasePompes();
        ThreadUtils.sleep(IOdinConstantesConfig.WAIT_POMPES);
    }

    private void depose(boolean gauche, boolean droite) {
        disableServicePinces();
        if (gauche && droite) {
            releasePompes();
        } else if (gauche) {
            releasePompe(true, false);
        } else if (droite) {
            releasePompe(false, true);
        }
        ThreadUtils.sleep(IOdinConstantesConfig.WAIT_POMPES);
    }

    @Override
    public void deposeGrandPort() {
        depose();
        group.deposeGrandPort(bouees());
        clearPinces();
    }

    @Override
    public void deposeFondGrandChenalRouge() {
        depose();
        group.deposeGrandChenalRouge(GrandChenaux.Line.C, bouees());
        clearPinces();
    }

    @Override
    public void deposeFondGrandChenalVert() {
        depose();
        group.deposeGrandChenalVert(GrandChenaux.Line.C, bouees());
        clearPinces();
    }

    @Override
    public void deposeGrandChenal(ECouleurBouee chenal, GrandChenaux.Line line, int idxGauche, int idxDroite) {
        disableServicePinces();

        depose(idxGauche != -1, idxDroite != -1);
        pousser(idxGauche != -1, idxDroite != -1);

        if (chenal == ECouleurBouee.VERT) {
            if (idxGauche != -1) {
                group.deposeGrandChenalVert(line, idxGauche, bouees()[0]);
                bouees()[0] = null;
            }
            if (idxDroite != -1) {
                group.deposeGrandChenalVert(line, idxDroite, bouees()[1]);
                bouees()[1] = null;
            }
        } else {
            if (idxGauche != -1) {
                group.deposeGrandChenalRouge(line, idxGauche, bouees()[0]);
                bouees()[0] = null;
            }
            if (idxDroite != -1) {
                group.deposeGrandChenalRouge(line, idxDroite, bouees()[1]);
                bouees()[1] = null;
            }
        }
    }

    @Override
    public void deposePetitChenal(ECouleurBouee chenal) {
        disableServicePinces();

        ECouleurBouee[] bouees = bouees();

        depose(bouees[0] != null, bouees[1] != null);
        pousser(bouees[0] != null, bouees[1] != null);

        if (chenal == ECouleurBouee.VERT) {
            group.deposePetitChenalVert(bouees);
        } else {
            group.deposePetitChenalRouge(bouees);
        }
        clearPinces();
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
            if (bouees()[i] == null) {
                releasePompe(i == 0, i == 1);
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
        for (int i = 0; i < 2; i++) {
            if (previousState[i] && !newState[i]) {
                // perte bouée
                registerBouee(i, null);

            } else if (!previousState[i] && newState[i]) {
                // nouvelle bouée
                registerBouee(i, getExpected());
                needReadColor = true;
            }
        }

        previousState = newState;
        return needReadColor;
    }

    @Override
    public void processCouleurBouee() {
        if (Stream.of(bouees()).noneMatch(c -> c == ECouleurBouee.INCONNU)) {
            // Pas d'inconnu, pas de lecture
            return;
        }

        io.enableLedCapteurCouleur();
        ThreadUtils.sleep(IOdinConstantesConfig.WAIT_LED);

        for (int i = 0; i < 2; i++) {
            if (bouees()[i] == ECouleurBouee.INCONNU) {
                registerBouee(i, getCouleurBouee(i));
            }
        }
        io.disableLedCapteurCouleur();
    }

    protected ECouleurBouee getExpected() {
        ECouleurBouee couleur = ECouleurBouee.INCONNU;
        if (expected != null) {
            couleur = expected;
            expected = null;
        }
        return couleur;
    }
}

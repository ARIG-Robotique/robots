package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesOdinConfig;
import org.arig.robot.model.ECouleurBouee;
import org.arig.robot.model.OdinRobotStatus;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;
import java.util.stream.Stream;

@Slf4j
public abstract class AbstractOdinPincesAvantService implements IOdinPincesAvantService {

    @Autowired
    private IOdinIOService io;

    @Autowired
    private OdinRobotStatus rs;

    @Autowired
    private RobotGroupService group;

    private boolean[] previousState = new boolean[]{false, false};

    @Override
    public void deposeGrandPort() {
        rs.disablePincesAvant();

        io.releaseAllPompe();
        ThreadUtils.sleep(IConstantesOdinConfig.WAIT_EXPIRATION);

        group.deposeGrandPort(Stream.of(rs.pincesAvant()).filter(Objects::nonNull).toArray(ECouleurBouee[]::new));
        rs.clearPincesAvant();
    }

    /**
     * Sur activation on descends tous les ascenseurs vides
     */
    @Override
    public void activate() {
        // Aspiration des nouvelles bouées
        io.enableAllPompe();
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
            if (rs.pincesAvant()[i] == ECouleurBouee.INCONNU) {
                registerBouee(i, getCouleurBouee(i));
            }
        }
        io.disableLedCapteurCouleur();
    }

    private boolean[] getNewState() {
        return new boolean[]{
                io.presenceVentouseAvantGauche(),
                io.presenceVentouseAvantDroit()
        };
    }

    private void registerBouee(int index, ECouleurBouee couleurBouee) {
        rs.pinceAvant(index, couleurBouee);
    }

    private ECouleurBouee getCouleurBouee(int index) {
        // @formatter:off
        switch (index) {
            case 0: return io.couleurBoueeAvantGauche();
            case 1: return io.couleurBoueeAvantDroit();
            default: return ECouleurBouee.INCONNU;
        }
        // @formatter:on
    }
}

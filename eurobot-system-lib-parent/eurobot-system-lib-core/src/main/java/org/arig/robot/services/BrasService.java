package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.arig.robot.model.EurobotStatus;
import org.arig.robot.model.RobotConfig;
import org.arig.robot.model.TypePlante;
import org.arig.robot.model.bras.OptionBras;
import org.arig.robot.model.bras.PositionBras;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * API de haut niveau pour les bras
 */
@Slf4j
@Service
public class BrasService extends BrasServiceInternal {

    private final RobotConfig config;
    private final CommonRobotIOService io;

    public BrasService(final AbstractCommonRobotServosService servos,
                       final ThreadPoolExecutor executor,
                       final RobotConfig config,
                       final EurobotStatus rs,
                       final CommonRobotIOService io) {
        super(servos, executor, rs);
        this.config = config;
        this.io = io;
    }

    /**
     * Activation de la ventouse bas + lecture couleur si besoin
     */
    public boolean waitEnablePresencePinceAvantGauche(TypePlante couleur) {
        log.info("Prise d'un {} dans la prince bras avant gauche", couleur);

        boolean ok = ThreadUtils.waitUntil(io::pinceAvantGauche, config.i2cReadTimeMs(), config.timeoutPompe());

        if (ok) {
            return true;
        } else {
            log.warn("Pas de présence pince avant gauche");
            return false;
        }
    }

    public boolean waitEnablePresencePinceAvantCentre(TypePlante couleur) {
        log.info("Prise d'un {} dans la prince bras avant centre", couleur);

        boolean ok = ThreadUtils.waitUntil(io::pinceAvantCentre, config.i2cReadTimeMs(), config.timeoutPompe());

        if (ok) {
            return true;
        } else {
            log.warn("Pas de présence pince avant centre");
            return false;
        }
    }

    public boolean waitEnablePresencePinceAvantDroite(TypePlante couleur) {
        log.info("Prise d'un {} dans la prince bras avant droit", couleur);

        boolean ok = ThreadUtils.waitUntil(io::pinceAvantDroite, config.i2cReadTimeMs(), config.timeoutPompe());

        if (ok) {
            return true;
        } else {
            log.warn("Pas de présence pince avant droite");
            return false;
        }
    }

    public boolean waitEnablePresencePinceArriereGauche(TypePlante couleur) {
        log.info("Prise d'un {} dans la prince bras arriere gauche", couleur);

        boolean ok = ThreadUtils.waitUntil(io::pinceArriereGauche, config.i2cReadTimeMs(), config.timeoutPompe());

        if (ok) {
            return true;
        } else {
            log.warn("Pas de présence pince arriere gauche");
            return false;
        }
    }

    public boolean waitEnablePresencePinceArriereCentre(TypePlante couleur) {
        log.info("Prise d'un {} dans la prince bras arriere centre", couleur);

        boolean ok = ThreadUtils.waitUntil(io::pinceArriereCentre, config.i2cReadTimeMs(), config.timeoutPompe());

        if (ok) {
            return true;
        } else {
            log.warn("Pas de présence pince arriere centre");
            return false;
        }
    }

    public boolean waitEnablePresencePinceArriereDroite(TypePlante couleur) {
        log.info("Prise d'un {} dans la prince bras arriere droit", couleur);

        boolean ok = ThreadUtils.waitUntil(io::pinceArriereDroite, config.i2cReadTimeMs(), config.timeoutPompe());

        if (ok) {
            return true;
        } else {
            log.warn("Pas de présence pince arriere droite");
            return false;
        }
    }

}

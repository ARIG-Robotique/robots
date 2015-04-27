package org.arig.eurobot.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.arig.eurobot.constants.IConstantesRobot;
import org.arig.eurobot.model.RobotStatus;
import org.arig.eurobot.services.ServosServices;
import org.arig.robot.system.MouvementManager;
import org.arig.robot.vo.CommandeRobot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Created by gdepuille on 23/12/14.
 */
@Slf4j
@Component
@Profile("raspi")
public class Schedulers {

    @Autowired
    private RobotStatus rs;

    @Autowired
    private MouvementManager mouvementManager;

    @Autowired
    private ServosServices servosServices;

    @Scheduled(fixedRate = (long) IConstantesRobot.asservTimeMs)
    public void robotManagerTask() {
        if (rs.isAsservEnabled()) {
            mouvementManager.process();
        } else {
            mouvementManager.stop();
        }
    }

    @Scheduled(fixedDelay = 100L)
    public void ascenseurTask() {
        if (rs.isMatchEnabled()) {
            try {
                servosServices.checkAscenseur();
            } catch (InterruptedException e) {
                log.error("Erreur lors du contrôle pour l'ascenseur {}", e.toString());
            }
        }
    }

    @Scheduled(fixedDelay = 100L)
    public void produitGaucheTask() {
        if (rs.isMatchEnabled()) {
            try {
                servosServices.checkProduitGauche();
            } catch (InterruptedException e) {
                log.error("Erreur lors du contrôle pour le produit gauche {}", e.toString());
            }
        }
    }

    @Scheduled(fixedDelay = 100L)
    public void produitDroitTask() {
        if (rs.isMatchEnabled()) {
            try {
                servosServices.checkProduitDroit();
            } catch (InterruptedException e) {
                log.error("Erreur lors du contrôle pour le produit droit  {}", e.toString());
            }
        }
    }
}

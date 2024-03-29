package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.AbstractRobotStatus;
import org.arig.robot.model.CommandeRobot;
import org.arig.robot.model.enums.TypeCalage;
import org.arig.robot.model.enums.TypeConsigne;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CalageService {

    @Autowired
    private TrajectoryManager trajectoryManager;

    @Autowired
    private IOService ioService;

    @Autowired
    private AbstractRobotStatus rs;

    @Autowired
    private CommandeRobot cmdRobot;

    public void process() {
        if (!rs.calage().isEmpty()) {
            boolean doneAvant = false;
            boolean doneArriere = false;
            boolean donePriseProduitAvant = false;
            boolean donePrisePotArriere = false;

            if (!rs.matchEnabled() && !ioService.auOk()) {
                doneAvant = doneArriere = donePriseProduitAvant = donePrisePotArriere = true;
            } else {
                if (rs.calage().size() == 1 && rs.calage().contains(TypeCalage.PRISE_PRODUIT_AVANT)) {
                    donePriseProduitAvant = ioService.calagePriseProduitAvant();
                }
                if (rs.calage().size() == 1 && rs.calage().contains(TypeCalage.PRISE_POT_ARRIERE)) {
                    donePrisePotArriere = ioService.calagePrisePotArriere();
                }

                if (cmdRobot.isType(TypeConsigne.DIST) && cmdRobot.isType(TypeConsigne.ANGLE)) {
                    // Calage bordure avec les deux asservissement. Un seul des capteurs suffit
                    if (rs.calage().contains(TypeCalage.AVANT)) {
                        doneAvant = ioService.calageAvantDroit() || ioService.calageAvantGauche();
                    }
                    if (rs.calage().contains(TypeCalage.ARRIERE)) {
                        doneArriere = ioService.calageArriereDroit() || ioService.calageArriereGauche();
                    }

                } else {
                    // Calage bordure avec un autre asservissement, ou uniquement la distance ou l'angle.
                    // Les deux capteurs sont utilisés pour le calage.
                    if (rs.calage().contains(TypeCalage.AVANT)) {
                        doneAvant = ioService.calageAvantDroit() && ioService.calageAvantGauche();
                    }
                    if (rs.calage().contains(TypeCalage.ARRIERE)) {
                        doneArriere = ioService.calageArriereDroit() && ioService.calageArriereGauche();
                    }
                }
            }

            if (doneAvant || doneArriere || donePriseProduitAvant || donePrisePotArriere) {
                if (doneAvant) {
                    log.info("Callage complet : {}", TypeCalage.AVANT);
                    rs.calageCompleted().add(TypeCalage.AVANT);
                }
                if (doneArriere) {
                    log.info("Callage complet : {}", TypeCalage.ARRIERE);
                    rs.calageCompleted().add(TypeCalage.ARRIERE);
                }
                if (donePriseProduitAvant) {
                    log.info("Callage complet : {}", TypeCalage.PRISE_PRODUIT_AVANT);
                    rs.calageCompleted().add(TypeCalage.PRISE_PRODUIT_AVANT);
                }
                if (donePrisePotArriere) {
                    log.info("Callage complet : {}", TypeCalage.PRISE_POT_ARRIERE);
                    rs.calageCompleted().add(TypeCalage.PRISE_POT_ARRIERE);
                }

                trajectoryManager.calageBordureDone(); // TODO Rename
            }
        }
    }
}

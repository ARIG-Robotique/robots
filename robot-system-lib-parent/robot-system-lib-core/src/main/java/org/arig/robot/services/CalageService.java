package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.arig.robot.model.AbstractRobotStatus;
import org.arig.robot.model.CommandeRobot;
import org.arig.robot.model.enums.TypeCalage;
import org.arig.robot.model.enums.TypeConsigne;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

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

    private StopWatch stopWatchPriseProduit = new StopWatch();

    public void process() {
        if (!rs.calage().isEmpty()) {
            boolean doneAvant = false;
            boolean doneArriere = false;
            boolean doneTempo = false;
            boolean donePriseProduitAvant = false;
            boolean donePriseProduitArriere = false;
            boolean doneElectroaimant = false;
            boolean doneInductif = false;

            if (!rs.matchEnabled() && !ioService.auOk()) {
                doneAvant = doneArriere = doneTempo = donePriseProduitAvant = donePriseProduitArriere = doneElectroaimant = doneInductif = true;
            } else {
                if (rs.calage().contains(TypeCalage.TEMPO)) {
                    doneTempo = rs.callageTime() < System.currentTimeMillis();
                }

                if (rs.calage().contains(TypeCalage.PRISE_PRODUIT_AVANT)) {
                    donePriseProduitAvant = ioService.calagePriseProduitAvant(2);
                }
                if (rs.calage().contains(TypeCalage.PRISE_PRODUIT_ARRIERE)) {
                    donePriseProduitArriere = ioService.calagePriseProduitArriere(2);
                }
                if (rs.calage().contains(TypeCalage.PRISE_ELECTROAIMANT)) {
                    doneElectroaimant = ioService.calageElectroaimant(2);
                }
                if (rs.calage().contains(TypeCalage.CONTACT_INDUCTIF)) {
                    doneInductif = ioService.calageElectroaimant(1);
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

            // le callage sur produit se fait avec un retard
            if (donePriseProduitAvant || donePriseProduitArriere) {
                if (stopWatchPriseProduit.isStopped()) {
                    stopWatchPriseProduit.start();
                } else if (stopWatchPriseProduit.getTime(TimeUnit.MILLISECONDS) >= 400) {
                    stopWatchPriseProduit.reset();

                    if (donePriseProduitAvant) {
                        log.info("Callage complet : {}", TypeCalage.PRISE_PRODUIT_AVANT);
                        rs.calageCompleted().add(TypeCalage.PRISE_PRODUIT_AVANT);
                    }
                    if (donePriseProduitArriere) {
                        log.info("Callage complet : {}", TypeCalage.PRISE_PRODUIT_ARRIERE);
                        rs.calageCompleted().add(TypeCalage.PRISE_PRODUIT_ARRIERE);
                    }

                    trajectoryManager.calageBordureDone();
                }
            } else if (stopWatchPriseProduit.isStarted()) {
                stopWatchPriseProduit.reset();
            }

            if (doneAvant || doneArriere || doneTempo || doneElectroaimant || doneInductif) {
                if (doneAvant) {
                    log.info("Callage complet : {}", TypeCalage.AVANT);
                    rs.calageCompleted().add(TypeCalage.AVANT);
                }
                if (doneArriere) {
                    log.info("Callage complet : {}", TypeCalage.ARRIERE);
                    rs.calageCompleted().add(TypeCalage.ARRIERE);
                }
                if (doneTempo) {
                    log.info("Callage complet : {}", TypeCalage.TEMPO);
                    rs.calageCompleted().add(TypeCalage.TEMPO);
                }
                if (doneElectroaimant) {
                    log.info("Callage complet : {}", TypeCalage.PRISE_ELECTROAIMANT);
                    rs.calageCompleted().add(TypeCalage.PRISE_ELECTROAIMANT);
                }
                if (doneInductif) {
                    log.info("Callage complet : {}", TypeCalage.CONTACT_INDUCTIF);
                    rs.calageCompleted().add(TypeCalage.CONTACT_INDUCTIF);
                }

                trajectoryManager.calageBordureDone(); // TODO Rename
            }
        }
    }
}

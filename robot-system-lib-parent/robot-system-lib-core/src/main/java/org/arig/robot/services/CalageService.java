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
            boolean doneArriere = false;
            boolean doneAvant = false;
            boolean doneLatteralDroit = false;
            boolean donePriseEchantillon = false;

            if (!rs.matchEnabled() && !ioService.auOk()) {
                doneArriere = doneAvant = doneLatteralDroit = true;
            } else {
                if (rs.calage().size() == 1 && rs.calage().contains(TypeCalage.LATTERAL_DROIT)) {
                    doneLatteralDroit = ioService.calageLatteralDroit();
                }
                if (rs.calage().size() == 1 && rs.calage().contains(TypeCalage.PRISE_ECHANTILLON)) {
                    donePriseEchantillon = ioService.calagePriseEchantillon();
                }

                if (cmdRobot.isType(TypeConsigne.DIST) && cmdRobot.isType(TypeConsigne.ANGLE)) {
                    // Calage bordure avec les deux asservissement. Un seul des capteurs suffit
                    if (rs.calage().contains(TypeCalage.ARRIERE)) {
                        doneArriere = ioService.calageArriereDroit() || ioService.calageArriereGauche();
                    }
                    if (rs.calage().contains(TypeCalage.AVANT)) {
                        doneAvant = ioService.calageAvantDroit() || ioService.calageAvantGauche();
                    }

                } else {
                    // Calage bordure avec les un autre asservissement, ou uniquement la distance ou l'angle.
                    // Les deux capteurs sont utilisés pour le calage.
                    if (rs.calage().contains(TypeCalage.ARRIERE)) {
                        doneArriere = ioService.calageArriereDroit() && ioService.calageArriereGauche();
                    }
                    if (rs.calage().contains(TypeCalage.AVANT)) {
                        doneAvant = ioService.calageAvantDroit() && ioService.calageAvantGauche();
                    }
                }
            }

            if (doneAvant || doneArriere || doneLatteralDroit || donePriseEchantillon) {
                trajectoryManager.calageBordureDone();
            }
        }
    }
}

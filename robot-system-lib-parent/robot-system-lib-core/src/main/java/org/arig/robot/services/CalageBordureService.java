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
public class CalageBordureService {

    @Autowired
    private TrajectoryManager trajectoryManager;

    @Autowired
    private IOService ioService;

    @Autowired
    private AbstractRobotStatus rs;

    @Autowired
    private CommandeRobot cmdRobot;

    public void process() {
        if (!rs.calageBordure().isEmpty()) {
            boolean doneArriere = false;
            boolean doneAvant = false;
            boolean doneCustom = false;

            if (!rs.matchEnabled() && !ioService.auOk()) {
                doneArriere = doneAvant = doneCustom = true;
            } else if (cmdRobot.isType(TypeConsigne.DIST) && cmdRobot.isType(TypeConsigne.ANGLE)) {
                // Calage bordure avec les deux asservissement. Un seul des capteurs suffit
                if (rs.calageBordure().contains(TypeCalage.ARRIERE)) {
                    doneArriere = ioService.calageBordureArriereDroit() || ioService.calageBordureArriereGauche();
                }
                if (rs.calageBordure().contains(TypeCalage.AVANT)) {
                    doneAvant = ioService.calageBordureAvantDroit() || ioService.calageBordureAvantGauche();
                }
                if (rs.calageBordure().contains(TypeCalage.CUSTOM)) {
                    doneCustom = ioService.calageBordureCustomDroit() || ioService.calageBordureCustomGauche();
                }
            } else {
                // Calage bordure avec les un autre asservissement, ou uniquement la distance ou l'angel.
                // Les deux capteurs sont utilisés pour le calage.
                if (rs.calageBordure().contains(TypeCalage.ARRIERE)) {
                    doneArriere = ioService.calageBordureArriereDroit() && ioService.calageBordureArriereGauche();
                }
                if (rs.calageBordure().contains(TypeCalage.AVANT)) {
                    doneAvant = ioService.calageBordureAvantDroit() && ioService.calageBordureAvantGauche();
                }
                if (rs.calageBordure().contains(TypeCalage.CUSTOM)) {
                    doneCustom = ioService.calageBordureCustomDroit() && ioService.calageBordureCustomGauche();
                }
            }

            if (doneAvant || doneArriere || doneCustom) {
                trajectoryManager.calageBordureDone();
                rs.disableCalageBordure();
            }
        }
    }
}

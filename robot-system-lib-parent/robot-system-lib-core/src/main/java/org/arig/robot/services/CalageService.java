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
            boolean doneAvantBas = false;
            boolean doneAvantHaut = false;
            boolean doneLatteralDroit = false;
            boolean donePriseEchantillon = false;
            boolean doneVentouseBas = false;

            if (!rs.matchEnabled() && !ioService.auOk()) {
                doneArriere = doneAvantBas = doneAvantHaut = doneLatteralDroit = donePriseEchantillon = doneVentouseBas = true;
            } else {
                if (rs.calage().size() == 1 && rs.calage().contains(TypeCalage.LATTERAL_DROIT)) {
                    doneLatteralDroit = ioService.calageLatteralDroit();
                }
                if (rs.calage().size() == 1 && rs.calage().contains(TypeCalage.PRISE_ECHANTILLON)) {
                    donePriseEchantillon = ioService.calagePriseEchantillon();
                }
                if (rs.calage().contains(TypeCalage.VENTOUSE_BAS)) {
                    doneVentouseBas = ioService.calageVentouseBas();
                }

                if (cmdRobot.isType(TypeConsigne.DIST) && cmdRobot.isType(TypeConsigne.ANGLE)) {
                    // Calage bordure avec les deux asservissement. Un seul des capteurs suffit
                    if (rs.calage().contains(TypeCalage.ARRIERE)) {
                        doneArriere = ioService.calageArriereDroit() || ioService.calageArriereGauche();
                    }
                    if (rs.calage().contains(TypeCalage.AVANT_BAS)) {
                        doneAvantBas = ioService.calageAvantBasDroit() || ioService.calageAvantBasGauche();
                    }
                    if (rs.calage().contains(TypeCalage.AVANT_HAUT)) {
                        doneAvantHaut = ioService.calageAvantHautDroit() || ioService.calageAvantHautGauche();
                    }

                } else {
                    // Calage bordure avec les un autre asservissement, ou uniquement la distance ou l'angle.
                    // Les deux capteurs sont utilisés pour le calage.
                    if (rs.calage().contains(TypeCalage.ARRIERE)) {
                        doneArriere = ioService.calageArriereDroit() && ioService.calageArriereGauche();
                    }
                    if (rs.calage().contains(TypeCalage.AVANT_BAS)) {
                        doneAvantBas = ioService.calageAvantBasDroit() && ioService.calageAvantBasGauche();
                    }
                    if (rs.calage().contains(TypeCalage.AVANT_HAUT)) {
                        doneAvantHaut = ioService.calageAvantHautDroit() && ioService.calageAvantHautGauche();
                    }
                }
            }

            if (doneAvantBas || doneAvantHaut || doneArriere || doneLatteralDroit || donePriseEchantillon || doneVentouseBas) {
                if (doneAvantBas) {
                    rs.calageCompleted().add(TypeCalage.AVANT_BAS);
                }
                if (doneAvantHaut) {
                    rs.calageCompleted().add(TypeCalage.AVANT_HAUT);
                }
                if (doneArriere) {
                    rs.calageCompleted().add(TypeCalage.ARRIERE);
                }
                if (doneLatteralDroit) {
                    rs.calageCompleted().add(TypeCalage.LATTERAL_DROIT);
                }
                if (donePriseEchantillon) {
                    rs.calageCompleted().add(TypeCalage.PRISE_ECHANTILLON);
                }
                if (doneVentouseBas) {
                    rs.calageCompleted().add(TypeCalage.VENTOUSE_BAS);
                }

                trajectoryManager.calageBordureDone(); // TODO Rename
            }
        }
    }
}

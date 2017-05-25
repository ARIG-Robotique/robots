package org.arig.robot.strategy.actions.active;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.exception.RefreshPathFindingException;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.model.Team;
import org.arig.robot.services.IIOService;
import org.arig.robot.services.ServosService;
import org.arig.robot.strategy.AbstractAction;
import org.arig.robot.system.ITrajectoryManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TransfertRessourcesAction extends AbstractAction {

    @Autowired
    private ITrajectoryManager mv;

    @Autowired
    private RobotStatus rs;

    @Autowired
    private IIOService ioService;

    @Autowired
    private ServosService servosService;

    @Getter
    private boolean completed = false;

    @Override
    public String name() {
        return "Transfert des balles à Elfa";
    }

    @Override
    public int order() {
        return rs.isHasPetitesBalles() ? 100 : 150;
    }

    @Override
    public boolean isValid() {
        if (!isTimeValid()) {
            return false;
        }

        return ioService.presenceBallesAspiration() && (
                Team.BLEU == rs.getTeam() && rs.isModuleRecupere(10) ||
                        Team.JAUNE == rs.getTeam() && rs.isModuleRecupere(1) ||
                        rs.getElapsedTime() > 65000
        );
    }

    @Override
    public void execute() {
        try {
            rs.enableAvoidance();

            mv.setVitesse(IConstantesNerellConfig.vitessePath, IConstantesNerellConfig.vitesseOrientation);

            if (Team.JAUNE == rs.getTeam()) {
                mv.pathTo(320, 690);
                servosService.aspirationMax();

                if (rs.isHasPetitesBalles()) {
                    mv.gotoOrientationDeg(145);
                } else {
                    mv.gotoOrientationDeg(180 - 16);
                }

            } else {
                mv.pathTo(2650, 750);
                mv.gotoOrientationDeg(-90);

                if (rs.isHasPetitesBalles()) {
                    mv.avanceMM(130);

                    servosService.aspirationMax();
                    mv.gotoOrientationDeg(-155);

                } else {
                    mv.avanceMM(90);

                    servosService.aspirationMax();
                    mv.gotoOrientationDeg(180);
                }
            }

            Thread.sleep(1500);

            servosService.aspirationTransfert();
            servosService.waitAspiration();

            servosService.aspirationStop();
            Thread.sleep(2000);

            servosService.aspirationFerme();
            servosService.waitAspiration();

            if (Team.BLEU == rs.getTeam()) {
                mv.gotoOrientationDeg(90);
                mv.avanceMM(90);
            }

            rs.addTransfertElfa();

        } catch (InterruptedException | NoPathFoundException | AvoidingException | RefreshPathFindingException e) {
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
            updateValidTime(IConstantesNerellConfig.invalidActionTimeSecond);
        }
    }
}

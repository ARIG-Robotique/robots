package org.arig.robot.strategy.actions.temp;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.model.Team;
import org.arig.robot.services.IIOService;
import org.arig.robot.strategy.AbstractAction;
import org.arig.robot.system.ITrajectoryManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author gdepuille on 06/11/16.
 */
@Slf4j
@Component
public class BalladeSurTableTestAction extends AbstractAction {

    @Autowired
    private ITrajectoryManager mv;

    @Autowired
    private RobotStatus rs;

    @Autowired
    private IIOService ioService;

    @Getter
    private boolean completed = false; // Jamais terminé quoi qu'il se passe

    @Override
    public String name() {
        return "Ballade sur table de test";
    }

    @Override
    public int order() {
        return 1000;
    }

    @Override
    public boolean isValid() {
        return (
                Team.JAUNE == rs.getTeam() &&
                        rs.isModuleRecupere(5) &&
                        rs.isModuleRecupere(2) &&
                        rs.isCratereZoneDepartJauneRecupere() &&
                        rs.getNbTransfertsElfa() > 0 &&
                        !ioService.presencePinceCentre() &&
                        !ioService.presencePinceDroite()
        ) || (
                Team.BLEU == rs.getTeam() &&
                        rs.isModuleRecupere(6) &&
                        rs.isModuleRecupere(9) &&
                        rs.isCratereZoneDepartBleuRecupere() &&
                        rs.getNbTransfertsElfa() > 0 &&
                        !ioService.presencePinceCentre() &&
                        !ioService.presencePinceDroite()
        );
    }

    @Override
    public void execute() {
        try {
            rs.enableAvoidance();
            mv.setVitesse(IConstantesNerellConfig.vitessePath, IConstantesNerellConfig.vitesseOrientation);

            mv.pathTo(1500, 700);
            mv.pathTo(500, 1000);
            mv.pathTo(2500, 1000);

        } catch (NoPathFoundException | AvoidingException e) {
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
        }
    }
}

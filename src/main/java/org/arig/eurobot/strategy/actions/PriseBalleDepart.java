package org.arig.eurobot.strategy.actions;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.arig.eurobot.constants.IConstantesServos;
import org.arig.eurobot.model.RobotStatus;
import org.arig.eurobot.model.Team;
import org.arig.eurobot.services.IOService;
import org.arig.eurobot.services.ServosService;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.exception.ObstacleFoundException;
import org.arig.robot.strategy.IAction;
import org.arig.robot.system.MouvementManager;
import org.arig.robot.system.servos.SD21Servos;
import org.arig.robot.vo.Position;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Created by gdepuille on 11/05/15.
 */
@Slf4j
@Component
public class PriseBalleDepart implements IAction {

    @Autowired
    private MouvementManager mv;

    @Autowired
    private IOService ioService;

    @Autowired
    private SD21Servos servos;

    @Autowired
    private ServosService servosService;

    @Autowired
    private RobotStatus rs;

    @Getter
    private boolean completed = false;

    @Override
    public String name() {
        return "Prise balle zone départ action";
    }

    @Override
    public int order() {
        return 800;
    }

    @Override
    public boolean isValid() {
        return rs.getNbPied() == 0 && rs.getIndexZoneDeposeSallePrincipale() == 0
                && !ioService.piedDroit() && !ioService.piedGauche();
    }

    @Override
    public void execute() {
        try {
            mv.setVitesse(400, 800);
            if (rs.getTeam() == Team.JAUNE) {
                mv.pathTo(1000, 500);
                servos.setPositionAndWait(IConstantesServos.ASCENSEUR, IConstantesServos.ASCENSEUR_BAS);
                servos.setPosition(IConstantesServos.PINCE, IConstantesServos.PINCE_OUVERTE);
                servosService.leveGobelets();
                mv.gotoOrientationDeg(-90);
                mv.setVitesse(200, 800);
                mv.gotoPointMM(1000, 187);
            } else {
                // TODO : Vert
            }

            servos.setPositionAndWait(IConstantesServos.PINCE, IConstantesServos.PINCE_PRISE_BALLE);
            servos.setPositionAndWait(IConstantesServos.ASCENSEUR, IConstantesServos.ASCENSEUR_HAUT_BALLE);
            rs.setBalleDansAscenseur(true);
            mv.reculeMM(50);

            boolean gbDroit = ioService.gobeletDroit();
            boolean gbGauche = ioService.gobeletGauche();
            if (gbDroit || gbGauche) {
                rs.getYZoneDeposePrincipale();
                if (ioService.gobeletDroit()) {
                    servosService.deposeGobeletDroit();
                } else if (ioService.gobeletGauche()) {
                    servosService.deposeGobeletGauche();
                }
                mv.setVitesse(400, 800);
                mv.reculeMM(200);
                servosService.fermeProduitDroit();
                servosService.fermeProduitGauche();
            }

            mv.tourneDeg(180);
            completed = true;
        } catch (NoPathFoundException | ObstacleFoundException | AvoidingException e) {
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
        } finally {
            servosService.priseProduitDroit();
            servosService.priseProduitGauche();
        }
    }
}

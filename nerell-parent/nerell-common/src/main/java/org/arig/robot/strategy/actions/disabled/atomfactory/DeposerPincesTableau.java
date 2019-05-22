package org.arig.robot.strategy.actions.disabled.atomfactory;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.exception.RefreshPathFindingException;
import org.arig.robot.model.ESide;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.model.Team;
import org.arig.robot.model.enums.CouleurPalet;
import org.arig.robot.services.IIOService;
import org.arig.robot.services.LeftSideService;
import org.arig.robot.services.RightSideService;
import org.arig.robot.services.VentousesService;
import org.arig.robot.strategy.AbstractAction;
import org.arig.robot.system.TrajectoryManager;
import org.arig.robot.utils.NerellUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

@Slf4j
@Component
public class DeposerPincesTableau extends AbstractAction {

    @Autowired
    private IIOService io;

    @Autowired
    private RobotStatus rs;

    @Autowired
    private TrajectoryManager mv;

    @Autowired
    private LeftSideService leftSideService;

    @Autowired
    private RightSideService rightSideService;

    @Autowired
    private VentousesService ventouses;

    @Getter
    private boolean completed = false;

    @Override
    public String name() {
        return "Depose de palets dans le tableau depuis les pinces";
    }

    @Override
    public int order() {
        int points = 0;
        points += io.presencePaletDroit() && io.buteePaletDroit() ? 1 : 0;
        points += io.presencePaletGauche() && io.buteePaletGauche() ? 1 : 0;
        points += ventouses.getCouleur(ESide.GAUCHE) != null ? 1 : 0;
        points += ventouses.getCouleur(ESide.DROITE) != null ? 1 : 0;
        points += ventouses.getCouleur(ESide.GAUCHE) == CouleurPalet.ROUGE && rs.getTeam() == Team.VIOLET ? 5 : 0;
        points += ventouses.getCouleur(ESide.DROITE) == CouleurPalet.VERT && rs.getTeam() == Team.VIOLET ? 5 : 0;
        points += ventouses.getCouleur(ESide.GAUCHE) == CouleurPalet.VERT && rs.getTeam() == Team.JAUNE ? 5 : 0;
        points += ventouses.getCouleur(ESide.DROITE) == CouleurPalet.ROUGE && rs.getTeam() == Team.JAUNE ? 5 : 0;
        return points;
    }

    @Override
    public boolean isValid() {
        return isTimeValid() &&
                (
                        ventouses.getCouleur(ESide.GAUCHE) != null ||
                                ventouses.getCouleur(ESide.DROITE) != null ||
                                io.buteePaletGauche() && io.presencePaletGauche() ||
                                io.buteePaletDroit() && io.presencePaletDroit()
                );
    }

    @Override
    public void execute() {

        try {
            rs.enableAvoidance();

            // 30=marge de sécu
            double offset = IConstantesNerellConfig.dstArriere + rs.getNbDeposesTableau() * IConstantesNerellConfig.offsetTableau + 30;

            if (rs.getTeam() == Team.VIOLET) {
                mv.pathTo(2550 - offset, 1400);
                mv.gotoOrientationDeg(0);
            } else {
                mv.pathTo(offset, 1400);
                mv.gotoOrientationDeg(180);
            }

            boolean paletPinceDroit = io.presencePaletDroit() && io.buteePaletDroit();
            boolean paletPinceGauche = io.presencePaletGauche() && io.buteePaletGauche();

            if (paletPinceDroit || paletPinceGauche) {
                rs.disableSerrage();

                leftSideService.pinceSerrageRepos(false);
                rightSideService.pinceSerrageRepos(false);

                rs.transfertPinceTableau(paletPinceDroit, paletPinceGauche);

                mv.reculeMM(IConstantesNerellConfig.offsetTableau);
            }

            CouleurPalet paletGauche = ventouses.getCouleur(ESide.GAUCHE);
            CouleurPalet paletDroite = ventouses.getCouleur(ESide.DROITE);

            NerellUtils.all(
                    ventouses.deposeTable(ESide.DROITE),
                    ventouses.deposeTable(ESide.GAUCHE)
            ).get();

            rs.transfertVentouseTableau(paletDroite, paletGauche);

            mv.reculeMM(IConstantesNerellConfig.offsetTableau);

        } catch (NoPathFoundException | AvoidingException | RefreshPathFindingException | InterruptedException | ExecutionException e) {
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
            updateValidTime();
            rs.enableSerrage();
        }
    }
}

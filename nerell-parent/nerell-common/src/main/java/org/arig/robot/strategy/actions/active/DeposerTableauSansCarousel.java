package org.arig.robot.strategy.actions.active;

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
import org.arig.robot.services.VentousesService;
import org.arig.robot.strategy.AbstractAction;
import org.arig.robot.system.TrajectoryManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DeposerTableauSansCarousel extends AbstractAction {


    @Autowired
    private IIOService io;

    @Autowired
    private RobotStatus rs;

    @Autowired
    private TrajectoryManager mv;

    @Autowired
    private VentousesService ventouses;

    @Getter
    private boolean completed = false;

    @Override
    public String name() {
        return "Déposer de palet sur le tableau sans carousel";
    }

    @Override
    public boolean isValid() {
        return isTimeValid() && (ventouses.getCouleur(ESide.GAUCHE) != null || ventouses.getCouleur(ESide.DROITE) != null);
    }

    @Override
    public int order() {
        return (ventouses.getCouleur(ESide.GAUCHE) != null ? 5 : 0) + (ventouses.getCouleur(ESide.DROITE) != null ? 5 : 0);
    }

    @Override
    public void execute() {
        mv.setVitesse(IConstantesNerellConfig.vitessePath, IConstantesNerellConfig.vitesseOrientation);
        try {
            rs.enableAvoidance();

            if (rs.getTeam() == Team.VIOLET) {
                mv.pathTo(2550 - IConstantesNerellConfig.dstArriere, 1400);
                mv.gotoOrientationDeg(0);
            } else {
                mv.pathTo(450 + IConstantesNerellConfig.dstArriere, 1400);
                mv.gotoOrientationDeg(180);
            }

            int distance = 450 - rs.getNbDeposesTableau() * IConstantesNerellConfig.offsetTableau;

            mv.avanceMM(distance);

            CouleurPalet paletGauche = ventouses.getCouleur(ESide.GAUCHE);
            CouleurPalet paletDroite = ventouses.getCouleur(ESide.DROITE);

            ventouses.deposeTable(ESide.DROITE);
            ventouses.deposeTable(ESide.GAUCHE);

            mv.avanceMM(distance);

            rs.incNbDeposesTableau();

            if (rs.isCarouselEnabled()) {
                rs.transfertTableau();
            } else {

                if (rs.getTeam() == Team.JAUNE) {
                    if (paletDroite != null) {
                        rs.getPaletsInTableauVert().add(paletDroite);
                    }
                    if (paletGauche != null) {
                        rs.getPaletsInTableauRouge().add(paletGauche);
                    }
                } else {
                    if (paletDroite != null) {
                        rs.getPaletsInTableauRouge().add(paletDroite);
                    }
                    if (paletGauche != null) {
                        rs.getPaletsInTableauVert().add(paletGauche);
                    }
                }
            }

        } catch (NoPathFoundException | AvoidingException | RefreshPathFindingException e) {
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
            updateValidTime();
        }
    }
}

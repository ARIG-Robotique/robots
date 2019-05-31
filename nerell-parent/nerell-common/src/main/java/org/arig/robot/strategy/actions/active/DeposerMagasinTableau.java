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
import org.arig.robot.services.MagasinService;
import org.arig.robot.strategy.AbstractAction;
import org.arig.robot.system.ICarouselManager;
import org.arig.robot.system.TrajectoryManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DeposerMagasinTableau extends AbstractAction {

    @Autowired
    private RobotStatus rs;

    @Autowired
    private TrajectoryManager mv;

    @Autowired
    private MagasinService magasin;

    @Autowired
    private ICarouselManager carousel;

    @Getter
    private boolean completed = false;

    @Override
    public String name() {
        return "Depose de palets dans le tableau depuis la magasin";
    }

    @Override
    public int order() {
        int nbPaletsMagasin = rs.getMagasin().get(ESide.DROITE).size() + rs.getMagasin().get(ESide.GAUCHE).size();
        int nbPaletsCarousel = (int) carousel.count(CouleurPalet.ROUGE);
        int nbPaletsMax = nbPaletsMagasin + Math.min(nbPaletsCarousel, IConstantesNerellConfig.nbPaletsMagasinMax * 2 - nbPaletsMagasin);
        return nbPaletsMax * 6;
    }

    @Override
    public boolean isValid() {
        return isTimeValid() &&
                rs.getNbDeposesTableau() == 0 &&
                (
                        rs.getMagasin().get(ESide.DROITE).size() + rs.getMagasin().get(ESide.GAUCHE).size() > 4 ||
                                rs.getRemainingTime() < 40000 && rs.getMagasin().get(ESide.DROITE).size() + rs.getMagasin().get(ESide.GAUCHE).size() > 0
                );
    }

    @Override
    public void execute() {

        try {
            mv.setVitesse(IConstantesNerellConfig.vitessePath, IConstantesNerellConfig.vitesseOrientation);

            rs.enableAvoidance();

            if (rs.getTeam().equals(Team.VIOLET)) {
                mv.pathTo(2300, 1580 - IConstantesNerellConfig.dstAtomeCentre);
                mv.pathTo(3000 - 280, 1580 - IConstantesNerellConfig.dstAtomeCentre);
            } else {
                mv.pathTo(700, 1580 - IConstantesNerellConfig.dstAtomeCentre);
                mv.pathTo(280, 1580 - IConstantesNerellConfig.dstAtomeCentre);
            }

            rs.disableAvoidance();
            mv.gotoOrientationDeg(-90);

            rs.disableMagasin();
            magasin.digerer();

            magasin.startEjection();

            mv.setVitesse(IConstantesNerellConfig.vitesseMoyenneBasse, IConstantesNerellConfig.vitesseOrientation);

            // On chie !!
            mv.avanceMM(340);
            mv.reculeMM(100);
            mv.avanceMM(200);

            rs.transfertMagasinTableau(true);

        } catch (NoPathFoundException | AvoidingException | RefreshPathFindingException e) {
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
            updateValidTime();
        }

        rs.enableMagasin();
        magasin.endEjection();
        completed = true;
    }

    /**
     * Regarde si dans le coté "vert" contient plutot du rouge
     */
    private boolean mostlyRed() {
        if (rs.getTeam() == Team.VIOLET) {
            if (rs.getMagasin().get(ESide.GAUCHE).stream().filter(c -> c != CouleurPalet.ROUGE).count() <= 1) {
                return true;
            }
        } else {
            if (rs.getMagasin().get(ESide.DROITE).stream().filter(c -> c != CouleurPalet.ROUGE).count() <= 1) {
                return true;
            }
        }
        return false;
    }
}

package org.arig.robot.strategy.actions.active;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.exception.RefreshPathFindingException;
import org.arig.robot.exceptions.VentouseNotAvailableException;
import org.arig.robot.model.ESide;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.model.Team;
import org.arig.robot.model.enums.CouleurPalet;
import org.arig.robot.services.CarouselService;
import org.arig.robot.services.MagasinService;
import org.arig.robot.services.VentousesService;
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
    private VentousesService ventouses;

    @Autowired
    private CarouselService carouselService;

    @Autowired
    private ICarouselManager carousel;

    @Getter
    private boolean completed = false;

    private boolean even = false;

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
            rs.enableAvoidance();
            mv.setVitesse(IConstantesNerellConfig.vitessePath, IConstantesNerellConfig.vitesseOrientation);

            even = !even;
            if (even) {
                if (rs.getTeam().equals(Team.VIOLET)) {
                    mv.pathTo(2300, 1580 - IConstantesNerellConfig.dstAtomeCentre);
                    rs.disableAvoidance();
                    mv.gotoPointMM(3000 - 280, 1580 - IConstantesNerellConfig.dstAtomeCentre, true);
                } else {
                    mv.pathTo(700, 1580 - IConstantesNerellConfig.dstAtomeCentre);
                    rs.disableAvoidance();
                    mv.gotoPointMM(280, 1580 - IConstantesNerellConfig.dstAtomeCentre, true);
                }
            } else {
                if (rs.getTeam() == Team.VIOLET) {
                    mv.pathTo(3000 - 280, 1050);
                } else {
                    mv.pathTo(280, 1050);
                }
            }

            rs.disableAvoidance();
            mv.gotoOrientationDeg(-90);

            if (!even) {
                // Second point de passage il faut reculer
                mv.reculeMM(450);
            }

            rs.disableMagasin();
            ventouses.waitAvailable(ESide.DROITE);
            ventouses.waitAvailable(ESide.GAUCHE);

            magasin.digerer(CouleurPalet.ROUGE);
            magasin.startEjection();

            mv.setVitesse(IConstantesNerellConfig.vitesseMoyenneBasse, IConstantesNerellConfig.vitesseOrientation);

            // On chie !!
            mv.avanceMM(340);
            mv.reculeMM(100);
            mv.avanceMM(200);

            magasin.endEjection();
            rs.transfertMagasinTableau(CouleurPalet.ROUGE);

            if (rs.getRemainingTime() > 5000) {
                // une deuxième fois en vert
                if (carousel.has(CouleurPalet.VERT)) {
                    magasin.digerer(CouleurPalet.VERT);
                    magasin.startEjection();

                    mv.avanceMM(340);
                    mv.reculeMM(100);
                    mv.avanceMM(200);

                    magasin.endEjection();
                    rs.transfertMagasinTableau(CouleurPalet.VERT);
                } else {
                    mv.avanceMM(440);
                }

                // une troisième fois en bleu
                if (carousel.has(CouleurPalet.BLEU)) {
                    magasin.digerer(CouleurPalet.BLEU);
                    magasin.startEjection();

                    mv.avanceMM(340);
                    mv.reculeMM(100);
                    mv.avanceMM(200);

                    magasin.endEjection();
                    rs.transfertMagasinTableau(CouleurPalet.BLEU);
                }
            } else if (carousel.has(CouleurPalet.ANY)) {
                // une deuxième fois en vert/bleu car plus beaucoup de temps
                magasin.digerer(CouleurPalet.ANY);
                magasin.startEjection();

                mv.avanceMM(340);
                mv.reculeMM(100);
                mv.avanceMM(200);

                magasin.endEjection();
                rs.transfertMagasinTableau(CouleurPalet.VERT);
            }

            completed = true;

        } catch (NoPathFoundException | AvoidingException | RefreshPathFindingException | VentouseNotAvailableException e) {
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
            updateValidTime();
        } finally {
            ventouses.releaseSide(ESide.DROITE);
            ventouses.releaseSide(ESide.GAUCHE);
        }

        rs.enableMagasin();
        magasin.endEjection();
    }
}

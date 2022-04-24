package org.arig.robot.strategy.actions.active;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.I2CException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.CarreFouille;
import org.arig.robot.model.CouleurCarreFouille;
import org.arig.robot.model.Point;
import org.arig.robot.model.Strategy;
import org.arig.robot.model.Team;
import org.arig.robot.model.enums.GotoOption;
import org.arig.robot.model.enums.TypeCalage;
import org.arig.robot.strategy.actions.AbstractEurobotAction;
import org.arig.robot.system.capteurs.CarreFouilleReader;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public abstract class AbstractDecouverteCarreDeFouilleAction extends AbstractEurobotAction {

    private static final int WAIT_READ_OHMMETRE_MS = 100;
    private static final int WAIT_READ_BASCULE_MS = 120;

    @Autowired
    private CarreFouilleReader cfReader;

    // Nombre de tentative de récupération des carrés de fouille
    protected int nbTry = 0;

    @Override
    public String name() {
        return EurobotConfig.ACTION_DECOUVERTE_CARRE_FOUILLE;
    }

    @Override
    public Point entryPoint() {
        return entryPoint(cf());
    }

    private Point entryPoint(CarreFouille carreFouille) {
        if (carreFouille != null) {
            return new Point(carreFouille.getX(), 200);
        } else {
            return new Point(getX(0), 0);
        }
    }

    @Override
    public int order() {
        return rs.zoneDeFouillePointRestant() + tableUtils.alterOrder(entryPoint());
    }

    @Override
    public boolean isValid() {
        return isTimeValid() && remainingTimeValid() && !rs.zoneDeFouilleComplete() && cf() != null;
    }

    @Override
    public void refreshCompleted() {
        if (rs.zoneDeFouilleComplete()) {
            complete();
        }
    }

    @Override
    public void execute() {
        try {
            double yRef = -1.0;
            double deltaX = 0;
            boolean calageBordure = false;
            boolean calageCarreFouille = false;
            do {
                CarreFouille carreFouille = cf();
                carreFouille.incrementTry();
                log.info("Traitement carré de fouille #{} {}", carreFouille.numero(), carreFouille.couleur());

                if (!calageBordure) {
                    // Calage bordure requis
                    final Point start = entryPoint(carreFouille);
                    log.info("Calage requis, on se place au point de départ : #{} - X={}", carreFouille.numero(), start.getX());
                    mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());
                    mv.pathTo(start);

                    mv.gotoOrientationDeg(-90);
                    mv.setVitesse(robotConfig.vitesse(0), robotConfig.vitesseOrientation());
                    rs.enableCalageBordure(TypeCalage.AVANT);
                    mv.avanceMM(start.getY() - robotConfig.distanceCalageAvant() - 10);
                    rs.enableCalageBordure(TypeCalage.AVANT);
                    mv.avanceMMSansAngle(40);
                    checkRecalageYmm(robotConfig.distanceCalageAvant());
                    checkRecalageAngleDeg(-90);

                    mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());
                    mv.reculeMM(70);
                    mv.tourneDeg(90);

                    yRef = conv.pulseToMm(position.getPt().getY());
                    log.info("Calage bordure terminé, yRef = {} mm", yRef);
                    calageBordure = true;
                }

                if (!calageCarreFouille && carreFouille.needRead()) {
                    log.info("Calage carré de fouille requis");
                    mv.setVitesse(robotConfig.vitesse(0), robotConfig.vitesseOrientation());

                    // On est censé avoir un carré de fouille
                    boolean presence;
                    int wait = 0;
                    int sleepTimeMs = 5;
                    do {
                        ThreadUtils.sleep(sleepTimeMs);
                        presence = commonIOService.presenceCarreFouille(true);
                        wait += sleepTimeMs;
                    } while(!presence && wait < WAIT_READ_BASCULE_MS);

                    // Calage uniquement si il y a un carre de fouille détecté
                    if (commonIOService.presenceCarreFouille(true)) {
                        rs.enableCalageBordure(TypeCalage.LATTERAL_DROIT);
                        mv.avanceMM(60);
                        mv.reculeMM(40); // Distance calage au capteur

                        double currentX = conv.pulseToMm(position.getPt().getX());
                        deltaX = currentX - carreFouille.getX();
                        log.info("On a déplacé le robot de {} mm (delta X)", deltaX);
                        calageCarreFouille = true;
                    }
                } else {
                    log.info("Position carre de fouille #{} - X={} ; Y={}", carreFouille.numero(), carreFouille.getX(), yRef);
                    GotoOption sens = GotoOption.AVANT;
                    if ((rs.team() == Team.JAUNE && isReverse()) || (rs.team() == Team.VIOLET && !isReverse())) {
                        sens = GotoOption.ARRIERE;
                    }
                    mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());
                    mv.gotoPoint(carreFouille.getX() + deltaX, yRef, sens, GotoOption.SANS_ORIENTATION);
                }
                commonServosService.carreFouilleOhmmetreOuvert(false);

                if (commonIOService.presenceCarreFouille(true)) {
                    log.info("Carré de fouille #{} {} : Presence", carreFouille.numero(), carreFouille.couleur());
                    CouleurCarreFouille couleur = carreFouille.couleur();
                    if (carreFouille.needRead()) {
                        log.info("Carré de fouille #{} {} : Lecture ohmmetre", carreFouille.numero(), carreFouille.couleur());
                        commonServosService.carreFouilleOhmmetreMesure(true);
                        int wait = 0;
                        int sleepTimeMs = 15;
                        do {
                            ThreadUtils.sleep(sleepTimeMs);
                            couleur = cfReader.readCarreFouille();
                            wait += sleepTimeMs;
                        } while(couleur == CouleurCarreFouille.INCONNU && wait < WAIT_READ_OHMMETRE_MS);
                        log.info("Carré de fouille #{} {} : Lecture ohmmetre : {}", carreFouille.numero(), carreFouille.couleur(), couleur);
                        group.couleurCarreFouille(carreFouille.numero(), couleur);
                        commonServosService.carreFouilleOhmmetreOuvert(false);
                    }

                    if (basculable(couleur)) {
                        log.info("Carré de fouille #{} {} : Basculage", carreFouille.numero(), carreFouille.couleur());
                        commonServosService.carreFouillePoussoirPoussette(true);
                        commonServosService.carreFouillePoussoirFerme(false);
                        boolean presence;
                        int wait = 0;
                        int sleepTimeMs = 5;
                        do {
                            ThreadUtils.sleep(sleepTimeMs);
                            presence = commonIOService.presenceCarreFouille(false);
                            wait += sleepTimeMs;
                        } while(presence && wait < WAIT_READ_BASCULE_MS);

                        if (!presence) {
                            group.basculeCarreFouille(carreFouille.numero());
                        }
                    }
                } else {
                    log.warn("Pas de carré de fouille, donc il est basculé");
                    group.basculeCarreFouille(carreFouille.numero());
                }

            } while (cf() != null);
        } catch (NoPathFoundException | AvoidingException | I2CException e) {
            updateValidTime();
            log.error("Erreur d'exécution de l'action : {}", e.toString());
        } finally {
            nbTry++;
            commonServosService.carreFouillePoussoirFerme(true);
            commonServosService.carreFouilleOhmmetreFerme(false);
            refreshCompleted();
        }
    }

    private CarreFouille cf() {
        return rs.nextCarreDeFouille(nbTry, isReverse());
    }

    private boolean basculable(CouleurCarreFouille couleur) {
        return (couleur == CouleurCarreFouille.JAUNE && rs.team() == Team.JAUNE) ||
                (couleur == CouleurCarreFouille.VIOLET && rs.team() == Team.VIOLET);
    }

    private boolean isReverse() {
        return rs.strategy() != Strategy.BASIC;
    }
}

package org.arig.robot.strategy.actions.active;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.I2CException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.CarreFouille;
import org.arig.robot.model.CouleurCarreFouille;
import org.arig.robot.model.Point;
import org.arig.robot.model.Team;
import org.arig.robot.model.enums.GotoOption;
import org.arig.robot.model.enums.TypeCalage;
import org.arig.robot.strategy.actions.AbstractEurobotAction;
import org.arig.robot.system.capteurs.CarreFouilleReader;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public abstract class AbstractDecouverteCarreDeFouilleAction extends AbstractEurobotAction {

    private static final int WAIT_READ_OHMMETRE_MS = 1000;
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
            boolean calageBordureDone = false;
            boolean calageCarreFouilleDone = false;
            boolean needMove = false;
            do {
                CarreFouille carreFouille = cf();
                carreFouille.incrementTry();
                log.info("Traitement carré de fouille #{} {}", carreFouille.numero(), carreFouille.couleur());

                // Le calage bordure n'as pas encore été fait, donc on se cale sur celle-ci
                if (!calageBordureDone) {
                    // Calage bordure requis
                    final Point start = entryPoint(carreFouille);
                    log.info("Calage requis, on se place au point de départ : #{} - X={}", carreFouille.numero(), start.getX());
                    mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());
                    mv.pathTo(start);

                    mv.gotoOrientationDeg(-90);
                    mv.setVitesse(robotConfig.vitesse(0), robotConfig.vitesseOrientation());
                    rs.enableCalageBordure(TypeCalage.AVANT_BAS, TypeCalage.FORCE);
                    mv.avanceMM(start.getY() - robotConfig.distanceCalageAvant() - 10);
                    rs.enableCalageBordure(TypeCalage.AVANT_BAS, TypeCalage.FORCE);
                    mv.avanceMMSansAngle(40);
                    checkRecalageYmm(robotConfig.distanceCalageAvant());
                    checkRecalageAngleDeg(-90);

                    mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());
                    mv.reculeMM(70);
                    mv.tourneDeg(90);

                    yRef = conv.pulseToMm(position.getPt().getY());
                    log.info("Calage bordure terminé, yRef = {} mm", yRef);
                    calageBordureDone = true;
                }

                // Si le calage sur carré de fouille n'a pas encore été fait, on se cale sur lui
                // si on a besoin de faire une lecture.
                if (!calageCarreFouilleDone && carreFouille.needRead()) {
                    log.info("Calage carré de fouille requis");
                    mv.setVitesse(robotConfig.vitesse(0), robotConfig.vitesseOrientation());

                    // On est censé avoir un carré de fouille
                    boolean presence = ThreadUtils.waitUntil(() -> commonIOService.presenceCarreFouille(true), 5, WAIT_READ_BASCULE_MS);

                    // Calage uniquement si il y a un carre de fouille détecté
                    if (presence) {
                        rs.enableCalageBordure(TypeCalage.LATTERAL_DROIT);
                        mv.avanceMM(60);
                        mv.reculeMM(40); // Distance calage au capteur

                        double currentX = conv.pulseToMm(position.getPt().getX());
                        deltaX = currentX - carreFouille.getX();
                        log.info("On a déplacé le robot de {} mm (delta X)", deltaX);
                        calageCarreFouilleDone = true;
                    }
                } else {
                    // Le calage sur carré de fouille n'auras plus lieu jusqu'a la prochaine tentative
                    calageCarreFouilleDone = true;
                }

                // Ici on se déplace seulement a partir du moment on a besoin.
                // Lors de la première itération, pas utile.
                // A partir des suivantes, on reset après calage sur le carré de fouille (cas ou pas besoin de lire au début)
                if (needMove) {
                    log.info("Position carre de fouille #{} - X={} ; Y={}", carreFouille.numero(), carreFouille.getX(), yRef);
                    GotoOption sens = GotoOption.AVANT;
                    if ((rs.team() == Team.JAUNE && isReverse()) || (rs.team() == Team.VIOLET && !isReverse())) {
                        sens = GotoOption.ARRIERE;
                    }
                    mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());
                    mv.gotoPoint(carreFouille.getX() + deltaX, yRef == -1.0 ? entryPoint().getY() : yRef, sens, GotoOption.SANS_ORIENTATION);
                }

                // Ouverture de l'ohmmetre
                commonServosService.carreFouilleOhmmetreOuvert(false);

                // Si on a pas de carre de fouille ici, c'est qu'il est basculé
                if (commonIOService.presenceCarreFouille(true)) {
                    log.info("Carré de fouille #{} {} : Presence", carreFouille.numero(), carreFouille.couleur());
                    CouleurCarreFouille couleur = carreFouille.couleur();
                    if (carreFouille.needRead()) {
                        log.info("Carré de fouille #{} {} : Lecture ohmmetre", carreFouille.numero(), carreFouille.couleur());
                        commonServosService.carreFouilleOhmmetreMesure(true);
                        couleur = ThreadUtils.waitUntil(() -> {
                            try {
                                return cfReader.readCarreFouille();
                            } catch (I2CException e) {
                                log.error("Erreur lecture carré de fouille", e);
                                return CouleurCarreFouille.INCONNU;
                            }
                        }, CouleurCarreFouille.INCONNU, 15, WAIT_READ_OHMMETRE_MS);

                        log.info("Carré de fouille #{} {} : Lecture ohmmetre : {}", carreFouille.numero(), carreFouille.couleur(), couleur);
                        group.couleurCarreFouille(carreFouille.numero(), couleur);
                        commonServosService.carreFouilleOhmmetreOuvert(false);
                    }

                    if (basculable(couleur)) {
                        log.info("Carré de fouille #{} {} : Basculage", carreFouille.numero(), carreFouille.couleur());
                        commonServosService.carreFouillePoussoirPoussette(true);
                        commonServosService.carreFouillePoussoirFerme(false);

                        boolean notPresence = ThreadUtils.waitUntil(() -> !commonIOService.presenceCarreFouille(false), 5, WAIT_READ_BASCULE_MS);
                        if (notPresence) {
                            group.basculeCarreFouille(carreFouille.numero());
                        }
                    }
                } else {
                    log.warn("Pas de carré de fouille, donc il est basculé");
                    group.basculeCarreFouille(carreFouille.numero());
                }

                // A la prochaine itération, il faut se déplacer devant le carré de fouille suivant
                needMove = true;
            } while (cf() != null);
        } catch (NoPathFoundException | AvoidingException e) {
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
        return rs.reverseCarreDeFouille() && nbTry == 0;
    }
}

package org.arig.robot.strategy.actions.active;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.I2CException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.*;
import org.arig.robot.model.bras.PositionBras;
import org.arig.robot.model.enums.GotoOption;
import org.arig.robot.model.enums.TypeCalage;
import org.arig.robot.services.BrasService;
import org.arig.robot.strategy.actions.AbstractEurobotAction;
import org.arig.robot.system.capteurs.CarreFouilleReader;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.Polygon;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class DecouverteCarreDeFouilleAction extends AbstractEurobotAction {

    private static final int WAIT_READ_OHMMETRE_MS = 1000;
    private static final int WAIT_READ_BASCULE_MS = 120;

    private static final int ENTRY_Y = 200;

    @Autowired
    private CarreFouilleReader cfReader;

    @Autowired
    private BrasService bras;

    // Nombre de tentative de récupération des carrés de fouille
    protected int nbTry = 0;

    boolean firstAction = false;

    boolean reverse = false;

    @Override
    public String name() {
        return EurobotConfig.ACTION_DECOUVERTE_CARRE_FOUILLE;
    }

    @Override
    public int executionTimeMs() {
        int executionTime = 2500; // Calage
        executionTime += 1300 * 4; // 1,3 sec par carre de fouille

        return executionTime;
    }

    @Override
    public Point entryPoint() {
        if (rs.reverseCarreDeFouille() && nbTry == 0) {
            reverse = true;
        } else {
            int currentX = getX((int) mv.currentXMm());
            reverse = currentX > 1500;
        }
        return entryPoint(cf());
    }

    private Point entryPoint(CarreFouille carreFouille) {
        if (carreFouille != null) {
            return new Point(carreFouille.getX(), ENTRY_Y);
        } else {
            return new Point(getX(0), 0);
        }
    }

    @Override
    public int order() {
        if (nbTry == 0 && rs.strategy() == Strategy.AGGRESSIVE
                && rs.twoRobots() && (robotName.id() == RobotName.RobotIdentification.ODIN)) {
            // Si c'est Odin et que la strat est aggressive avec deux robots
            // C'est la première action
            firstAction = true;
            return 1000;
        }

        return rs.carresDeFouillePointRestant() + tableUtils.alterOrder(entryPoint());
    }

    @Override
    public boolean isValid() {
        return isTimeValid() && timeBeforeRetourValid() && !rs.carresDeFouilleComplete() && cf() != null;
    }

    @Override
    public List<String> blockingActions() {
        if (!reverse) {
            return Arrays.asList(EurobotConfig.ACTION_PRISE_SITE_FOUILLE_EQUIPE, EurobotConfig.ACTION_ABRI_CHANTIER);
        }
        return Collections.singletonList(EurobotConfig.ACTION_PRISE_SITE_FOUILLE_EQUIPE);
    }

    @Override
    public void refreshCompleted() {
        if (rs.carresDeFouilleComplete()) {
            complete();
        }
    }

    @Override
    public void execute() {
        try {
            double yRef = ENTRY_Y;
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
                    mv.setVitesse(config.vitesse(), config.vitesseOrientation());
                    if (firstAction) {
                        mv.gotoPoint(getX(660), 320, GotoOption.SANS_ORIENTATION);
                        rs.enableAvoidance();
                        mv.gotoPoint(start, GotoOption.SANS_ORIENTATION);
                    } else {
//                        Echantillon echantillonAEnlever = deminageRequis(start);
//                        if (echantillonAEnlever != null) {
//                            deminage(echantillonAEnlever);
//                            mv.gotoPoint(start);
//                        } else {
                            mv.pathTo(start);
//                        }
                    }

                    mv.gotoOrientationDeg(-90);
                    mv.setVitesse(config.vitesse(0), config.vitesseOrientation());
                    rs.enableCalageBordure(TypeCalage.AVANT_BAS, TypeCalage.FORCE);
                    mv.avanceMM(start.getY() - config.distanceCalageAvant() - 10);
                    rs.enableCalageBordure(TypeCalage.AVANT_BAS, TypeCalage.FORCE);
                    mv.avanceMMSansAngle(40);
                    checkRecalageYmm(config.distanceCalageAvant());
                    checkRecalageAngleDeg(-90);

                    mv.setVitesse(config.vitesse(), config.vitesseOrientation());
                    mv.reculeMM(70);
                    mv.tourneDeg(90);

                    yRef = mv.currentYMm();
                    log.info("Calage bordure terminé, yRef = {} mm", yRef);
                    calageBordureDone = true;
                } else {
                    mv.gotoOrientationDeg(0);
                }

                // Si le calage sur carré de fouille n'a pas encore été fait, on se cale sur lui
                // si on a besoin de faire une lecture.
                //if (nbTry > 0 && !calageCarreFouilleDone && carreFouille.needRead()) {
                if (!calageCarreFouilleDone && carreFouille.needRead()) {
                    log.info("Calage carré de fouille requis");
                    mv.setVitesse(config.vitesse(0), config.vitesseOrientation());

                    // On est censé avoir un carré de fouille
                    boolean presence = ThreadUtils.waitUntil(() -> io.presenceCarreFouille(true), 5, WAIT_READ_BASCULE_MS);

                    // Calage uniquement si il y a un carre de fouille détecté
                    if (presence) {
                        rs.enableCalageBordure(TypeCalage.LATTERAL_DROIT);
                        mv.avanceMM(60);
                        mv.reculeMM(40); // Distance calage au capteur

                        deltaX = mv.currentXMm() - carreFouille.getX();
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
                    if ((rs.team() == Team.JAUNE && reverse) || (rs.team() == Team.VIOLET && !reverse)) {
                        sens = GotoOption.ARRIERE;
                    }
                    mv.setVitesse(config.vitesse(), config.vitesseOrientation());
                    mv.gotoPoint(carreFouille.getX() + deltaX, yRef, sens, GotoOption.SANS_ORIENTATION);
                }

                // Ouverture de l'ohmmetre
                servos.carreFouilleOhmmetreOuvert(false);

                // Si on a pas de carre de fouille ici, c'est qu'il est basculé
                if (io.presenceCarreFouille(true)) {
                    log.info("Carré de fouille #{} {} : Presence", carreFouille.numero(), carreFouille.couleur());
                    CouleurCarreFouille couleur = carreFouille.couleur();
                    if (carreFouille.needRead()) {
                        int nbTryRead = 0;
                        do {
                            log.info("Carré de fouille #{} {} : Lecture ohmmetre", carreFouille.numero(), carreFouille.couleur());
                            servos.carreFouilleOhmmetreMesure(true);
                            couleur = ThreadUtils.waitUntil(() -> {
                                try {
                                    return cfReader.readCarreFouille();
                                } catch (I2CException e) {
                                    log.error("Erreur lecture carré de fouille", e);
                                    return CouleurCarreFouille.INCONNU;
                                }
                            }, CouleurCarreFouille.INCONNU, 15, WAIT_READ_OHMMETRE_MS);
                            if (couleur == CouleurCarreFouille.INCONNU) {
                                servos.carreFouilleOhmmetreOuvert(true);
                            }
                        } while (couleur == CouleurCarreFouille.INCONNU && nbTryRead++ < 2);

                        log.info("Carré de fouille #{} {} : Lecture ohmmetre : {}", carreFouille.numero(), carreFouille.couleur(), couleur);
                        group.couleurCarreFouille(carreFouille.numero(), couleur);
                        servos.carreFouilleOhmmetreOuvert(false);
                    }

                    if (basculable(couleur)) {
                        log.info("Carré de fouille #{} {} : Basculage", carreFouille.numero(), carreFouille.couleur());
                        servos.carreFouillePoussoirPoussette(true);
                        servos.carreFouillePoussoirFerme(false);

                        boolean notPresence = ThreadUtils.waitUntil(() -> !io.presenceCarreFouille(false), 5, WAIT_READ_BASCULE_MS);
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
            firstAction = false;
            servos.carreFouillePoussoirFerme(true);
            servos.carreFouilleOhmmetreFerme(false);
            refreshCompleted();
            if (!isCompleted()) {
                updateValidTime(); // Retentative plus tard
            }
        }
    }

    private CarreFouille cf() {
        return rs.nextCarreDeFouille(nbTry, reverse);
    }

    private boolean basculable(CouleurCarreFouille couleur) {
        return (couleur == CouleurCarreFouille.JAUNE && rs.team() == Team.JAUNE) ||
                (couleur == CouleurCarreFouille.VIOLET && rs.team() == Team.VIOLET);
    }

    private Echantillon deminageRequis(Point entryPoint) {
        java.awt.Rectangle zoneEntree = new java.awt.Rectangle();
        zoneEntree.setSize(300, 200);
        zoneEntree.setLocation((int) (entryPoint.getX() - zoneEntree.getWidth() / 2.0), 0);

        List<Echantillon> echantillons = rs.echantillons().findEchantillon(zoneEntree);
        if (echantillons.isEmpty()) {
            return null;
        } else {
            return echantillons.get(0);
        }
    }

    private void deminage(Echantillon echantillon) throws AvoidingException, NoPathFoundException {
        log.info("Déminage {}", echantillon);
        mv.pathTo(echantillon.getX(), 300, GotoOption.AVANT);

        mv.alignFrontTo(echantillon);
        Point ptApproche = tableUtils.eloigner(echantillon, -EurobotConfig.ECHANTILLON_SIZE - config.distanceCalageAvant());
        if (ptApproche.getY() < 300) {
            mv.gotoPoint(ptApproche);
        }

        mv.setVitesse(config.vitesse(0), config.vitesseOrientation());
        rs.enableCalageBordure(TypeCalage.PRISE_ECHANTILLON, TypeCalage.FORCE);
        mv.avanceMMSansAngle(EurobotConfig.ECHANTILLON_SIZE);

        bras.setBrasHaut(PositionBras.HORIZONTAL);
        bras.setBrasBas(PositionBras.STOCK_ENTREE);
        bras.setBrasBas(PositionBras.SOL_PRISE);

        mv.setVitesse(config.vitesse(50), config.vitesseOrientation());

        if (bras.waitEnableVentouseBas(echantillon.getCouleur())) {
            bras.setBrasBas(PositionBras.SOL_DEPOSE_5);
            mv.gotoOrientationDeg(90);
            bras.waitReleaseVentouseBas();
        }

        bras.repos();
    }
}

package org.arig.robot.strategy.actions.active;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.I2CException;
import org.arig.robot.exception.MovementCancelledException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.CarreFouille;
import org.arig.robot.model.CouleurCarreFouille;
import org.arig.robot.model.Point;
import org.arig.robot.model.Team;
import org.arig.robot.model.enums.GotoOption;
import org.arig.robot.model.enums.TypeCalage;
import org.arig.robot.services.BrasService;
import org.arig.robot.strategy.actions.AbstractEurobotAction;
import org.arig.robot.system.capteurs.CarreFouilleReader;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class DecouverteCarreDeFouille extends AbstractEurobotAction {

    private static final int WAIT_READ_OHMMETRE_MS = 1000;
    private static final int WAIT_READ_BASCULE_MS = 120;

    private static final int ENTRY_Y = 300;

    @Autowired
    private CarreFouilleReader cfReader;

    @Autowired
    private BrasService bras;

    // Nombre de tentative de récupération des carrés de fouille
    protected int nbTry = 0;

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
        if (rs.team() == Team.JAUNE) {
            reverse = mv.currentXMm() > 1200;
        } else {
            reverse = mv.currentXMm() < 800;
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
        return 10000 + rs.carresDeFouillePointRestant() + tableUtils.alterOrder(entryPoint());
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
            CarreFouille carreFouille;

            List<CarreFouille> carresBloques = new ArrayList<>();

            while ((carreFouille = cf()) != null) {
                carreFouille.incrementTry();
                final Point start = entryPoint(carreFouille);
                log.info("Traitement carré de fouille #{} {}", carreFouille.numero(), carreFouille.couleur());

                // Le calage bordure n'as pas encore été fait, donc on se cale sur celle-ci
                if (!calageBordureDone) {
                    // Calage bordure requis
                    log.info("Calage requis, on se place au point de départ : #{} - X={}", carreFouille.numero(), start.getX());
                    mv.setVitesse(config.vitesse(), config.vitesseOrientation());
                    try {
                        mv.pathTo(start);
                    } catch (MovementCancelledException e) {
                        if (mv.currentYMm() < 300) {
                            log.warn("Blocage pendant l'approche du carré de fouille {}", carreFouille.numero());
                            carresBloques.add(carreFouille);
                            CarreFouille nextCf = cf();
                            if (nextCf != null) {
                                nextCf.incrementTry();
                                carresBloques.add(carreFouille);
                            }
                            if (mv.currentAngleDeg() > 0) {
                                mv.avanceMM(100);
                            } else {
                                mv.reculeMM(100);
                            }
                            continue;
                        } else {
                            throw e;
                        }
                    }

                    rs.enableAvoidance(true);
                    if (rs.stockTaille() >= 5 || mv.currentAngleDeg() > 0) {
                        mv.gotoOrientationDeg(90);
                        mv.setVitesse(config.vitesse(0), config.vitesseOrientation());
                        rs.enableCalageBordure(TypeCalage.ARRIERE, TypeCalage.FORCE);
                        mv.reculeMM(start.getY() - config.distanceCalageArriere() - 10);

                        if (mv.currentYMm() > 150) {
                            log.warn("Blocage pendant le callage du carré de fouille {}", carreFouille.numero());
                            carresBloques.add(carreFouille);
                            CarreFouille nextCf = cf();
                            if (nextCf != null) {
                                nextCf.incrementTry();
                                carresBloques.add(carreFouille);
                            }
                            if (mv.currentAngleDeg() > 0) {
                                mv.avanceMM(100);
                            } else {
                                mv.reculeMM(100);
                            }
                            continue;
                        }

                        rs.enableCalageBordure(TypeCalage.ARRIERE, TypeCalage.FORCE);
                        mv.reculeMMSansAngle(40);
                        checkRecalageYmm(config.distanceCalageArriere(), TypeCalage.ARRIERE);
                        checkRecalageAngleDeg(90, TypeCalage.ARRIERE);

                        mv.setVitesse(config.vitesse(), config.vitesseOrientation());
                        mv.avanceMM(70);
                        mv.tourneDeg(-90);

                    } else {
                        mv.gotoOrientationDeg(-90);
                        mv.setVitesse(config.vitesse(0), config.vitesseOrientation());
                        rs.enableCalageBordure(TypeCalage.AVANT_BAS, TypeCalage.FORCE);
                        mv.avanceMM(start.getY() - config.distanceCalageAvant() - 10);

                        if (mv.currentYMm() > 150) {
                            log.warn("Blocage pendant le callage du carré de fouille {}", carreFouille.numero());
                            carresBloques.add(carreFouille);
                            CarreFouille nextCf = cf();
                            if (nextCf != null) {
                                nextCf.incrementTry();
                                carresBloques.add(carreFouille);
                            }
                            if (mv.currentAngleDeg() > 0) {
                                mv.avanceMM(100);
                            } else {
                                mv.reculeMM(100);
                            }
                            continue;
                        }

                        rs.enableCalageBordure(TypeCalage.AVANT_BAS, TypeCalage.FORCE);
                        mv.avanceMMSansAngle(40);
                        checkRecalageYmm(config.distanceCalageAvant(), TypeCalage.AVANT_BAS);
                        checkRecalageAngleDeg(-90, TypeCalage.AVANT_BAS);

                        mv.setVitesse(config.vitesse(), config.vitesseOrientation());
                        mv.reculeMM(70);
                        mv.tourneDeg(90);
                    }

                    yRef = mv.currentYMm();
                    log.info("Calage bordure terminé, yRef = {} mm", yRef);
                    calageBordureDone = true;
                } else {
                    mv.gotoOrientationDeg(0);
                }

                carresBloques.forEach(CarreFouille::decrementTry);
                carresBloques.clear();

                rs.enableAvoidance(true);

                // Si le calage sur carré de fouille n'a pas encore été fait, on se cale sur lui
                // si on a besoin de faire une lecture.
                if (!calageCarreFouilleDone && carreFouille.needRead()) {
                    log.info("Calage carré de fouille requis");
                    mv.setVitesse(config.vitesse(0), config.vitesseOrientation());
                    mv.setRampesDistance(config.rampeAccelDistance(20), config.rampeDecelDistance(20));

                    // On est censé avoir un carré de fouille
                    boolean presence = ThreadUtils.waitUntil(() -> io.presenceCarreFouille(true), 5, WAIT_READ_BASCULE_MS);

                    // Calage uniquement si il y a un carre de fouille détecté
                    if (presence) {
                        rs.enableCalageBordure(TypeCalage.LATERAL_DROIT);
                        mv.avanceMM(60);
                        if (!rs.calageCompleted().contains(TypeCalage.LATERAL_DROIT)) {
                            mv.avanceMM(60);
                        }
                        mv.reculeMM(40); // Distance calage au capteur

                        deltaX = mv.currentXMm() - carreFouille.getX();
                        log.info("On a déplacé le robot de {} mm (delta X)", deltaX);
                        calageCarreFouilleDone = true;
                    }

                    mv.setRampesDistance(config.rampeAccelDistance(), config.rampeDecelDistance());
                }

                // Ici on se déplace seulement a partir du moment on a besoin.
                // Lors de la première itération, pas utile.
                // A partir des suivantes, on reset après calage sur le carré de fouille (cas ou pas besoin de lire au début)
                if (needMove) {
                    log.info("Position carre de fouille #{} - X={} ; Y={}", carreFouille.numero(), carreFouille.getX(), yRef);
                    mv.setVitesse(config.vitesse(), config.vitesseOrientation());
                    mv.gotoPoint(carreFouille.getX() + deltaX, yRef, GotoOption.SANS_ORIENTATION);
                    mv.gotoOrientationDeg(0);
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
            }
        } catch (NoPathFoundException | AvoidingException e) {
            updateValidTime();
            log.error("Erreur d'exécution de l'action : {}", e.toString());
        } finally {
            nbTry++;
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

}

package org.arig.robot.strategy.actions.active;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.MovementCancelledException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.*;
import org.arig.robot.model.Point;
import org.arig.robot.model.enums.GotoOption;
import org.arig.robot.model.enums.TypeCalage;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.concurrent.CompletableFuture;

import static org.arig.robot.constants.EurobotConfig.PTS_DEPOSE_PRISE;

@Slf4j
@Component
public class DistributeurEquipe extends AbstractDistributeur {

    private static final int DISTRIB_H = 102;

    private static final int ENTRY_X = 300;
    private static final int ENTRY_Y = 750;

    @Override
    public String name() {
        return EurobotConfig.ACTION_PRISE_DISTRIB_EQUIPE;
    }

    @Override
    public int executionTimeMs() {
        int executionTime = 1000; // Calage
        executionTime += 2000 * 3; // 2 sec par échantillon

        return executionTime;
    }

    @Override
    public int order() {
        int points = 3 + 3 * PTS_DEPOSE_PRISE;
        return points + tableUtils.alterOrder(entryPoint());
    }

    @Override
    public void refreshCompleted() {
        if (rs.distributeurEquipeTermine()) {
            complete();
        }
    }

    @Override
    public boolean isValid() {
        return isTimeValid() && timeBeforeRetourValid()
                && rs.distributeurEquipeDispo() && rs.stockDisponible() >= 3;
    }

    @Override
    public Point entryPoint() {
        return new Point(getX(ENTRY_X), ENTRY_Y);
    }

    @Override
    public void execute() {
        try {
            Point entry = entryPoint();
            mv.setVitesse(config.vitesse(), config.vitesseOrientation());

            // deminage par la caméra
            Echantillon echantillonAEnlever = deminageRequis();
            if (echantillonAEnlever != null) {
                deminage(echantillonAEnlever);
                if (!timeBeforeRetourValid()) {
                    log.warn("Annulation {}, y'a plus le temps", name());
                    return;
                }
                mv.gotoPoint(entry);
            } else {
                mv.pathTo(entry, GotoOption.AVANT);
                if (!timeBeforeRetourValid()) {
                    log.warn("Annulation {}, y'a plus le temps", name());
                    return;
                }
            }

            doExecute();

        } catch (NoPathFoundException | AvoidingException e) {
            if (e instanceof MovementCancelledException) {
                final double robotX = mv.currentXMm();
                final double robotY = mv.currentYMm();

                // blocage dans la zone d'approche = un échantillon bloque le passage
                if ((robotX <= 350 || robotX >= 3000 - 350) && robotY <= 830 && robotY >= 670) {
                    log.warn("Blocage détecté à proximité de {}", name());

                    try {
                        mv.avanceMM(0);

                        if (io.presencePriseBras(false)) {
                            log.info("Tentative de prise au sol");
                            if (priseAuSolEtEjecte(CouleurEchantillon.ROCHER, 90)) {
                                mv.gotoPoint(entryPoint());
                                doExecute();
                                return;
                            }
                        }

                        log.info("Attente de détection par la balise");
                        Echantillon echantillon = ThreadUtils.waitUntil(this::deminageRequis, null, 500, 3000);

                        if (echantillon != null) {
                            mv.reculeMM(100);
                            mv.alignFrontTo(echantillon);
                            mv.setVitesse(config.vitesse(0), config.vitesseOrientation());
                            rs.enableCalageBordure(TypeCalage.PRISE_ECHANTILLON, TypeCalage.FORCE);
                            mv.avanceMMSansAngle(100 + EurobotConfig.ECHANTILLON_SIZE);
                            if (priseAuSolEtEjecte(CouleurEchantillon.ROCHER, 90)) {
                                mv.gotoPoint(entryPoint());
                                doExecute();
                                return;
                            }
                        }

                        log.warn("Echec de déminage {}", name());
                        setDistributeurBloque();
                        mv.setVitesse(config.vitesse(), config.vitesseOrientation());
                        mv.reculeMM(100);

                    } catch (AvoidingException e2) {
                        log.warn("Erreur pendant le déminage " + e2.getMessage());
                        setDistributeurBloque();
                    }
                    return;
                }
            }

            log.error("Erreur d'exécution de l'action : {}", e.toString());
            updateValidTime();
            bras.safeHoming();
        }
    }

    private Echantillon deminageRequis() {
        Polygon zoneDistrib = new Polygon();
        zoneDistrib.addPoint(getX(80), 950);
        zoneDistrib.addPoint(getX(280), 950);
        zoneDistrib.addPoint(getX(280), 550);
        zoneDistrib.addPoint(getX(80), 550);
        return rs.echantillons().findEchantillon(zoneDistrib);
    }

    private void deminage(Echantillon echantillon) throws AvoidingException, NoPathFoundException {
        log.info("Déminage {} pour {}", echantillon, name());

        mv.pathTo(tableUtils.eloigner(echantillon, -EurobotConfig.ECHANTILLON_SIZE - config.distanceCalageAvant()), GotoOption.AVANT);
        mv.alignFrontTo(echantillon);

        mv.setVitesse(config.vitesse(0), config.vitesseOrientation());
        rs.enableCalageBordure(TypeCalage.PRISE_ECHANTILLON, TypeCalage.FORCE);
        mv.avanceMMSansAngle(EurobotConfig.ECHANTILLON_SIZE);

        priseAuSolEtEjecte(echantillon.getCouleur(), 90);
    }

    private void doExecute() throws AvoidingException {
        rs.disableAvoidance();

        // Calage sur X
        mv.setVitesse(config.vitesse(50), config.vitesseOrientation());
        mv.gotoOrientationDeg(rs.team() == Team.JAUNE ? 180 : 0);
        rs.enableCalageBordure(TypeCalage.AVANT_BAS);
        mv.avanceMM(ENTRY_X - DISTRIB_H - config.distanceCalageAvant() - 10);
        mv.setVitesse(config.vitesse(0), config.vitesseOrientation());
        rs.enableCalageBordure(TypeCalage.AVANT_BAS);
        mv.avanceMM(100);

        if (!io.calageAvantBasDroit() || !io.calageAvantBasGauche()) {
            log.warn("Mauvaise position Y pour {}", name());
            updateValidTime(); // FIXME on devrait requérir un callage avant de recommencer
            rs.enableAvoidance();
            mv.setVitesse(config.vitesse(), config.vitesseOrientation());
            mv.gotoPoint(entryPoint(), GotoOption.ARRIERE);
            return;
        }

        CompletableFuture<?> task = prepare();

        mv.setVitesse(config.vitesse(50), config.vitesseOrientation());
        mv.reculeMM(ENTRY_X - DISTRIB_H - config.distanceCalageAvant() - 10);
        mv.gotoOrientationDeg(rs.team() == Team.JAUNE ? 180 : 0);

        task.join();
        prise();

        rs.enableAvoidance();
        mv.setVitesse(config.vitesse(), config.vitesseOrientation());
        mv.gotoPoint(entryPoint(), GotoOption.ARRIERE);

        group.distributeurEquipe(StatutDistributeur.PRIS_NOUS);
        complete(true);
    }

    private void setDistributeurBloque() {
        group.distributeurEquipe(StatutDistributeur.BLOQUE); // on désactive l'action
    }
}

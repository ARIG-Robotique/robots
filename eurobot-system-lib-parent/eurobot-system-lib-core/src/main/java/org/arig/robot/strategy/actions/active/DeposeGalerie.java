package org.arig.robot.strategy.actions.active;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.CouleurEchantillon;
import org.arig.robot.model.Galerie;
import org.arig.robot.model.Point;
import org.arig.robot.model.bras.PositionBras;
import org.arig.robot.model.enums.GotoOption;
import org.arig.robot.model.enums.TypeCalage;
import org.arig.robot.services.BrasService;
import org.arig.robot.strategy.actions.AbstractEurobotAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@Slf4j
@Component
public class DeposeGalerie extends AbstractEurobotAction {

    private static final int GALERIE_WIDTH = 720;
    private static final int GALERIE_X_START = 450;
    private static final int GALERIE_X_END = GALERIE_X_START + GALERIE_WIDTH;
    private static final int GALERIE_CENTRE = GALERIE_X_START + GALERIE_WIDTH / 2;
    private static final int PERIODE_WIDTH = GALERIE_WIDTH / 3;
    private static final int DEMI_ECHANTILLON_WIDTH = EurobotConfig.ECHANTILLON_SIZE / 2;

    private static final int ENTRY_X_BLEU = GALERIE_X_START + DEMI_ECHANTILLON_WIDTH;
    private static final int ENTRY_X_BLEU_VERT = GALERIE_X_START + PERIODE_WIDTH;
    private static final int ENTRY_X_SINGLE_VERT = GALERIE_CENTRE;
    private static final int ENTRY_X_ROUGE_VERT = GALERIE_X_END - PERIODE_WIDTH;
    private static final int ENTRY_X_ROUGE = GALERIE_X_END - DEMI_ECHANTILLON_WIDTH;

    private static final int ENTRY_Y = 1720;

    private static final int OFFSET_Y_REF_AVANT_PROCHAINE_DEPOSE = 80;
    private static final int OFFSET_Y_REF_POUR_PREPARATION = 165;
    private static final int OFFSET_Y_REF_BAS = 20;

    @Autowired
    private BrasService bras;

    @Override
    public List<String> blockingActions() {
        return Collections.singletonList(EurobotConfig.ACTION_PRISE_DISTRIB_COMMUN_EQUIPE);
    }

    @Override
    public String name() {
        return EurobotConfig.ACTION_DEPOSE_GALERIE;
    }

    @Override
    public boolean isValid() {
        return isTimeValid() && remainingTimeBeforeRetourSiteValid() && !rs.galerieComplete()
                && (rs.stockTaille() >= 4 || (rs.stockTaille() > 0 && rs.getRemainingTime() < EurobotConfig.validDeposeIfElementInStockRemainingTime));
    }

    @Override
    public void refreshCompleted() {
        if (rs.galerieComplete()) {
            complete();
        }
    }

    @Override
    public int order() {
        return Math.min(rs.galerieEmplacementDisponible() * 6, rs.stockTaille() * 6) + tableUtils.alterOrder(entryPoint());
    }

    @Override
    public Point entryPoint() {
        return echantillonEntryPoint(bestPosition(null));
    }

    private Point echantillonEntryPoint(Galerie.GaleriePosition pos) {
        switch (pos.periode()) {
            case BLEU:
                return new Point(getX(ENTRY_X_BLEU), ENTRY_Y);
            case BLEU_VERT:
                return new Point(getX(ENTRY_X_BLEU_VERT), ENTRY_Y);
            case VERT:
                return new Point(getX(ENTRY_X_SINGLE_VERT), ENTRY_Y);
            case ROUGE_VERT:
                return new Point(getX(ENTRY_X_ROUGE_VERT), ENTRY_Y);
            default:
                return new Point(getX(ENTRY_X_ROUGE), ENTRY_Y);
        }
    }

    private Galerie.GaleriePosition bestPosition(Galerie.GaleriePosition lastPosition) {
        return rs.galerieBestPosition(rs.stockFirst(), lastPosition != null ? lastPosition.periode() : null);
    }

    @Override
    public void execute() {
        try {
            double yRefBordure = ENTRY_Y;
            Galerie.GaleriePosition lastPosition = null;
            Point entryPoint;
            boolean hasNextDepose;
            do {
                Galerie.GaleriePosition pos = bestPosition(lastPosition);
                CouleurEchantillon couleur = rs.stockFirst();
                log.info("Dépose {} dans la galerie : Période {}, Etage {}", couleur, pos.periode(), pos.etage());
                entryPoint = echantillonEntryPoint(pos);

                if (yRefBordure == ENTRY_Y) {
                    mv.setVitesse(config.vitesse(), config.vitesseOrientation());
                    mv.pathTo(entryPoint);

                    rs.disableAvoidance();
                    rs.enableCalageBordure(TypeCalage.AVANT_BAS, TypeCalage.FORCE);
                    mv.gotoPoint(entryPoint.getX(), EurobotConfig.tableHeight - config.distanceCalageAvant() - 85 - 20, GotoOption.AVANT);

                    mv.setVitesse(config.vitesse(0), config.vitesseOrientation());
                    rs.enableCalageBordure(TypeCalage.AVANT_BAS, TypeCalage.FORCE);
                    mv.avanceMM(100);
                    yRefBordure = mv.currentYMm();
                    log.info("Calage bordure galerie terminé, yRef = {} mm", yRefBordure);
                }

                io.enableLedCapteurCouleur();

                // On se place à la position permettant de tourner le robot
                rs.disableAvoidance();

                final Point tempPoint = new Point(entryPoint.getX(), yRefBordure - OFFSET_Y_REF_POUR_ROTATION);
                CompletableFuture<Void> moveTask = runAsync(() -> {
                    try {
                        mv.setVitesse(config.vitesse(), config.vitesseOrientation());
                        mv.gotoPoint(entryPoint.getX(), yRefBordure - OFFSET_Y_REF_POUR_PREPARATION, GotoOption.SANS_ORIENTATION);
                        mv.gotoOrientationDeg(90);
                    } catch (AvoidingException e) {
                        throw new CompletionException(e);
                    }
                });

                boolean ok;

                if (pos.etage() == Galerie.Etage.BAS) {
                    bras.setBrasHaut(PositionBras.HORIZONTAL);

                    if (couleur.isNeedsEchange()) {
                        bras.setBrasBas(PositionBras.HORIZONTAL);
                        ok = bras.destockageHaut() && bras.echangeHautBas();

                    } else {
                        ok = bras.destockageBas();
                    }

                    if (ok) {
                        bras.setBrasHaut(PositionBras.HORIZONTAL);
                        bras.setBrasBas(PositionBras.GALERIE_DEPOSE);

                        moveTask.join();
                        mv.gotoPoint(entryPoint.getX(), yRefBordure - OFFSET_Y_REF_BAS, GotoOption.AVANT);

                        couleur = rs.ventouseBas();
                        bras.waitReleaseVentouseBas();
                        bras.setBrasBas(PositionBras.STOCK_ENTREE);

                        comptagePoint(pos, couleur);
                    } else {
                        bras.setBrasHaut(PositionBras.HORIZONTAL);
                        bras.setBrasBas(PositionBras.STOCK_ENTREE);
                    }

                } else if (pos.etage() == Galerie.Etage.HAUT) {
                    bras.setBrasHaut(PositionBras.HORIZONTAL);

                    if (couleur.isNeedsEchange()) {
                        ok = bras.destockageBas() && bras.echangeBasHaut();

                    } else {
                        bras.setBrasBas(PositionBras.HORIZONTAL);
                        ok = bras.destockageHaut();
                    }

                    if (ok) {
                        bras.setBrasHaut(PositionBras.GALERIE_DEPOSE);
                        bras.setBrasBas(PositionBras.STOCK_ENTREE);

                        moveTask.join();
                        rs.enableCalageBordure(TypeCalage.AVANT_BAS, TypeCalage.FORCE);
                        mv.gotoPoint(entryPoint.getX(), yRefBordure, GotoOption.AVANT);

                        couleur = rs.ventouseHaut();
                        bras.waitReleaseVentouseHaut();

                        comptagePoint(pos, couleur);
                    } else {
                        bras.setBrasHaut(PositionBras.HORIZONTAL);
                        bras.setBrasBas(PositionBras.STOCK_ENTREE);
                    }
                }

                lastPosition = pos;

                hasNextDepose = !rs.galerieComplete() && rs.stockTaille() != 0 && remainingTimeBeforeRetourSiteValid();
                if (hasNextDepose) {
                    mv.gotoPoint(entryPoint.getX(), yRefBordure - OFFSET_Y_REF_AVANT_PROCHAINE_DEPOSE, GotoOption.SANS_ORIENTATION);
                }

                bras.repos();
            } while (hasNextDepose);

            // On se place à la position permettant de tourner le robot pour la prochaine action
            mv.setVitesse(config.vitesse(), config.vitesseOrientation());
            mv.gotoPoint(entryPoint.getX(), yRefBordure - OFFSET_Y_REF_POUR_PREPARATION, GotoOption.SANS_ORIENTATION);

        } catch (NoPathFoundException | AvoidingException e) {
            updateValidTime();
            log.error("Erreur d'exécution de l'action : {}", e.toString());

        } finally {
            bras.safeHoming();
            refreshCompleted();
        }
    }

    private void comptagePoint(Galerie.GaleriePosition pos, CouleurEchantillon echantillonDepose) {
        if (pos.periode() == Galerie.Periode.BLEU) {
            group.deposeGalerieBleu(echantillonDepose);
        } else if (pos.periode() == Galerie.Periode.BLEU_VERT) {
            group.deposeGalerieBleuVert(echantillonDepose);
        } else if (pos.periode() == Galerie.Periode.ROUGE) {
            group.deposeGalerieRouge(echantillonDepose);
        } else if (pos.periode() == Galerie.Periode.ROUGE_VERT) {
            group.deposeGalerieRougeVert(echantillonDepose);
        } else if (pos.periode() == Galerie.Periode.VERT) {
            group.deposeGalerieVert(echantillonDepose);
        }
    }
}

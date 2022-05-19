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

import static org.arig.robot.model.Galerie.Periode.*;

@Slf4j
@Component
public class DeposeGalerie extends AbstractEurobotAction {

    private static final int GALERIE_WIDTH = 720;
    private static final int GALERIE_X_START = 450;
    private static final int GALERIE_X_END = GALERIE_X_START + GALERIE_WIDTH;
    private static final int GALERIE_CENTRE = GALERIE_X_START + GALERIE_WIDTH / 2;
    private static final int PERIODE_WIDTH = GALERIE_WIDTH / 3;
    private static final int PERIODE_ECHANTILLON_A_CHEVAL = 30;

    private static final int ENTRY_X_BLEU = GALERIE_X_START + PERIODE_ECHANTILLON_A_CHEVAL;
    private static final int ENTRY_X_BLEU_VERT = GALERIE_X_START + PERIODE_WIDTH - PERIODE_ECHANTILLON_A_CHEVAL;
    private static final int ENTRY_X_SINGLE_VERT = GALERIE_CENTRE;
    private static final int ENTRY_X_ROUGE_VERT = GALERIE_X_END - PERIODE_WIDTH + PERIODE_ECHANTILLON_A_CHEVAL;
    private static final int ENTRY_X_ROUGE = GALERIE_X_END - PERIODE_ECHANTILLON_A_CHEVAL;

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
        return echantillonEntryPoint(VERT);
    }

    private Point echantillonEntryPoint(Galerie.Periode periode) {
        switch (periode) {
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
        if (rs.doubleDeposeGalerie() && rs.stockTaille() > 1) {
            return rs.galerieBestPositionDoubleDepose(rs.stockFirst(), rs.stockSecond(), lastPosition != null ? lastPosition.periode() : null);
        } else {
            return rs.galerieBestPosition(rs.stockFirst(), lastPosition != null ? lastPosition.periode() : null);
        }
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
                entryPoint = echantillonEntryPoint(pos.periode());

                if (pos.etage() == Galerie.Etage.DOUBLE) {
                    log.info("Dépose {}+{} dans la galerie : Période {}, Etage {}", rs.stockFirst(), rs.stockSecond(), pos.periode(), pos.etage());
                } else {
                    log.info("Dépose {} dans la galerie : Période {}, Etage {}", rs.stockFirst(), pos.periode(), pos.etage());
                }

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

                final Point tempPoint = new Point(entryPoint.getX(), yRefBordure - OFFSET_Y_REF_POUR_PREPARATION);
                CompletableFuture<Void> moveTask = runAsync(() -> {
                    try {
                        mv.setVitesse(config.vitesse(), config.vitesseOrientation());
                        mv.gotoPoint(tempPoint, GotoOption.SANS_ORIENTATION);
                        mv.gotoOrientationDeg(90);
                    } catch (AvoidingException e) {
                        throw new CompletionException(e);
                    }
                });

                if (pos.etage() == Galerie.Etage.BAS) {
                    CouleurEchantillon couleur = rs.stockFirst();

                    boolean ok;
                    if (needsEchange(couleur, pos)) {
                        bras.setBrasHaut(PositionBras.HORIZONTAL);
                        bras.setBrasBas(PositionBras.HORIZONTAL);
                        ok = bras.destockageHaut() && bras.echangeHautBas();

                    } else {
                        bras.setBrasHaut(PositionBras.HORIZONTAL);
                        ok = bras.destockageBas();
                    }

                    moveTask.join();

                    if (ok) {
                        bras.setBrasHaut(PositionBras.HORIZONTAL);
                        bras.setBrasBas(PositionBras.GALERIE_DEPOSE);

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
                    CouleurEchantillon couleur = rs.stockFirst();

                    boolean ok;
                    if (needsEchange(couleur, pos)) {
                        bras.setBrasHaut(PositionBras.HORIZONTAL);
                        ok = bras.destockageBas() && bras.echangeBasHaut();

                    } else {
                        bras.setBrasHaut(PositionBras.HORIZONTAL);
                        bras.setBrasBas(PositionBras.HORIZONTAL);
                        ok = bras.destockageHaut();
                    }

                    moveTask.join();

                    if (ok) {
                        bras.setBrasHaut(PositionBras.GALERIE_DEPOSE);
                        bras.setBrasBas(PositionBras.STOCK_ENTREE);

                        rs.enableCalageBordure(TypeCalage.AVANT_BAS, TypeCalage.FORCE);
                        mv.gotoPoint(entryPoint.getX(), yRefBordure, GotoOption.AVANT);

                        couleur = rs.ventouseHaut();
                        bras.waitReleaseVentouseHaut();

                        comptagePoint(pos, couleur);
                    } else {
                        bras.setBrasHaut(PositionBras.HORIZONTAL);
                        bras.setBrasBas(PositionBras.STOCK_ENTREE);
                    }

                } else if (pos.etage() == Galerie.Etage.DOUBLE) {
                    CouleurEchantillon couleur1 = rs.stockFirst();
                    CouleurEchantillon couleur2 = rs.stockSecond();

                    boolean okHaut;
                    if (needsEchange(couleur1, pos)) {
                        bras.setBrasHaut(PositionBras.HORIZONTAL);
                        okHaut = bras.destockageBas() && bras.echangeBasHaut();
                    } else {
                        bras.setBrasHaut(PositionBras.HORIZONTAL);
                        bras.setBrasBas(PositionBras.HORIZONTAL);
                        okHaut = bras.destockageHaut();
                    }
                    if (okHaut) {
                        bras.setBrasHaut(PositionBras.GALERIE_DEPOSE);
                    } else {
                        bras.setBrasHaut(PositionBras.HORIZONTAL);
                    }

                    boolean okBas = bras.destockageBas();
                    if (okBas) {
                        bras.setBrasBas(PositionBras.GALERIE_DEPOSE);
                    } else {
                        bras.setBrasBas(PositionBras.STOCK_ENTREE);
                    }

                    moveTask.join();

                    if (okBas) {
                        mv.gotoPoint(entryPoint.getX(), yRefBordure - OFFSET_Y_REF_BAS, GotoOption.AVANT);

                        couleur2 = rs.ventouseBas();
                        bras.waitReleaseVentouseBas();
                        bras.setBrasBas(PositionBras.STOCK_ENTREE);

                        comptagePoint(pos, couleur2);
                    }

                    if (okHaut) {
                        rs.enableCalageBordure(TypeCalage.AVANT_BAS, TypeCalage.FORCE);
                        mv.gotoPoint(entryPoint.getX(), yRefBordure, GotoOption.AVANT);

                        couleur1 = rs.ventouseHaut();
                        bras.waitReleaseVentouseHaut();

                        comptagePoint(pos, couleur1);
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
        } else if (pos.periode() == ROUGE) {
            group.deposeGalerieRouge(echantillonDepose);
        } else if (pos.periode() == ROUGE_VERT) {
            group.deposeGalerieRougeVert(echantillonDepose);
        } else if (pos.periode() == VERT) {
            group.deposeGalerieVert(echantillonDepose);
        }
    }

    /**
     * Vérifie si un échantillon a effectivement d'etre retourné pour le poser dans une période donnée
     * ex: pas besoin de retourner un ROUGE_VERT si on le pose dans la période BLEU
     */
    boolean needsEchange(CouleurEchantillon couleur, Galerie.GaleriePosition pos) {
        if (!couleur.isNeedsEchange()) {
            return false;
        }
        switch (pos.periode()) {
            case ROUGE:
                return couleur.isRouge();
            case ROUGE_VERT:
                return couleur.isRouge() || couleur.isVert();
            case VERT:
                return couleur.isVert();
            case BLEU_VERT:
                return couleur.isBleu() || couleur.isVert();
            case BLEU:
                return couleur.isBleu();
            default:
                return false;
        }
    }
}

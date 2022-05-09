package org.arig.robot.strategy.actions.active;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.CouleurEchantillon;
import org.arig.robot.model.Galerie;
import org.arig.robot.model.Point;
import org.arig.robot.model.enums.GotoOption;
import org.arig.robot.model.enums.TypeCalage;
import org.arig.robot.services.BrasService;
import org.arig.robot.strategy.actions.AbstractEurobotAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;


@Slf4j
@Component
public class DeposeGalerie extends AbstractEurobotAction {

    private static final int DEMI_ECHANTILLON_WIDTH = EurobotConfig.ECHANTILLON_SIZE / 2; // 75 = 150 / 2
    private static final int DEMI_GALERIE_WIDTH = EurobotConfig.GALERIE_WIDTH / 2; // 360 = 720 / 2
    private static final int PERIODE_WITDH = EurobotConfig.GALERIE_WIDTH / 3; // 240 = 720 / 3

    private static final int GALERIE_X_START = 450;
    private static final int ENTRY_X_BLEU = GALERIE_X_START + DEMI_ECHANTILLON_WIDTH;
    private static final int ENTRY_X_BLEU_VERT = GALERIE_X_START + PERIODE_WITDH;
    private static final int ENTRY_X_SINGLE_VERT = GALERIE_X_START + DEMI_GALERIE_WIDTH;
    private static final int ENTRY_X_ROUGE_VERT = GALERIE_X_START + (2 * PERIODE_WITDH);
    private static final int ENTRY_X_ROUGE = GALERIE_X_START + EurobotConfig.GALERIE_WIDTH - DEMI_ECHANTILLON_WIDTH;
    private static final int ENTRY_Y = 1720;

    private static final int OFFSET_Y_REF_POUR_ROTATION = 165;
    private static final int OFFSET_Y_REF_BAS = 20;
    private static final int OFFSET_Y_REF_HAUT = 5;

    @Autowired
    private BrasService brasService;

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
                && rs.getRemainingTime() >= (rs.stockTaille() * 5000L) // 5s par échantillon environ
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
        if (pos.periode() == Galerie.Periode.BLEU) {
            return new Point(getX(ENTRY_X_BLEU), ENTRY_Y);
        } else if (pos.periode() == Galerie.Periode.BLEU_VERT) {
            return new Point(getX(ENTRY_X_BLEU_VERT), ENTRY_Y);
        } else if (pos.periode() == Galerie.Periode.VERT) {
            return new Point(getX(ENTRY_X_SINGLE_VERT), ENTRY_Y);
        } else if (pos.periode() == Galerie.Periode.ROUGE_VERT) {
            return new Point(getX(ENTRY_X_ROUGE_VERT), ENTRY_Y);
        } else {
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
            do {
                Galerie.GaleriePosition pos = bestPosition(lastPosition);
                log.info("Dépose dans la galerie : Période {}, Etage {}", pos.periode(), pos.etage());
                entryPoint = echantillonEntryPoint(pos);
                if (yRefBordure == ENTRY_Y) {
                    mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());
                    mv.pathTo(entryPoint);

                    rs.enableCalageBordure(TypeCalage.AVANT_BAS, TypeCalage.FORCE);
                    mv.gotoPoint(entryPoint.getX(), EurobotConfig.tableHeight - robotConfig.distanceCalageAvant() - 102, GotoOption.AVANT);

                    mv.setVitesse(robotConfig.vitesse(0), robotConfig.vitesseOrientation());
                    rs.enableCalageBordure(TypeCalage.AVANT_BAS, TypeCalage.FORCE);
                    mv.avanceMM(100);
                    yRefBordure = conv.pulseToMm(position.getPt().getY());
                    log.info("Calage bordure galerie terminé, yRef = {} mm", yRefBordure);

                }

                // On se place à la position permettant de tourner le robot
                mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());
                mv.gotoPoint(entryPoint.getX(), yRefBordure - OFFSET_Y_REF_POUR_ROTATION, GotoOption.SANS_ORIENTATION);

                CouleurEchantillon echantillonDepose = null;
                if (pos.etage() == Galerie.Etage.BAS) {
                    if (brasService.initDepose(BrasService.TypeDepose.GALERIE_BAS)
                            && brasService.processDepose(BrasService.TypeDepose.GALERIE_BAS) != null) {

                        mv.gotoPoint(entryPoint.getX(), yRefBordure - OFFSET_Y_REF_BAS, GotoOption.AVANT);
                        echantillonDepose = brasService.processEndDeposeGalerie(BrasService.TypeDepose.GALERIE_BAS);
                    }

                } else if (pos.etage() == Galerie.Etage.HAUT) {
                    if (brasService.initDepose(BrasService.TypeDepose.GALERIE_HAUT)
                            && brasService.processDepose(BrasService.TypeDepose.GALERIE_HAUT) != null) {

                        mv.gotoPoint(entryPoint.getX(), yRefBordure - OFFSET_Y_REF_HAUT, GotoOption.AVANT);
                        echantillonDepose = brasService.processEndDeposeGalerie(BrasService.TypeDepose.GALERIE_HAUT);
                    }

                } else {
                    if (brasService.initDepose(BrasService.TypeDepose.GALERIE_CENTRE)
                            && brasService.processDepose(BrasService.TypeDepose.GALERIE_CENTRE) != null) {

                        rs.enableCalageBordure(TypeCalage.AVANT_BAS, TypeCalage.FORCE);
                        mv.gotoPoint(entryPoint.getX(), yRefBordure, GotoOption.AVANT);
                        echantillonDepose = brasService.processEndDeposeGalerie(BrasService.TypeDepose.GALERIE_CENTRE);
                    }
                }
                if (echantillonDepose != null) {
                    comptagePoint(pos, echantillonDepose);
                    brasService.finalizeDepose();
                } else {
                    log.warn("Echantillon de dépose null dans la galerie {} - {}", pos.periode(), pos.etage());
                }

                lastPosition = pos;
            } while (!rs.galerieComplete() && rs.stockTaille() != 0);

            // On se place à la position permettant de tourner le robot pour la prochaine action
            mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());
            mv.gotoPoint(entryPoint.getX(), yRefBordure - OFFSET_Y_REF_POUR_ROTATION, GotoOption.SANS_ORIENTATION);

        } catch (NoPathFoundException | AvoidingException e) {
            updateValidTime();
            log.error("Erreur d'exécution de l'action : {}", e.toString());
        } finally {
            brasService.safeHoming();
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

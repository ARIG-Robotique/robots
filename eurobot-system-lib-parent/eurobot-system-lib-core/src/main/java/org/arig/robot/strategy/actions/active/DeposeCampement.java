package org.arig.robot.strategy.actions.active;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.Campement;
import org.arig.robot.model.CouleurEchantillon;
import org.arig.robot.model.Point;
import org.arig.robot.model.Team;
import org.arig.robot.model.enums.TypeCalage;
import org.arig.robot.services.BrasService;
import org.arig.robot.strategy.actions.AbstractEurobotAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class DeposeCampement extends AbstractEurobotAction {

    protected final int X = 330;
    protected final int Y = 1260;
    protected final int OFFSET = 70; // TODO

    @Autowired
    private BrasService bras;

    @Override
    public List<String> blockingActions() {
        return Collections.singletonList(EurobotConfig.ACTION_POUSSETTE_CAMPEMENT);
    }

    @Override
    public String name() {
        return EurobotConfig.ACTION_DEPOSE_CAMPEMENT;
    }

    @Override
    public boolean isValid() {
        return !rs.poussetteCampementFaite() && !rs.campementComplet()
                && (rs.stockTaille() >= 4 || (rs.stockTaille() > 0 && rs.getRemainingTime() < EurobotConfig.validDeposeIfElementInStockRemainingTime))
                && isTimeValid() && remainingTimeBeforeRetourSiteValid();
    }

    @Override
    public int order() {
        int resteRouge = Campement.MAX_DEPOSE - rs.tailleCampementRouge();
        int resteVert = Campement.MAX_DEPOSE - rs.tailleCampementVertTemp();
        int resteBleu = Campement.MAX_DEPOSE - rs.tailleCampementBleu();
        int points = 0;

        // #puke
        for (int i = 0; i < rs.stock().length; i++) {
            if (rs.stock()[i] != null) {
                switch (rs.stock()[i]) {
                    case ROUGE:
                        if (resteRouge > 0) {
                            points += 2;
                            resteRouge--;
                        } else if (resteVert > 0) {
                            points += 1;
                            resteVert--;
                        } else if (resteBleu > 0) {
                            points += 1;
                            resteBleu--;
                        }
                        break;
                    case VERT:
                        if (resteVert > 0) {
                            points += 2;
                            resteVert--;
                        } else if (resteRouge > 0) {
                            points += 1;
                            resteRouge--;
                        } else if (resteBleu > 0) {
                            points += 1;
                            resteBleu--;
                        }
                        break;
                    case BLEU:
                        if (resteBleu > 0) {
                            points += 2;
                            resteBleu--;
                        } else if (resteRouge > 0) {
                            points += 1;
                            resteRouge--;
                        } else if (resteVert > 0) {
                            points += 1;
                            resteVert--;
                        }
                        break;
                    default:
                        if (resteRouge > 0) {
                            points += 1;
                            resteRouge--;
                        } else if (resteVert > 0) {
                            points += 1;
                            resteVert--;
                        } else if (resteBleu > 0) {
                            points += 1;
                            resteBleu--;
                        }
                }
            }
        }

        return points + tableUtils.alterOrder(entryPoint());
    }

    @Override
    public Point entryPoint() {
        return new Point(getX(X), Y);
    }

    @Override
    public void refreshCompleted() {
        if (rs.poussetteCampementFaite() || rs.campementComplet()) {
            complete();
        }
    }

    @Override
    public void execute() {
        try {
            final Point entry = entryPoint();

            mv.setVitesse(config.vitesse(), config.vitesseOrientation());
            mv.pathTo(entry);

            if (rs.tailleCampementRouge() == 0) {
                rs.enableCalageBordure(TypeCalage.AVANT_BAS, TypeCalage.FORCE);
                mv.gotoOrientationDeg(rs.team() == Team.JAUNE ? 180 : 0);
                mv.avanceMM(X - config.distanceCalageAvant() - 10);
                mv.setVitesse(config.vitesse(10), config.vitesseOrientation());
                rs.enableCalageBordure(TypeCalage.AVANT_BAS, TypeCalage.FORCE);
                mv.avanceMMSansAngle(100);
                checkRecalageXmm(rs.team() == Team.JAUNE ? config.distanceCalageAvant() : EurobotConfig.tableWidth - config.distanceCalageAvant());
                checkRecalageAngleDeg(rs.team() == Team.JAUNE ? 180 : 0);
                mv.setVitesse(config.vitesse(), config.vitesseOrientation());
                mv.gotoPoint(entry);
            }

            //mv.setVitesse(robotConfig.vitesse(50), robotConfig.vitesseOrientation());
            rs.disableAvoidance();

            bras.initDepose(BrasService.TypeDepose.SOL);

            CouleurEchantillon echantillon;
            CouleurEchantillon position = null;

            while ((echantillon = rs.stockFirst()) != null && remainingTimeBeforeRetourSiteValid()) {
                // on doit changer de colonne
                // - la première fois
                // - si on change de couleur
                // - si la zone est pleine
                CouleurEchantillon newPosition = getNewPosition(echantillon);
                if (newPosition == null) {
                    log.info("Plus de place au campement pour faire la dépose");
                    complete();
                    break;
                }

                log.info("Dépose {} dans le campement {}", echantillon, newPosition);

                if (position != newPosition) {
                    if (position != null) {
                        mv.gotoPoint(entry);
                    }
                    position = newPosition;

                    switch (position) {
                        case ROUGE:
                            mv.gotoOrientationDeg(rs.team() == Team.JAUNE ? 180 : 0);
                            break;
                        case VERT:
                            mv.gotoOrientationDeg(-90);
                            break;
                        case BLEU:
                            mv.gotoOrientationDeg(90);
                            break;
                    }
                    mv.avanceMM(position == CouleurEchantillon.ROUGE ? OFFSET - 10 : OFFSET);
                }


                switch (position) {
                    case ROUGE:
                        if (bras.processDeposeSol(rs.tailleCampementRouge()) != null) {
                            group.deposeCampementRouge(echantillon);
                        }
                        break;
                    case VERT:
                        if (bras.processDeposeSol(rs.tailleCampementVertTemp()) != null) {
                            group.deposeCampementVertTemp(echantillon);
                        }
                        break;
                    case BLEU:
                        if (bras.processDeposeSol(rs.tailleCampementBleu()) != null) {
                            group.deposeCampementBleu(echantillon);
                        }
                        break;
                }
            }

            mv.gotoPoint(entry);
            bras.finalizeDepose();
            complete(true);

        } catch (NoPathFoundException | AvoidingException e) {
            log.error("Erreur d'exécution de l'action : {}", e.toString());
            updateValidTime();
            bras.safeHoming();
        }
    }

    private CouleurEchantillon getNewPosition(CouleurEchantillon echantillon) {
        int tailleRouge = rs.tailleCampementRouge();
        int tailleVert = rs.tailleCampementVertTemp();
        int tailleBleu = rs.tailleCampementBleu();

        log.info("Recherche de position campement pour {}. R:{}, V:{}, B:{}", echantillon, tailleRouge, tailleVert, tailleBleu);

        switch (echantillon) {
            case ROUGE:
                if (tailleRouge < Campement.MAX_DEPOSE) {
                    return CouleurEchantillon.ROUGE;
                } else {
                    return getNewPosition(CouleurEchantillon.ROCHER);
                }

            case VERT:
                if (tailleVert < Campement.MAX_DEPOSE) {
                    return CouleurEchantillon.VERT;
                } else {
                    return getNewPosition(CouleurEchantillon.ROCHER);
                }

            case BLEU:
                if (tailleBleu < Campement.MAX_DEPOSE) {
                    return CouleurEchantillon.BLEU;
                } else {
                    return getNewPosition(CouleurEchantillon.ROCHER);
                }

            default:
                if (tailleRouge == tailleBleu && tailleBleu == tailleVert) {
                    return CouleurEchantillon.values()[(int) Math.floor(Math.random() * 3)];
                }
                // on privilégie le moins vide
                if (tailleRouge < tailleVert && tailleRouge < tailleBleu) {
                    return CouleurEchantillon.ROUGE;
                }
                if (tailleVert < tailleRouge && tailleVert < tailleBleu) {
                    return CouleurEchantillon.VERT;
                }
                if (tailleBleu < tailleRouge && tailleBleu < tailleVert) {
                    return CouleurEchantillon.BLEU;
                }
                if (tailleRouge < tailleVert || tailleRouge < tailleBleu) {
                    return CouleurEchantillon.ROUGE;
                }
                if (tailleVert < tailleRouge || tailleVert < tailleBleu) {
                    return CouleurEchantillon.VERT;
                }
                if (tailleBleu < tailleRouge || tailleBleu < tailleVert) {
                    return CouleurEchantillon.BLEU;
                }
        }

        return null;
    }

}

package org.arig.robot.strategy.actions.active;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IEurobotConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.Chenaux;
import org.arig.robot.model.ECouleurBouee;
import org.arig.robot.model.ETeam;
import org.arig.robot.model.GrandChenaux;
import org.arig.robot.model.Point;
import org.arig.robot.model.enums.GotoOption;
import org.arig.robot.services.AbstractOdinPincesArriereService;
import org.arig.robot.services.AbstractOdinPincesAvantService;
import org.arig.robot.strategy.actions.AbstractOdinAction;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Slf4j
public abstract class AbstractOdinDeposeGrandChenal extends AbstractOdinAction {

    protected enum EPosition {
        NORD,
        SUD
    }

    @AllArgsConstructor
    class Result {
        GrandChenaux.Line line;
        int idxGauche;
        int idxDroite;
    }

    @Autowired
    private AbstractOdinPincesAvantService pincesAvantService;

    @Autowired
    private AbstractOdinPincesArriereService pincesArriereService;

    private Result result;

    protected abstract ECouleurBouee getCouleurChenal();

    protected abstract EPosition getPositionChenal();

    protected abstract ECouleurBouee[] getPinces();

    protected abstract List<ECouleurBouee> getChenal(GrandChenaux.Line line);

    @Override
    public boolean isValid() {
        if (Arrays.stream(getPinces()).noneMatch(Objects::nonNull)) {
            result = null;
            return false;
        }
        if (!isTimeValid() || rsOdin.inPort() || rsOdin.getRemainingTime() < IEurobotConfig.validRetourPortRemainingTimeOdin) {
            result = null;
            return false;
        }

        result = getOptimalPosition();

        return result != null;
    }

    @Override
    public Point entryPoint() {
        if (result == null) {
            return null;
        }
        if (result.line == GrandChenaux.Line.B) {
            return new Point(getX(400), 1200);
        } else if (getPositionChenal() == EPosition.NORD) {
            return new Point(getX(400), 1765);
        } else {
            return new Point(getX(400), 635);
        }
    }

    @Override
    public int order() {
        if (result == null) {
            return -1;
        }

        Chenaux chenauxFuture = rs.cloneGrandChenaux();
        int currentScoreChenaux = chenauxFuture.score();

        if (getCouleurChenal() == ECouleurBouee.VERT) {
            if (result.idxGauche != -1) {
                chenauxFuture.addVert(getPinces()[0]);
            } else {
                chenauxFuture.addVert(getPinces()[1]);
            }
        } else {
            if (result.idxGauche != -1) {
                chenauxFuture.addRouge(getPinces()[0]);
            } else {
                chenauxFuture.addRouge(getPinces()[1]);
            }
        }

        return (chenauxFuture.score() - currentScoreChenaux) + tableUtils.alterOrder(entryPoint());
    }

    @Override
    public void execute() {
        try {
            log.info("Depose grand chenal {} ligne {}", getCouleurChenal(), result.line);
            log.info("Idx gauche {} ; idx droite {}", result.idxGauche, result.idxDroite);

            rsOdin.enablePincesAvant();
            rsOdin.enablePincesArriere();
            mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());

            final Point entry = entryPoint();
            final Point currentPt = position.getPt();
            if (Math.abs(conv.pulseToMm(currentPt.getY()) - entry.getY()) < 10 &&
                    (rs.team() == ETeam.BLEU ? conv.pulseToMm(currentPt.getX()) < 400 : conv.pulseToMm(currentPt.getX()) > 2600)) {
                // on est déjà dans la zone, pas besoin d'aller à l'entry
            } else {
                mv.pathTo(entry);
            }

            int x = getXDepose();
            int y = getYDepose();
            log.info("Point de dépose {}x{}", x, y);
            mv.gotoPoint(x, y, getCouleurChenal() == ECouleurBouee.VERT ? GotoOption.AVANT : GotoOption.ARRIERE);

            if (result.line == GrandChenaux.Line.A && rs.team() == ETeam.BLEU ||
                    result.line == GrandChenaux.Line.B && rs.team() == ETeam.JAUNE) {
                mv.gotoOrientationDeg(-90);
            } else {
                mv.gotoOrientationDeg(90);
            }

            if (getCouleurChenal() == ECouleurBouee.VERT) {
                pincesAvantService.deposeGrandChenal(getCouleurChenal(), result.line, result.idxGauche, result.idxDroite);
            } else {
                pincesArriereService.deposeGrandChenal(getCouleurChenal(), result.line, result.idxGauche, result.idxDroite);
            }

            mv.gotoPoint(x, entry.getY(), getCouleurChenal() == ECouleurBouee.VERT ? GotoOption.ARRIERE : GotoOption.AVANT);

        } catch (NoPathFoundException | AvoidingException e) {
            updateValidTime();
            log.error("Erreur d'exécution de l'action : {}", e.toString());
        } finally {
            result = null;
        }
    }

    Result getOptimalPosition() {
        Result result = getOptimalPosition(GrandChenaux.Line.B);
        if (result == null) {
            result = getOptimalPosition(GrandChenaux.Line.A);
        }
        return result;
    }

    Result getOptimalPosition(GrandChenaux.Line line) {
        List<ECouleurBouee> chenal = getChenal(line);
        boolean hasGauche = getPinces()[0] != null;
        boolean hasDroite = getPinces()[1] != null;
        int idxGauche = -1;
        int idxDroite = -1;

        for (int i = 0; i < chenal.size(); i++) {
            // deux emplacements consécutifs
            if (hasGauche && hasDroite && i > 0 && chenal.get(i) == null && chenal.get(i - 1) == null) {
                if (line == GrandChenaux.Line.A && getCouleurChenal() == ECouleurBouee.VERT ||
                        line == GrandChenaux.Line.B && getCouleurChenal() == ECouleurBouee.ROUGE) {
                    idxGauche = i;
                    idxDroite = i - 1;
                } else {
                    idxGauche = i - 1;
                    idxDroite = i;
                }
                break;
            }
            // un seul emplacement
            if (chenal.get(i) == null) {
                if (line == GrandChenaux.Line.A) {
                    if (hasDroite) {
                        idxGauche = -1;
                        idxDroite = i;
                    } else {
                        idxGauche = i;
                        idxDroite = -1;
                    }
                } else {
                    if (hasGauche) {
                        idxGauche = i;
                        idxDroite = -1;
                    } else {
                        idxGauche = -1;
                        idxDroite = i;
                    }
                }
            }
        }

        // positions impossibles
        if (line == GrandChenaux.Line.A ? idxGauche == 0 : idxDroite == 0) {
            return null;
        }

        if (idxDroite != -1 || idxGauche != -1) {
            return new Result(line, idxGauche, idxDroite);
        } else {
            return null;
        }
    }

    int getXDepose() {
        // x de la bouée
        int x = 120;
        if (result.idxGauche != -1) {
            x += 75 * result.idxGauche;
        } else {
            x += 75 * result.idxDroite;
        }

        // offset ligne B
        if (result.line == GrandChenaux.Line.B) {
            x += 38;
        }

        // offset du robot
        if (result.idxGauche != -1) {
            if (result.line == GrandChenaux.Line.A) {
                x -= 38;
            } else {
                x += 38;
            }
        } else {
            if (result.line == GrandChenaux.Line.A) {
                x += 38;
            } else {
                x -= 38;
            }
        }

        return getX(x);
    }

    int getYDepose() {
        if (result.line == GrandChenaux.Line.A) {
            if (getPositionChenal() == EPosition.NORD) {
                return 1750;
            } else {
                return 1200 - 550;
            }
        } else {
            if (getPositionChenal() == EPosition.NORD) {
                return 1250;
            } else {
                return 1150;
            }
        }
    }
}

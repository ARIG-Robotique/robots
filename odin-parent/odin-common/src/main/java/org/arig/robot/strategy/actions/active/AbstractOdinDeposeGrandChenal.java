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
import org.arig.robot.model.OdinRobotStatus;
import org.arig.robot.model.Point;
import org.arig.robot.model.enums.GotoOption;
import org.arig.robot.services.AbstractOdinPincesArriereService;
import org.arig.robot.services.AbstractOdinPincesAvantService;
import org.arig.robot.strategy.actions.AbstractOdinAction;
import org.springframework.beans.factory.annotation.Autowired;

import java.awt.Rectangle;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

@Slf4j
public abstract class AbstractOdinDeposeGrandChenal extends AbstractOdinAction {

    protected enum EPosition {
        NORD,
        SUD
    }

    @AllArgsConstructor
    protected enum EFace {
        AVANT(OdinRobotStatus::pincesAvant, ECouleurBouee.VERT),
        ARRIERE(OdinRobotStatus::pincesArriere, ECouleurBouee.ROUGE);

        Function<OdinRobotStatus, ECouleurBouee[]> pinces;
        ECouleurBouee couleur;
    }

    @AllArgsConstructor
    class Result {
        GrandChenaux.Line line;
        EFace face;
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

    protected abstract List<ECouleurBouee> getChenal(GrandChenaux.Line line);

    @Override
    public boolean isValid() {
        if (!rsOdin.deposePartielle()) {
            // Pas de tri, on interdit les déposes grand chenal sinon Nerell est bloqué pour ces déposes non triés
            return false;
        }

        if (rsOdin.pincesArriereEmpty() && rsOdin.pincesAvantEmpty()) {
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
    public Rectangle blockingZone() {
        if (result == null) {
            return null;
        }
        if (result.line == GrandChenaux.Line.A) {
            if (getCouleurChenal() == ECouleurBouee.VERT && rs.team() == ETeam.BLEU ||
                    getCouleurChenal() == ECouleurBouee.ROUGE && rs.team() == ETeam.JAUNE) {
                return rs.team() == ETeam.BLEU ? IEurobotConfig.ZONE_PHARE_BLEU : IEurobotConfig.ZONE_PHARE_JAUNE;
            } else {
                return rs.team() == ETeam.BLEU ? IEurobotConfig.ZONE_ECUEIL_EQUIPE_BLEU : IEurobotConfig.ZONE_ECUEIL_EQUIPE_JAUNE;
            }
        } else {
            return rs.team() == ETeam.BLEU ? IEurobotConfig.ZONE_GRAND_PORT_BLEU : IEurobotConfig.ZONE_GRAND_PORT_JAUNE;
        }
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
        ECouleurBouee[] pinces = result.face.pinces.apply(rsOdin);

        if (getCouleurChenal() == ECouleurBouee.VERT) {
            if (result.idxGauche != -1) {
                chenauxFuture.addVert(pinces[0]);
            }
            if (result.idxDroite != -1) {
                chenauxFuture.addVert(pinces[1]);
            }
        } else {
            if (result.idxGauche != -1) {
                chenauxFuture.addRouge(pinces[0]);
            }
            if (result.idxDroite != -1) {
                chenauxFuture.addRouge(pinces[1]);
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
            mv.gotoPoint(x, y);

            int orientation = -90; // BLEU, avant, VERT A
            if (result.line == GrandChenaux.Line.B) {
                orientation *= -1;
            }
            if (getCouleurChenal() == ECouleurBouee.ROUGE) {
                orientation *= -1;
            }
            if (result.face == EFace.ARRIERE) {
                orientation *= -1;
            }
            if (rs.team() == ETeam.JAUNE) {
                orientation *= -1;
            }

            mv.gotoOrientationDeg(orientation);

            if (result.face == EFace.AVANT) {
                pincesAvantService.deposeGrandChenal(getCouleurChenal(), result.line, result.idxGauche, result.idxDroite);
            } else {
                pincesArriereService.deposeGrandChenal(getCouleurChenal(), result.line, result.idxGauche, result.idxDroite);
            }

            mv.gotoPoint(x, entry.getY());

        } catch (NoPathFoundException | AvoidingException e) {
            updateValidTime();
            log.error("Erreur d'exécution de l'action : {}", e.toString());
        } finally {
            result = null;
        }
    }

    Result getOptimalPosition() {
        EFace face = null;

        // face pleine
        for (EFace candidat : EFace.values()) {
            if (Stream.of(candidat.pinces.apply(rsOdin)).allMatch(b -> b == getCouleurChenal())) {
                face = candidat;
                break;
            }
        }

        // face pas pleine
        if (face == null) {
            for (EFace candidat : EFace.values()) {
                if (Stream.of(candidat.pinces.apply(rsOdin)).anyMatch(b -> b == getCouleurChenal() || b == ECouleurBouee.INCONNU)) {
                    face = candidat;
                    break;
                }
            }
        }

        if (face == null) {
            return null;
        }

        GrandChenaux.Line[] lines;
        if (rs.grandPortEmpty()) {
            lines = new GrandChenaux.Line[]{GrandChenaux.Line.B, GrandChenaux.Line.A};
        } else {
            lines = new GrandChenaux.Line[]{GrandChenaux.Line.A};
        }

        Result result = null;
        for (GrandChenaux.Line line : lines) {
            result = getOptimalPosition(line, face);
            if (result != null) {
                break;
            }
        }

        return result;
    }

    Result getOptimalPosition(GrandChenaux.Line line, EFace face) {
        ECouleurBouee[] pinces = face.pinces.apply(rsOdin);
        ECouleurBouee couleurChenal = getCouleurChenal();
        List<ECouleurBouee> chenal = getChenal(line);
        boolean hasGauche = pinces[0] == couleurChenal || pinces[0] == ECouleurBouee.INCONNU;
        boolean hasDroite = pinces[1] == couleurChenal || pinces[1] == ECouleurBouee.INCONNU;
        int idxGauche = -1;
        int idxDroite = -1;

        if (couleurChenal != face.couleur) {
            boolean temp = hasDroite;
            hasDroite = hasGauche;
            hasGauche = temp;
        }

        for (int i = 0; i < chenal.size(); i++) {
            // deux emplacements consécutifs
            if (hasGauche && hasDroite && i > 0 && chenal.get(i) == null && chenal.get(i - 1) == null) {
                if (line == GrandChenaux.Line.A) {
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
                // dépriorise l'emplacement extrème
                if (i == 4 && line == GrandChenaux.Line.A && (idxDroite != -1 || idxGauche != -1)) {
                    break;
                }

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

        if (couleurChenal != face.couleur) {
            int temp = idxGauche;
            idxGauche = idxDroite;
            idxDroite = temp;
        }

        if (idxDroite != -1 || idxGauche != -1) {
            return new Result(line, face, idxGauche, idxDroite);
        } else {
            return null;
        }
    }

    int getXDepose() throws AvoidingException {
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
        int offsetRobot;
        if (result.idxGauche != -1) {
            if (result.line == GrandChenaux.Line.A) {
                offsetRobot = -38;
            } else {
                offsetRobot = 38;
            }
        } else {
            if (result.line == GrandChenaux.Line.A) {
                offsetRobot = 38;
            } else {
                offsetRobot = -38;
            }
        }

        if (getCouleurChenal() != result.face.couleur) {
            offsetRobot = -offsetRobot;
        }

        x += offsetRobot;

        if (x < 150) {
            throw new AvoidingException("Erreur de calcul de la position de dépose");
        }

        return getX(x);
    }

    int getYDepose() {
        if (result.line == GrandChenaux.Line.A) {
            if (getPositionChenal() == EPosition.NORD) {
                return 1720;
            } else {
                return 1200 - 520;
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

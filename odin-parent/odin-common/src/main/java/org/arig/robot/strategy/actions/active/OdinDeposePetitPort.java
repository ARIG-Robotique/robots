package org.arig.robot.strategy.actions.active;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IEurobotConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.Chenaux;
import org.arig.robot.model.ECouleurBouee;
import org.arig.robot.model.ETeam;
import org.arig.robot.model.Point;
import org.arig.robot.model.enums.GotoOption;
import org.arig.robot.model.enums.SensRotation;
import org.arig.robot.model.enums.TypeCalage;
import org.arig.robot.services.AbstractOdinPincesArriereService;
import org.arig.robot.services.AbstractOdinPincesAvantService;
import org.arig.robot.strategy.actions.AbstractOdinAction;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.geom.Rectangle2D;

@Slf4j
@Component
public class OdinDeposePetitPort extends AbstractOdinAction {

    @Autowired
    private AbstractOdinPincesAvantService pincesAvantService;

    @Autowired
    private AbstractOdinPincesArriereService pincesArriereService;

    @Override
    public String name() {
        return IEurobotConfig.ACTION_DEPOSE_PETIT_PORT;
    }

    @Override
    public Point entryPoint() {
        return new Point(getX(1800), 620);
    }

    @Override
    public int order() {
        if (rs.stepsPetitPort() == 0) {
            return 12 + tableUtils.alterOrder(entryPoint());

        } else {
            Chenaux chenauxFuture1 = rsOdin.clonePetitChenaux();
            Chenaux chenauxFuture2 = rsOdin.clonePetitChenaux();
            int currentScoreChenaux = chenauxFuture1.score();

            chenauxFuture1.addVert(rsOdin.pincesAvant());
            chenauxFuture1.addRouge(rsOdin.pincesArriere());
            chenauxFuture2.addVert(rsOdin.pincesArriere());
            chenauxFuture2.addRouge(rsOdin.pincesAvant());

            int order = Math.max(chenauxFuture1.score(), chenauxFuture2.score()) + -currentScoreChenaux;
            return order + tableUtils.alterOrder(entryPoint());
        }
    }

    @Override
    public boolean isValid() {
        return isTimeValid() && !rsOdin.inPort() &&
                (rs.stepsPetitPort() == 0 ?
                        (rsOdin.pincesArriereEmpty() && rsOdin.pincesAvantEmpty()) : // traitement petit port
                        rs.stepsPetitPort() < 4 && (!rsOdin.pincesArriereEmpty() || !rsOdin.pincesAvantEmpty()) // depose
                );
    }

    @Override
    public void execute() {
        if (rs.stepsPetitPort() == 0) {
            executeDanse();
        } else {
            executeDepose();
        }
    }

    private void executeDanse() {
        boolean started = false;
        try {
            //point d'entrée
            mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());
            mv.pathTo(getX(1900), 550);
            mv.gotoOrientationDeg(-90);

            if (!rsOdin.pincesAvantEmpty() || !rsOdin.pincesArriereEmpty()) {
                log.warn("Plus de place pour faire la danse");
                return;
            }

            started = true;

            tableUtils.addDynamicDeadZone(new Rectangle2D.Double(900, 0, 1200, 400));

            // prise bouée verte (jaune) rouge (bleu)
            rsOdin.enablePincesAvant();

            if (rs.team() == ETeam.JAUNE) {
                pincesAvantService.setExpected(ECouleurBouee.VERT, 1);
            } else {
                pincesAvantService.setExpected(ECouleurBouee.ROUGE, 0);
            }

            mv.setVitesse(robotConfig.vitesse(20), robotConfig.vitesseOrientation(5));
            mv.gotoPoint(getX(1900), 220, GotoOption.AVANT);

            // deplacement bouée rouge (jaune) verte (bleu)
            if (rs.team() == ETeam.JAUNE) {
                servosOdin.brasDroitGobelet(true);
                mv.gotoOrientationDeg(0, SensRotation.TRIGO);
                servosOdin.brasDroitFerme(true);
            } else {
                servosOdin.brasGaucheGobelet(true);
                mv.gotoOrientationDeg(180, SensRotation.HORAIRE);
                servosOdin.brasGaucheFerme(true);
            }

            mv.setVitesse(robotConfig.vitesse(20), robotConfig.vitesseOrientation(100));
            mv.gotoOrientationDeg(-90);

            // prise bouée rouge (jaune) verte (bleu)
            rs.enableCalageBordure(TypeCalage.CUSTOM); // calage sur présence ventouses
            if (rs.team() == ETeam.JAUNE) {
                pincesAvantService.setExpected(ECouleurBouee.ROUGE, 0);
            } else {
                pincesAvantService.setExpected(ECouleurBouee.VERT, 1);
            }
            mv.avanceMMSansAngle(90);
            ThreadUtils.sleep(410); // double du scheduler de gestion du service

            // depose bouée verte (jaune) rouge (bleu)
            rsOdin.disablePincesAvant();
            if (rs.team() == ETeam.JAUNE) {
                pincesAvantService.releasePompe(false, true);
                group.deposePetitChenalVert(ECouleurBouee.VERT);
                rsOdin.pinceAvant(1, null);
            } else {
                pincesAvantService.releasePompe(true, false);
                group.deposePetitChenalRouge(ECouleurBouee.ROUGE);
                rsOdin.pinceAvant(0, null);
            }

            // degagement
            mv.gotoPoint(getX(1900), 220, GotoOption.ARRIERE);

            // prise bouée rouge (jaune) vert (bleu)
            rsOdin.enablePincesAvant();
            if (rs.team() == ETeam.JAUNE) {
                pincesAvantService.setExpected(ECouleurBouee.ROUGE, 1);
            } else {
                pincesAvantService.setExpected(ECouleurBouee.VERT, 0);
            }
            mv.gotoPoint(getX(1705), 370, GotoOption.AVANT);

            // deplacement bouée verte (jaune) rouge (bleu)
            mv.gotoOrientationDeg(-90);
            mv.gotoPoint(getX(1705), 220, GotoOption.AVANT);

            if (rs.team() == ETeam.JAUNE) {
                servosOdin.brasGaucheGobelet(true);
            } else {
                servosOdin.brasDroitGobelet(true);
            }
            mv.setVitesse(robotConfig.vitesse(100), robotConfig.vitesseOrientation(5));
            if (rs.team() == ETeam.JAUNE) {
                mv.gotoOrientationDeg(180, SensRotation.HORAIRE);
            } else {
                mv.gotoOrientationDeg(0, SensRotation.TRIGO);
            }
            mv.gotoPoint(getX(1820), 220, GotoOption.AVANT);
            if (rs.team() == ETeam.JAUNE) {
                group.deposePetitChenalVert(ECouleurBouee.VERT);
                servosOdin.brasGaucheFerme(true);
            } else {
                group.deposePetitChenalRouge(ECouleurBouee.ROUGE);
                servosOdin.brasDroitFerme(true);
            }

            // double dépose rouge (jaune) vert (bleu)
            mv.setVitesse(robotConfig.vitesse(100), robotConfig.vitesseOrientation(100));
            mv.gotoPoint(getX(1700), 245, GotoOption.ARRIERE);
            mv.gotoOrientationDeg(-90);

            if (rs.team() == ETeam.JAUNE) {
                pincesAvantService.deposePetitChenal(ECouleurBouee.ROUGE);
            } else {
                pincesAvantService.deposePetitChenal(ECouleurBouee.VERT);
            }

        } catch (NoPathFoundException | AvoidingException e) {
            updateValidTime();
            log.error("Erreur d'exécution de l'action : {}", e.toString());

        } finally {
            if (started) {
                group.incStepsPetitPort();
            }

            tableUtils.clearDynamicDeadZones();

            servosOdin.brasDroitFerme(false);
            servosOdin.brasGaucheFerme(false);
        }
    }

    private void executeDepose() {
        boolean didSomething = false;

        try {
            rsOdin.enablePincesArriere();
            rsOdin.enablePincesAvant();

            mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());

            final Point entry = entryPoint();
            final double entryX = entry.getX();
            final double baseYStep = 245;
            final double offsetX = 100;
            final boolean isReversed = isReversed();
            double y = baseYStep + rs.stepsPetitPort() * 70;

            if (!rsOdin.pincesAvantEmpty()) {
                double x = entryX + (isReversed ? offsetX : -offsetX);

                mv.pathTo(x, y, GotoOption.AVANT);
                mv.gotoOrientationDeg(-90);

                pincesAvantService.deposePetitChenal(isReversed ? ECouleurBouee.ROUGE : ECouleurBouee.VERT);

                didSomething = true;
            }

            if (!rsOdin.pincesArriereEmpty()) {
                double x = entryX + (isReversed ? -offsetX : offsetX);

                mv.pathTo(x, y, GotoOption.ARRIERE);
                mv.gotoOrientationDeg(90);

                pincesArriereService.deposePetitChenal(isReversed ? ECouleurBouee.VERT : ECouleurBouee.ROUGE);

                didSomething = true;
            }

        } catch (NoPathFoundException | AvoidingException e) {
            updateValidTime();
            log.error("Erreur d'exécution de l'action : {}", e.toString());

        } finally {
            if (didSomething) {
                group.incStepsPetitPort();

                if (rs.stepsPetitPort() > 3) {
                    complete();
                }
            }
        }
    }

    private boolean isReversed() {
        Chenaux chenauxFuture1 = rsOdin.clonePetitChenaux();
        Chenaux chenauxFuture2 = rsOdin.clonePetitChenaux();

        chenauxFuture1.addVert(rsOdin.pincesAvant());
        chenauxFuture1.addRouge(rsOdin.pincesArriere());
        chenauxFuture2.addVert(rsOdin.pincesArriere());
        chenauxFuture2.addRouge(rsOdin.pincesAvant());

        return chenauxFuture2.score() > chenauxFuture1.score();
    }
}

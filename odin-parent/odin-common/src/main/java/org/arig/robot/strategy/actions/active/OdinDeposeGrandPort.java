package org.arig.robot.strategy.actions.active;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IEurobotConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.MovementCancelledException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.Chenaux;
import org.arig.robot.model.ECouleurBouee;
import org.arig.robot.model.ETeam;
import org.arig.robot.model.Point;
import org.arig.robot.model.enums.GotoOption;
import org.arig.robot.services.AbstractOdinPincesArriereService;
import org.arig.robot.services.AbstractOdinPincesAvantService;
import org.arig.robot.strategy.actions.AbstractOdinAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OdinDeposeGrandPort extends AbstractOdinAction {

    @Autowired
    private AbstractOdinPincesAvantService pincesAvantService;

    @Autowired
    private AbstractOdinPincesArriereService pincesArriereService;

    @Setter
    private boolean disablePathTo = false;

    @Override
    public String name() {
        return IEurobotConfig.ACTION_DEPOSE_GRAND_PORT;
    }

    @Override
    public Point entryPoint() {
        return new Point(getX(235), 1200);
    }

    @Override
    public boolean isValid() {
        return isTimeValid() && !rsOdin.inPort() &&
                rs.getRemainingTime() > IEurobotConfig.validRetourPortRemainingTimeOdin &&
                rs.grandChenalRougeEmpty() && rs.grandChenalVertEmpty() && rs.grandChenalRougeBordureEmpty() && rs.grandChenalVertBordureEmpty() &&
                (!rsOdin.pincesAvantEmpty() || !rsOdin.pincesArriereEmpty());
    }

    @Override
    public int order() {
        Chenaux chenauxFuture = rsOdin.clonePetitChenaux();
        int currentScoreChenaux = chenauxFuture.score();

        chenauxFuture.addVert(rsOdin.pincesAvant());
        chenauxFuture.addRouge(rsOdin.pincesArriere());

        int order = chenauxFuture.score() - currentScoreChenaux;
        return order + tableUtils.alterOrder(entryPoint());
    }

    @Override
    public void execute() {
        try {
            final Point entry = entryPoint();

            rsOdin.enablePincesAvant();
            rsOdin.enablePincesArriere();

            mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());

            if (!disablePathTo) {
                mv.pathTo(entry);
            }

            // depose nord
            ECouleurBouee couleurNord = rs.team() == ETeam.BLEU ? ECouleurBouee.VERT : ECouleurBouee.ROUGE;
            int nbFaceAvant = 0;
            int nbFaceArriere = 0;
            if (rsOdin.pincesAvant()[0] == couleurNord) nbFaceAvant++;
            if (rsOdin.pincesAvant()[1] == couleurNord) nbFaceAvant++;
            if (rsOdin.pincesArriere()[0] == couleurNord) nbFaceArriere++;
            if (rsOdin.pincesArriere()[1] == couleurNord) nbFaceArriere++;
            GotoOption sens = nbFaceAvant > nbFaceArriere ? GotoOption.AVANT : GotoOption.ARRIERE;

            mv.gotoPoint(getX(235), 1490, sens);
            if (sens == GotoOption.AVANT) {
                mv.gotoOrientationDeg(rs.team() == ETeam.BLEU ? 180 : 0);
                if (rsOdin.team() == ETeam.BLEU) {
                    pincesAvantService.deposeFondGrandChenalVert();
                } else {
                    pincesAvantService.deposeFondGrandChenalRouge();
                }
            } else {
                mv.gotoOrientationDeg(rs.team() == ETeam.BLEU ? 0 : 180);
                if (rsOdin.team() == ETeam.BLEU) {
                    pincesArriereService.deposeFondGrandChenalVert();
                } else {
                    pincesArriereService.deposeFondGrandChenalRouge();
                }
            }

            // Dépose sud
            if (rsOdin.pincesAvantEmpty() && rsOdin.pincesArriereEmpty()) {
                return;
            }
            sens = sens == GotoOption.AVANT ? GotoOption.ARRIERE : GotoOption.AVANT;

            mv.gotoPoint(getX(235), 920, sens);
            if (sens == GotoOption.ARRIERE) {
                mv.gotoOrientationDeg(rsOdin.team() == ETeam.BLEU ? 0 : 180);
                if (rsOdin.team() == ETeam.BLEU) {
                    pincesArriereService.deposeFondGrandChenalRouge();
                } else {
                    pincesArriereService.deposeFondGrandChenalVert();
                }
            } else {
                mv.gotoOrientationDeg(rsOdin.team() == ETeam.BLEU ? 180 : 0);
                if (rsOdin.team() == ETeam.BLEU) {
                    pincesAvantService.deposeFondGrandChenalRouge();
                } else {
                    pincesAvantService.deposeFondGrandChenalVert();
                }
            }

            complete();

        } catch (MovementCancelledException e) {
            log.info("Mouvement annulé, on termine l'action");
            complete();

        } catch (NoPathFoundException | AvoidingException e) {
            updateValidTime();
            disablePathTo = false;
            log.error("Erreur d'exécution de l'action : {}", e.toString());
        }
    }

}

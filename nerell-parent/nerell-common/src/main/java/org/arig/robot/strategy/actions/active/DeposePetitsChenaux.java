package org.arig.robot.strategy.actions.active;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.*;
import org.arig.robot.model.enums.SensDeplacement;
import org.arig.robot.services.ServosService;
import org.arig.robot.strategy.AbstractAction;
import org.arig.robot.system.ITrajectoryManager;
import org.arig.robot.utils.TableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DeposePetitsChenaux extends AbstractAction {

    @Autowired
    private ITrajectoryManager mv;

    @Autowired
    private RobotStatus rs;

    @Autowired
    private ServosService servos;

    @Autowired
    private TableUtils tableUtils;

    @Getter
    private boolean completed = false;

    private int step = 0;

    @Override
    public String name() {
        return "Dépose petits chaunaux";
    }

    @Override
    protected Point entryPoint() {
        double x = 1800;
        double y = 600;
        if (ETeam.JAUNE == rs.getTeam()) {
            x = 3000 - x;
        }

        return new Point(x, y);
    }

    @Override
    public int order() {
        Chenaux chenauxFuture = rs.petitChanaux().with(
                ArrayUtils.subarray(rs.getPincesArriere(), 0, 2),
                ArrayUtils.subarray(rs.getPincesArriere(), 2, 4)
        );

        if (step == 0) {
            chenauxFuture.addRouge(ECouleurBouee.ROUGE, ECouleurBouee.VERT);
            chenauxFuture.addVert(ECouleurBouee.ROUGE, ECouleurBouee.VERT);
        }

        int order = chenauxFuture.score() - rs.petitChanaux().score();
        return order + tableUtils.alterOrder(entryPoint());
    }

    @Override
    public boolean isValid() {
        return isTimeValid() && (!rs.pincesAvantEmpty() || step == 0 && rs.getRemainingTime() < 40000);
    }

    @Override
    public void execute() {
        try {
            rs.enableAvoidance();
            mv.setVitesse(IConstantesNerellConfig.vitessePath, IConstantesNerellConfig.vitesseOrientation);

            final Point entry = entryPoint();
            final double x = entry.getX();
            final double y = entry.getY();
            mv.pathTo(entry);

            mv.gotoOrientationDeg(-90);
            rs.disablePinces();

            // première dépose
            // gestion des bouées devant et sur les côtés
            if (step == 0) {
                servos.ascenseurAvantOuvertureMoustache(true);
                servos.moustachesOuvert(true);

                mv.gotoPointMM(x, 500, false);
                servos.moustachesPoussette(true); // TODO gestion vitesse
                servos.moustachesOuvert(true);

                mv.gotoPointMM(x, 220, false);
                servos.moustachesPoussette(true);
                servos.moustachesOuvert(true);

                servos.ascenseurAvantBas(true);
                servos.pincesAvantOuvert(true);

                rs.petitChanaux().addRouge(ECouleurBouee.ROUGE, ECouleurBouee.VERT);
                rs.petitChanaux().addVert(ECouleurBouee.ROUGE, ECouleurBouee.VERT);
            }
            // déposes suivantes
            else {
                mv.gotoPointMM(x, 220 + step * 80, false);

                servos.ascenseurAvantBas(true);
                servos.pincesAvantOuvert(true);
            }

            if (rs.getPincesAvant()[0] != null) {
                rs.petitChanaux().addRouge(rs.getPincesAvant()[0]);
            }
            if (rs.getPincesAvant()[1] != null) {
                rs.petitChanaux().addRouge(rs.getPincesAvant()[1]);
            }
            if (rs.getPincesAvant()[2] != null) {
                rs.petitChanaux().addVert(rs.getPincesAvant()[2]);
            }
            if (rs.getPincesAvant()[3] != null) {
                rs.petitChanaux().addVert(rs.getPincesAvant()[3]);
            }
            rs.clearPincesAvant();

            mv.gotoPointMM(x, y, false, true, SensDeplacement.ARRIERE);

            step++;
            if (step > 2) {
                completed = true;
            }

        } catch (NoPathFoundException | AvoidingException e) {
            updateValidTime();
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
        } finally {
            rs.enablePinces();
            servos.moustachesFerme(true);
        }
    }
}

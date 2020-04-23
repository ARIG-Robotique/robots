package org.arig.robot.strategy.actions.active;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.Chenaux;
import org.arig.robot.model.ECouleurBouee;
import org.arig.robot.model.ETeam;
import org.arig.robot.model.Point;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.services.IPincesArriereService;
import org.arig.robot.services.ServosService;
import org.arig.robot.strategy.AbstractAction;
import org.arig.robot.system.ITrajectoryManager;
import org.arig.robot.utils.TableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class DeposePetitPort extends AbstractAction {

    @Autowired
    private ITrajectoryManager mv;

    @Autowired
    private IPincesArriereService pincesArriereService;

    @Autowired
    private RobotStatus rs;

    @Autowired
    private ServosService servos;

    @Autowired
    private TableUtils tableUtils;

    @Getter
    private boolean completed = false;

    private boolean moustacheFaites = false;

    private int step = 0;

    @Override
    public String name() {
        return "Dépose petit port";
    }

    @Override
    protected Point entryPoint() {
        double x = 1800;
        double y = 610;
        if (ETeam.JAUNE == rs.getTeam()) {
            x = 3000 - x;
        }

        return new Point(x, y);
    }

    @Override
    public int order() {
        Chenaux chenauxFuture = rs.petitChenaux().with(
                ArrayUtils.subarray(rs.getPincesAvant(), 0, 2),
                ArrayUtils.subarray(rs.getPincesAvant(), 2, 4)
        );
        List<ECouleurBouee> petitPortFutur = new ArrayList<>();

        if (!rs.pincesArriereEmpty()) {
            chenauxFuture.addVert(ArrayUtils.subarray(rs.getPincesArriere(), 0, 2));
            petitPortFutur.add(rs.getPincesArriere()[2]);
            chenauxFuture.addRouge(ArrayUtils.subarray(rs.getPincesArriere(), 3, 4));
        }

        if (!moustacheFaites) {
            chenauxFuture.addRouge(ECouleurBouee.ROUGE, ECouleurBouee.VERT);
            chenauxFuture.addVert(ECouleurBouee.ROUGE, ECouleurBouee.VERT);
        }

        int order = chenauxFuture.score() + petitPortFutur.size() - rs.petitChenaux().score();
        return order + tableUtils.alterOrder(entryPoint());
    }

    @Override
    public boolean isValid() {
        return isTimeValid() && !rs.inPort() && (!rs.pincesAvantEmpty() || !rs.pincesArriereEmpty() || (!moustacheFaites && rs.getRemainingTime() < 40000));
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

            // première dépose
            // gestion des bouées devant et sur les côtés
            if (!moustacheFaites) {
                servos.ascenseurAvantOuvertureMoustache(true);
                servos.moustachesOuvert(true);

                mv.gotoPointMM(x, 500, false);
                servos.moustachesPoussette(true);
                servos.moustachesOuvert(true);

                mv.gotoPointMM(x, 220, false);
                servos.moustachesPoussette(true);
                servos.moustachesOuvert(true);

                rs.petitChenaux().addRouge(ECouleurBouee.ROUGE, ECouleurBouee.VERT);
                rs.petitChenaux().addVert(ECouleurBouee.ROUGE, ECouleurBouee.VERT);

                moustacheFaites = true;
            } else {
                // déposes suivantes
                mv.gotoPointMM(x, 220 + step * 75, false);
            }

            if (!rs.pincesAvantEmpty()) {
                servos.ascenseurAvantBas(true);
                servos.pincesAvantOuvert(true);
                rs.petitChenaux().addRouge(ArrayUtils.subarray(rs.getPincesAvant(), 0, 2));
                rs.petitChenaux().addVert(ArrayUtils.subarray(rs.getPincesAvant(), 2, 4));
                rs.clearPincesAvant();
                step++;
            }

            mv.reculeMM(120);

            if (!rs.pincesArriereEmpty()) {
                // Dépose stock arrière si il y en as
                servos.moustachesFerme(false);
                mv.gotoOrientationDeg(90);
                pincesArriereService.deposeArrierePetitPort();
                step++;
            }

            if (step > 3) {
                completed = true;
            }

        } catch (NoPathFoundException | AvoidingException e) {
            updateValidTime();
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
        } finally {
            servos.moustachesFerme(true);
        }
    }
}

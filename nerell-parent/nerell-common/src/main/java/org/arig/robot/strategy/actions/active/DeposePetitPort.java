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
import org.arig.robot.model.NerellRobotStatus;
import org.arig.robot.model.Point;
import org.arig.robot.model.Position;
import org.arig.robot.model.enums.SensDeplacement;
import org.arig.robot.services.IPincesArriereService;
import org.arig.robot.services.IPincesAvantService;
import org.arig.robot.services.ServosService;
import org.arig.robot.strategy.actions.AbstractNerellAction;
import org.arig.robot.strategy.actions.active.AbstractDeposeGrandPortChenal.EPosition;
import org.arig.robot.system.ITrajectoryManager;
import org.arig.robot.utils.ConvertionRobotUnit;
import org.arig.robot.utils.TableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class DeposePetitPort extends AbstractNerellAction {

    @Autowired
    private ITrajectoryManager mv;

    @Autowired
    private IPincesAvantService pincesAvantService;

    @Autowired
    private IPincesArriereService pincesArriereService;

    @Autowired
    private NerellRobotStatus rs;

    @Autowired
    private ServosService servos;

    @Autowired
    private ConvertionRobotUnit conv;

    @Autowired
    @Qualifier("currentPosition")
    private Position position;

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
        double y = 620;
        if (ETeam.JAUNE == rs.getTeam()) {
            x = 3000 - x;
        }

        return new Point(x, y);
    }

    @Override
    public int order() {
        Chenaux chenauxFuture = rs.petitChenaux().with(
                ArrayUtils.subarray(rs.pincesAvant(), 0, 2),
                ArrayUtils.subarray(rs.pincesAvant(), 2, 4)
        );
        List<ECouleurBouee> petitPortFutur = new ArrayList<>();

        if (!rs.pincesArriereEmpty() && rs.grandChenaux().deposeArriereImpossible()) {
            chenauxFuture.addVert(ArrayUtils.subarray(rs.pincesArriere(), 0, 2));
            petitPortFutur.add(rs.pincesArriere()[2]);
            chenauxFuture.addRouge(ArrayUtils.subarray(rs.pincesArriere(), 3, 4));
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
            final Point entry = entryPoint();
            final double x = entry.getX();
            final double baseYStep = 240;

            mv.setVitesse(IConstantesNerellConfig.vitessePath, IConstantesNerellConfig.vitesseOrientation);

            SensDeplacement sensEntry = SensDeplacement.AVANT;
            if (moustacheFaites && rs.pincesAvantEmpty()) {
                sensEntry = SensDeplacement.ARRIERE;
            }
            mv.pathTo(entry, sensEntry);
            rs.disableAvoidance();

            boolean deposePinceDone = false;
            boolean moustacheAtStart = moustacheFaites;

            // première dépose
            // gestion des bouées devant et sur les côtés
            if (!moustacheFaites) {
                mv.gotoOrientationDeg(-90);

                if (rs.pincesAvantEmpty()) {
                    servos.ascenseurAvantBas(true);
                } else {
                    servos.ascenseurAvantOuvertureMoustache(true);
                }
                servos.moustachesOuvert(true);

                // ouvre les deux pinces centrale si vide pour ne pas faire tomber les deux bouées de devant
                for (int i = 1; i < 3; i++) {
                    if (rs.pincesAvant()[i] == null) {
                        servos.pinceAvantOuvert(i, false);
                    }
                }

                mv.setVitesse(IConstantesNerellConfig.vitesseSuperLente, IConstantesNerellConfig.vitesseOrientation);

                mv.gotoPointMM(x, 540, false);
                servos.moustachesPoussette(true);
                servos.moustachesOuvert(false);

                mv.gotoPointMM(x, baseYStep, false);
                moustacheFaites = true;

                rs.petitChenaux().addRouge(ECouleurBouee.ROUGE);
                rs.petitChenaux().addVert(ECouleurBouee.VERT);

                mv.setVitesse(IConstantesNerellConfig.vitessePath, IConstantesNerellConfig.vitesseOrientationBasse);

                mv.tourneDeg(10);
                rs.petitChenaux().addVert(ECouleurBouee.ROUGE);

                mv.tourneDeg(-20);
                rs.petitChenaux().addRouge(ECouleurBouee.VERT);

                mv.gotoOrientationDeg(-90);

            } else if (!rs.pincesAvantEmpty()) {
                // déposes suivantes
                mv.gotoPointMM(x, baseYStep + step * 70, false);
                mv.gotoOrientationDeg(-90);
            }

            if (!rs.pincesAvantEmpty()) {
                pincesAvantService.deposePetitPort();
                step++;
                mv.reculeMM(80);
                pincesAvantService.finaliseDepose();
                deposePinceDone = true;
            }

            if (!moustacheAtStart && moustacheFaites) {
                mv.reculeMM(deposePinceDone ? 70 : 150);
                servos.moustachesFerme(false);
            }

            if (!rs.pincesArriereEmpty()) {
                // Dépose stock arrière si il y en as
                mv.gotoPointMM(x, baseYStep + (deposePinceDone ? 80 : 150) + step * 70, false);
                mv.gotoOrientationDeg(90);
                pincesArriereService.deposePetitPort();
                deposePinceDone = true;
                step++;
            }

            if (!deposePinceDone) {
                mv.reculeMM(150);
            }

            if (step > 3) {
                completed = true;
            }

        } catch (NoPathFoundException | AvoidingException e) {
            if (conv.pulseToDeg(position.getAngle()) < 0) {
                try {
                    mv.reculeMM(200);
                } catch (AvoidingException eRecul) {
                    log.warn("Condition d'échappement en erreur sur le recul");
                }
            }
            updateValidTime();
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
        } finally {
            pincesAvantService.finaliseDepose();
            servos.moustachesFerme(false);
        }
    }
}

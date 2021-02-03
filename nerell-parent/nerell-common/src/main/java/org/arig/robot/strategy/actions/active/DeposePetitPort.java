package org.arig.robot.strategy.actions.active;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.Chenaux;
import org.arig.robot.model.ECouleurBouee;
import org.arig.robot.model.ETeam;
import org.arig.robot.model.Point;
import org.arig.robot.model.enums.GotoOption;
import org.arig.robot.services.IPincesArriereService;
import org.arig.robot.services.IPincesAvantService;
import org.arig.robot.strategy.actions.AbstractNerellAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class DeposePetitPort extends AbstractNerellAction {

    @Autowired
    private IPincesAvantService pincesAvantService;

    @Autowired
    private IPincesArriereService pincesArriereService;

    @Autowired
    private Bouee8 bouee8;

    @Autowired
    private Bouee9 bouee9;

    private boolean moustacheFaites = false;

    private int step = 0;

    @Override
    public String name() {
        return "Dépose petit port";
    }

    @Override
    public Point entryPoint() {
        double x = 1800;
        double y = 620;
        if (ETeam.JAUNE == rs.team()) {
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
        boolean inPort = false;

        try {
            if (rs.team() == ETeam.JAUNE && bouee8.isValid()) {
                bouee8.execute();
            } else if (rs.team() == ETeam.BLEU && bouee9.isValid()) {
                bouee9.execute();
            }

            final Point entry = entryPoint();
            final double x = entry.getX();
            final double baseYStep = 260;

            mv.setVitesse(IConstantesNerellConfig.vitessePath, IConstantesNerellConfig.vitesseOrientation);

            GotoOption sensEntry = GotoOption.AVANT;
            if (moustacheFaites && rs.pincesAvantEmpty()) {
                sensEntry = GotoOption.ARRIERE;
            }
            mv.pathTo(entry, sensEntry);
            rs.disableAvoidance();
            inPort = true;

            // on a shooté la bouée
            if (rs.team() == ETeam.JAUNE) {
                rs.bouee(8).setPrise();
            } else {
                rs.bouee(9).setPrise();
            }

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

                mv.gotoPoint(x, 540, GotoOption.SANS_ORIENTATION);
                servos.moustachesPoussette(true);
                servos.moustachesOuvert(false);

                mv.gotoPoint(x, baseYStep, GotoOption.SANS_ORIENTATION);
                moustacheFaites = true;

                rs.petitChenaux().addRouge(ECouleurBouee.ROUGE);
                rs.petitChenaux().addVert(ECouleurBouee.VERT);

                mv.setVitesse(IConstantesNerellConfig.vitessePath, IConstantesNerellConfig.vitesseOrientationBasse);

                mv.tourneDeg(15);
                rs.petitChenaux().addVert(ECouleurBouee.ROUGE);

                mv.tourneDeg(-30);
                rs.petitChenaux().addRouge(ECouleurBouee.VERT);

                mv.gotoOrientationDeg(-90);

            } else if (!rs.pincesAvantEmpty()) {
                // déposes suivantes
                mv.gotoPoint(x, baseYStep + step * 70, GotoOption.SANS_ORIENTATION);
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
                mv.gotoPoint(x, baseYStep + (deposePinceDone ? 80 : 150) + step * 70, GotoOption.SANS_ORIENTATION);
                mv.gotoOrientationDeg(90);
                pincesArriereService.deposePetitPort();
                deposePinceDone = true;
                step++;
            }

            if (!deposePinceDone) {
                inPort = false;
                mv.reculeMM(150);
            }

            if (step > 2) {
                complete();
            }

        } catch (NoPathFoundException | AvoidingException e) {
            if (conv.pulseToDeg(position.getAngle()) < 0 && inPort) {
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

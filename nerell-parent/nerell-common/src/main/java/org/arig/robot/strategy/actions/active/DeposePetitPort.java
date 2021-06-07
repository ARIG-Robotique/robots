package org.arig.robot.strategy.actions.active;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.Chenaux;
import org.arig.robot.model.ECouleurBouee;
import org.arig.robot.model.ETeam;
import org.arig.robot.model.Point;
import org.arig.robot.model.enums.GotoOption;
import org.arig.robot.services.INerellPincesArriereService;
import org.arig.robot.services.INerellPincesAvantService;
import org.arig.robot.strategy.actions.AbstractNerellAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class DeposePetitPort extends AbstractNerellAction {

    @Autowired
    private INerellPincesAvantService pincesAvantService;

    @Autowired
    private INerellPincesArriereService pincesArriereService;

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
        Chenaux chenauxFuture = rs.clonePetitChenaux();
        int currentScoreChenaux = chenauxFuture.score();
        List<ECouleurBouee> petitPortFutur = new ArrayList<>();

        chenauxFuture.addRouge(ArrayUtils.subarray(rs.pincesAvant(), 0, 2));
        chenauxFuture.addVert(ArrayUtils.subarray(rs.pincesAvant(), 2, 4));

        if (!rs.pincesArriereEmpty() && !rs.deposeArriereGrandChenalPossible()) {
            chenauxFuture.addVert(ArrayUtils.subarray(rs.pincesArriere(), 0, 2));
            petitPortFutur.add(rs.pincesArriere()[2]);
            chenauxFuture.addRouge(ArrayUtils.subarray(rs.pincesArriere(), 3, 4));
        }

        if (!moustacheFaites) {
            chenauxFuture.addRouge(ECouleurBouee.ROUGE, ECouleurBouee.VERT);
            chenauxFuture.addVert(ECouleurBouee.ROUGE, ECouleurBouee.VERT);
        }

        int order = chenauxFuture.score() + petitPortFutur.size() - currentScoreChenaux;
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
            final double baseYStep = 230;

            mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());

            GotoOption sensEntry = GotoOption.AVANT;
            if (moustacheFaites && rs.pincesAvantEmpty()) {
                sensEntry = GotoOption.ARRIERE;
            }
            mv.pathTo(entry, sensEntry);
            rs.disableAvoidance();
            inPort = true;

            // on a shooté la bouée
            if (rs.team() == ETeam.JAUNE) {
                rs.boueePrise(8);
            } else {
                rs.boueePrise(9);
            }

            rs.disablePincesAvant(); // Pour ne pas faire de comptage supplémentaire si il reste des emplacements vide
            boolean deposePinceDone = false;
            boolean moustacheAtStart = moustacheFaites;

            // première dépose
            // gestion des bouées devant et sur les côtés
            if (!moustacheFaites) {
                mv.gotoOrientationDeg(-90);

                servos.ascenseursAvantHaut(true);
                servos.moustachesOuvert(true);

                mv.setVitesse(robotConfig.vitesse(50), robotConfig.vitesseOrientation());

                mv.gotoPoint(x, 540, GotoOption.SANS_ORIENTATION);
                servos.moustachesPoussette(true);
                servos.moustachesOuvertSpecial(false);

                mv.gotoPoint(x, baseYStep, GotoOption.SANS_ORIENTATION);
                moustacheFaites = true;

                rs.deposePetitChenalRouge(ECouleurBouee.ROUGE);
                rs.deposePetitChenalVert(ECouleurBouee.VERT);

                servos.moustachesOuvert(true); // En fait ici c'est pour "fermer" les bouée du fond
                rs.deposePetitChenalVert(ECouleurBouee.ROUGE);
                rs.deposePetitChenalRouge(ECouleurBouee.VERT);

            } else if (!rs.pincesAvantEmpty()) {
                // déposes suivantes
                mv.gotoPoint(x, baseYStep + step * 70, GotoOption.SANS_ORIENTATION);
                mv.gotoOrientationDeg(-90);
            }

            if (!rs.pincesAvantEmpty()) {
                pincesAvantService.deposePetitPort();
                step++;
                mv.reculeMM(rs.pincesArriereEmpty() ? 150 : 80);
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
            servos.moustachesFerme(false);
        }
    }
}

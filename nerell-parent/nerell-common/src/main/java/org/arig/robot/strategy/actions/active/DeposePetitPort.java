package org.arig.robot.strategy.actions.active;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.arig.robot.constants.IEurobotConfig;
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
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class DeposePetitPort extends AbstractNerellAction {

    @Autowired
    private INerellPincesAvantService pincesAvantService;

    @Autowired
    private INerellPincesArriereService pincesArriereService;

    @Autowired
    private NerellBouee8 bouee8;

    @Autowired
    private NerellBouee9 bouee9;

    private boolean moustacheFaites = false;

    private int step = 0;

    @Override
    public String name() {
        return IEurobotConfig.ACTION_DEPOSE_PETIT_PORT;
    }

    @Getter
    @Accessors(fluent = true)
    public List<String> blockingActions = Arrays.asList(
            IEurobotConfig.ACTION_NETTOYAGE_PETIT_PORT
    );

    @Override
    public Point entryPoint() {
        double x = 1800;
        double y = 620;
        if (ETeam.JAUNE == rsNerell.team()) {
            x = 3000 - x;
        }

        return new Point(x, y);
    }

    @Override
    public int order() {
        Chenaux chenauxFuture = rsNerell.clonePetitChenaux();
        int currentScoreChenaux = chenauxFuture.score();
        List<ECouleurBouee> petitPortFutur = new ArrayList<>();

        chenauxFuture.addRouge(ArrayUtils.subarray(rsNerell.pincesAvant(), 0, 2));
        chenauxFuture.addVert(ArrayUtils.subarray(rsNerell.pincesAvant(), 2, 4));

        if (!rsNerell.pincesArriereEmpty() && !rsNerell.deposeArriereGrandChenalPossible()) {
            chenauxFuture.addVert(ArrayUtils.subarray(rsNerell.pincesArriere(), 0, 2));
            petitPortFutur.add(rsNerell.pincesArriere()[2]);
            chenauxFuture.addRouge(ArrayUtils.subarray(rsNerell.pincesArriere(), 3, 4));
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
        return isTimeValid() && !rsNerell.inPort() && (!rsNerell.pincesAvantEmpty() || !rsNerell.pincesArriereEmpty() || (!moustacheFaites && rsNerell.getRemainingTime() < 40000));
    }

    @Override
    public void execute() {
        boolean inPort = false;

        try {
            if (rsNerell.team() == ETeam.JAUNE && bouee8.isValid()) {
                bouee8.execute();
            } else if (rsNerell.team() == ETeam.BLEU && bouee9.isValid()) {
                bouee9.execute();
            }

            final Point entry = entryPoint();
            final double x = entry.getX();
            final double baseYStep = 230;

            mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());

            GotoOption sensEntry = GotoOption.AVANT;
            if (moustacheFaites && rsNerell.pincesAvantEmpty()) {
                sensEntry = GotoOption.ARRIERE;
            }
            mv.pathTo(entry, sensEntry);
            rsNerell.disableAvoidance();
            inPort = true;

            // on a shooté la bouée
            if (rsNerell.team() == ETeam.JAUNE) {
                group.boueePrise(8);
            } else {
                group.boueePrise(9);
            }

            rsNerell.disablePincesAvant(); // Pour ne pas faire de comptage supplémentaire si il reste des emplacements vide
            boolean deposePinceDone = false;
            boolean moustacheAtStart = moustacheFaites;

            // première dépose
            // gestion des bouées devant et sur les côtés
            if (!moustacheFaites) {
                mv.gotoOrientationDeg(-90);

                servosNerell.ascenseursAvantHaut(true);
                servosNerell.moustachesOuvert(true);

                mv.setVitesse(robotConfig.vitesse(50), robotConfig.vitesseOrientation());

                mv.gotoPoint(x, 540, GotoOption.SANS_ORIENTATION);
                servosNerell.moustachesPoussette(true);
                servosNerell.moustachesOuvertSpecial(false);

                mv.gotoPoint(x, baseYStep, GotoOption.SANS_ORIENTATION);
                moustacheFaites = true;

                group.deposePetitChenalRouge(ECouleurBouee.ROUGE);
                group.deposePetitChenalVert(ECouleurBouee.VERT);

                servosNerell.moustachesOuvert(true); // En fait ici c'est pour "fermer" les bouée du fond
                group.deposePetitChenalVert(ECouleurBouee.ROUGE);
                group.deposePetitChenalRouge(ECouleurBouee.VERT);

            } else if (!rsNerell.pincesAvantEmpty()) {
                // déposes suivantes
                mv.gotoPoint(x, baseYStep + step * 70, GotoOption.SANS_ORIENTATION);
                mv.gotoOrientationDeg(-90);
            }

            if (!rsNerell.pincesAvantEmpty()) {
                pincesAvantService.deposePetitPort();
                step++;
                mv.reculeMM(rsNerell.pincesArriereEmpty() ? 150 : 80);
                deposePinceDone = true;
            }

            if (!moustacheAtStart && moustacheFaites) {
                mv.reculeMM(deposePinceDone ? 70 : 150);
                servosNerell.moustachesFerme(false);
            }

            if (!rsNerell.pincesArriereEmpty()) {
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
            log.error("Erreur d'exécution de l'action : {}", e.toString());
        } finally {
            servosNerell.moustachesFerme(false);
        }
    }
}

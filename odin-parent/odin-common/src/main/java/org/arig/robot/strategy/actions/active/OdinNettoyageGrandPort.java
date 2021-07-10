package org.arig.robot.strategy.actions.active;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IEurobotConfig;
import org.arig.robot.constants.IOdinConstantesConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.ECouleurBouee;
import org.arig.robot.model.EStrategy;
import org.arig.robot.model.ETeam;
import org.arig.robot.model.Point;
import org.arig.robot.model.enums.GotoOption;
import org.arig.robot.services.AbstractOdinPincesArriereService;
import org.arig.robot.services.AbstractOdinPincesAvantService;
import org.arig.robot.strategy.actions.AbstractOdinAction;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OdinNettoyageGrandPort extends AbstractOdinAction {

    @Autowired
    private AbstractOdinPincesAvantService pincesAvantService;

    @Autowired
    private AbstractOdinPincesArriereService pincesArriereService;

    @Autowired
    private OdinDeposeGrandPort deposeGrandPort;

    @Override
    public String name() {
        return IEurobotConfig.ACTION_NETTOYAGE_GRAND_PORT;
    }

    @Override
    public Point entryPoint() {
        return new Point(getX(255), 1200);
    }

    @Override
    public boolean isValid() {
        return isTimeValid() && !rsOdin.inPort() &&
                rs.getRemainingTime() > IEurobotConfig.invalidPriseRemainingTime &&
                rsOdin.pincesArriereEmpty() && rsOdin.pincesArriereEmpty();
    }

    @Override
    public int order() {
        return 1000;
    }

    @Override
    public void execute() {
        try {
            final Point entry = entryPoint();
            final int pctVitessePriseBouee = 20;

            if (tableUtils.distance(entry) > 300) {
                mv.pathTo(entry);
            }

            rsOdin.disableAvoidance();

            rsOdin.enablePincesAvant();
            rsOdin.enablePincesArriere();

            // prise 4/16
            if (rsOdin.team() == ETeam.BLEU) {
                pincesAvantService.setExpected(ECouleurBouee.VERT, 0);
            } else {
                pincesAvantService.setExpected(ECouleurBouee.ROUGE, 1);
            }

            mv.setVitesse(robotConfig.vitesse(pctVitessePriseBouee), robotConfig.vitesseOrientation());
            mv.gotoPoint(getX(255), 800, GotoOption.AVANT);
            ThreadUtils.sleep(IOdinConstantesConfig.WAIT_POMPES);
            if (rsOdin.team() == ETeam.BLEU) {
                group.boueePrise(4); // vert avant gauche
            } else {
                group.boueePrise(16); // rouge avant droite
            }

            // prise 3/15
            if (rsOdin.team() == ETeam.BLEU) {
                pincesArriereService.setExpected(ECouleurBouee.ROUGE, 0);
            } else {
                pincesArriereService.setExpected(ECouleurBouee.VERT, 1);
            }

            mv.gotoPoint(getX(408), 927, GotoOption.ARRIERE);
            ThreadUtils.sleep(IOdinConstantesConfig.WAIT_POMPES);
            if (rsOdin.team() == ETeam.BLEU) {
                group.boueePrise(3); // rouge arriere gauche
            } else {
                group.boueePrise(15); // vert arriere droite
            }

            if (rsOdin.team() == ETeam.BLEU && (rs.boueePresente(2) || rsOdin.boueePresente(1)) ||
                    rsOdin.team() == ETeam.JAUNE && (rs.boueePresente(14) || rsOdin.boueePresente(13))) {
                mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());
                mv.gotoPoint(getX(412), 1390, GotoOption.AVANT);

                // prise 2/14
                if (rsOdin.team() == ETeam.BLEU) {
                    pincesAvantService.setExpected(ECouleurBouee.VERT, 1);
                } else {
                    pincesAvantService.setExpected(ECouleurBouee.ROUGE, 0);
                }

                mv.setVitesse(robotConfig.vitesse(pctVitessePriseBouee), robotConfig.vitesseOrientation());
                mv.gotoPoint(getX(412), 1430, GotoOption.AVANT);
                ThreadUtils.sleep(IOdinConstantesConfig.WAIT_POMPES);
                if (rsOdin.team() == ETeam.BLEU) {
                    group.boueePrise(2); // vert avant droite
                } else {
                    group.boueePrise(14); // rouge avant gauche
                }

                // prise 1/13
                if (rsOdin.team() == ETeam.BLEU) {
                    pincesArriereService.setExpected(ECouleurBouee.ROUGE, 1);
                } else {
                    pincesArriereService.setExpected(ECouleurBouee.VERT, 0);
                }

                mv.gotoPoint(getX(337), 1593, GotoOption.ARRIERE);
                ThreadUtils.sleep(IOdinConstantesConfig.WAIT_POMPES);
                if (rsOdin.team() == ETeam.BLEU) {
                    group.boueePrise(1); // rouge arriere droite
                } else {
                    group.boueePrise(13); // vert arriere gauche
                }
            }

            if (deposeGrandPort.isValid()) {
                deposeGrandPort.setDisablePathTo(true);
                deposeGrandPort.execute();
            }

            complete();

        } catch (NoPathFoundException | AvoidingException e) {
            updateValidTime();
            log.error("Erreur d'exécution de l'action : {}", e.toString());
        } finally {
            pincesArriereService.setExpected(null, -1);
            pincesAvantService.setExpected(null, -1);
        }
    }
}

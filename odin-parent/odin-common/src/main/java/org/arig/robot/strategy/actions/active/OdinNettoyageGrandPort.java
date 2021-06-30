package org.arig.robot.strategy.actions.active;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesOdinConfig;
import org.arig.robot.constants.IEurobotConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.model.ECouleurBouee;
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

    @Override
    public String name() {
        return IEurobotConfig.ACTION_NETTOYAGE_GRAND_PORT;
    }

    @Override
    public Point entryPoint() {
        return new Point(getX(255), 1000);
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public int order() {
        return 1000;
    }

    @Override
    public void execute() {
        try {
            // TODO Optim d'entry point avec distance comme pour le phare et les dépose chenaux de Nerell
            final Point entry = entryPoint();

            rsOdin.disableAvoidance();

            rsOdin.enablePincesAvant();
            rsOdin.enablePincesArriere();

            // prise 4/16
            if (rsOdin.team() == ETeam.BLEU) {
                pincesAvantService.setExpected(ECouleurBouee.VERT);
            } else {
                pincesAvantService.setExpected(ECouleurBouee.ROUGE);
            }

            mv.setVitesse(robotConfig.vitesse(30), robotConfig.vitesseOrientation());
            mv.gotoPoint(getX(255), 800, GotoOption.AVANT);
            ThreadUtils.sleep(IConstantesOdinConfig.WAIT_POMPES);
            if (rsOdin.team() == ETeam.BLEU) {
                group.boueePrise(4); // vert avant gauche
            } else {
                group.boueePrise(16); // rouge avant droite
            }

            // prise 3/15
            if (rsOdin.team() == ETeam.BLEU) {
                pincesArriereService.setExpected(ECouleurBouee.ROUGE);
            } else {
                pincesArriereService.setExpected(ECouleurBouee.VERT);
            }

            mv.gotoPoint(getX(408), 927, GotoOption.ARRIERE);
            ThreadUtils.sleep(IConstantesOdinConfig.WAIT_POMPES);
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
                    pincesAvantService.setExpected(ECouleurBouee.VERT);
                } else {
                    pincesAvantService.setExpected(ECouleurBouee.ROUGE);
                }

                mv.setVitesse(robotConfig.vitesse(30), robotConfig.vitesseOrientation());
                mv.gotoPoint(getX(412), 1430, GotoOption.AVANT);
                ThreadUtils.sleep(IConstantesOdinConfig.WAIT_POMPES);
                if (rsOdin.team() == ETeam.BLEU) {
                    group.boueePrise(2); // vert avant droite
                } else {
                    group.boueePrise(14); // rouge avant gauche
                }

                // prise 1/13
                if (rsOdin.team() == ETeam.BLEU) {
                    pincesArriereService.setExpected(ECouleurBouee.ROUGE);
                } else {
                    pincesArriereService.setExpected(ECouleurBouee.VERT);
                }

                mv.gotoPoint(getX(337), 1593, GotoOption.ARRIERE);
                ThreadUtils.sleep(IConstantesOdinConfig.WAIT_POMPES);
                if (rsOdin.team() == ETeam.BLEU) {
                    group.boueePrise(1); // rouge arriere droite
                } else {
                    group.boueePrise(13); // vert arriere gauche
                }
            }

            mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());

            // Dépose nord (face avant)
            if (!rsOdin.pincesAvantEmpty()) {
                mv.gotoPoint(getX(180), 1490, GotoOption.AVANT);
                mv.gotoPoint(getX(150), 1490, GotoOption.AVANT);
                if (rsOdin.team() == ETeam.BLEU) {
                    pincesAvantService.deposeGrandChenalVert();
                } else {
                    pincesAvantService.deposeGrandChenalRouge();
                }
                mv.gotoPoint(getX(250), 1490, GotoOption.ARRIERE);
            }

            // Dépose sud (face arrière)
            if (!rsOdin.pincesArriereEmpty()) {
                mv.gotoPoint(getX(180), 920, GotoOption.ARRIERE);
                mv.gotoPoint(getX(150), 920, GotoOption.ARRIERE);
                if (rsOdin.team() == ETeam.BLEU) {
                    pincesArriereService.deposeGrandChenalRouge();
                } else {
                    pincesArriereService.deposeGrandChenalVert();
                }
                mv.gotoPoint(getX(250), 920, GotoOption.AVANT);
            }

            complete();

        } catch (AvoidingException e) {
            updateValidTime();
            log.error("Erreur d'exécution de l'action : {}", e.toString());
        } finally {
            pincesArriereService.setExpected(null);
            pincesAvantService.setExpected(null);
        }
    }
}

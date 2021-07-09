package org.arig.robot.strategy.actions.active;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IEurobotConfig;
import org.arig.robot.constants.IOdinConstantesConfig;
import org.arig.robot.exception.AvoidingException;
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

    @Override
    public String name() {
        return IEurobotConfig.ACTION_NETTOYAGE_GRAND_PORT;
    }

    @Override
    public Point entryPoint() {
        return new Point(getX(255), rs.strategy() == EStrategy.FINALE ? 1400 : 1000);
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
        if (rs.strategy() == EStrategy.FINALE) {
            executeNord();
        } else {
            executeSud();
        }
    }

    private void executeSud() {
        try {
            // TODO Optim d'entry point avec distance comme pour le phare et les dépose chenaux de Nerell
            final Point entry = entryPoint();
            final int pctVitessePriseBouee = 20;

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

            mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());

            // Dépose nord (face avant)
            if (!rsOdin.pincesAvantEmpty()) {
                mv.gotoPoint(getX(235), 1490, GotoOption.AVANT);
                mv.gotoOrientationDeg(rsOdin.team() == ETeam.BLEU ? 180 : 0);
                if (rsOdin.team() == ETeam.BLEU) {
                    pincesAvantService.deposeFondGrandChenalVert();
                } else {
                    pincesAvantService.deposeFondGrandChenalRouge();
                }
            }

            // Dépose sud (face arrière)
            if (!rsOdin.pincesArriereEmpty()) {
                mv.gotoPoint(getX(235), 920, GotoOption.ARRIERE);
                mv.gotoOrientationDeg(rsOdin.team() == ETeam.BLEU ? 0 : 180);
                if (rsOdin.team() == ETeam.BLEU) {
                    pincesArriereService.deposeFondGrandChenalRouge();
                } else {
                    pincesArriereService.deposeFondGrandChenalVert();
                }
            }

            complete();

        } catch (AvoidingException e) {
            updateValidTime();
            log.error("Erreur d'exécution de l'action : {}", e.toString());
        } finally {
            pincesArriereService.setExpected(null, -1);
            pincesAvantService.setExpected(null, -1);
        }
    }

    private void executeNord() {
        try {
            // TODO Optim d'entry point avec distance comme pour le phare et les dépose chenaux de Nerell
            final Point entry = entryPoint();
            final int pctVitessePriseBouee = 20;

            rsOdin.disableAvoidance();

            rsOdin.enablePincesAvant();
            rsOdin.enablePincesArriere();

            // prise 1/13
            if (rsOdin.team() == ETeam.BLEU) {
                pincesAvantService.setExpected(ECouleurBouee.ROUGE, 1);
            } else {
                pincesAvantService.setExpected(ECouleurBouee.VERT, 0);
            }

            mv.setVitesse(robotConfig.vitesse(pctVitessePriseBouee), robotConfig.vitesseOrientation());
            mv.gotoPoint(getX(255), 1600, GotoOption.AVANT);
            ThreadUtils.sleep(IOdinConstantesConfig.WAIT_POMPES);
            if (rsOdin.team() == ETeam.BLEU) {
                group.boueePrise(1); // rouge avant droite
            } else {
                group.boueePrise(13); // vert avant gauche
            }

            // prise 2/14
            if (rsOdin.team() == ETeam.BLEU) {
                pincesArriereService.setExpected(ECouleurBouee.VERT, 1);
            } else {
                pincesArriereService.setExpected(ECouleurBouee.ROUGE, 0);
            }

            mv.gotoPoint(getX(408), 1473, GotoOption.ARRIERE);
            ThreadUtils.sleep(IOdinConstantesConfig.WAIT_POMPES);
            if (rsOdin.team() == ETeam.BLEU) {
                group.boueePrise(2); // vert arriere droite
            } else {
                group.boueePrise(14); // rouge arriere gauche
            }

            if (rsOdin.team() == ETeam.BLEU && (rs.boueePresente(3) || rsOdin.boueePresente(4)) ||
                    rsOdin.team() == ETeam.JAUNE && (rs.boueePresente(15) || rsOdin.boueePresente(16))) {
                mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());
                mv.gotoPoint(getX(412), 1010, GotoOption.AVANT);

                // prise 3/15
                if (rsOdin.team() == ETeam.BLEU) {
                    pincesAvantService.setExpected(ECouleurBouee.ROUGE, 0);
                } else {
                    pincesAvantService.setExpected(ECouleurBouee.VERT, 1);
                }

                mv.setVitesse(robotConfig.vitesse(pctVitessePriseBouee), robotConfig.vitesseOrientation());
                mv.gotoPoint(getX(412), 970, GotoOption.AVANT);
                ThreadUtils.sleep(IOdinConstantesConfig.WAIT_POMPES);
                if (rsOdin.team() == ETeam.BLEU) {
                    group.boueePrise(3); // rouge avant gauche
                } else {
                    group.boueePrise(15); // vert avant droit
                }

                // prise 4/16
                if (rsOdin.team() == ETeam.BLEU) {
                    pincesArriereService.setExpected(ECouleurBouee.VERT, 0);
                } else {
                    pincesArriereService.setExpected(ECouleurBouee.ROUGE, 1);
                }

                mv.gotoPoint(getX(337), 807, GotoOption.ARRIERE);
                ThreadUtils.sleep(IOdinConstantesConfig.WAIT_POMPES);
                if (rsOdin.team() == ETeam.BLEU) {
                    group.boueePrise(4); // vert arriere gauche
                } else {
                    group.boueePrise(16); // rouge arriere droit
                }
            }

            mv.setVitesse(robotConfig.vitesse(), robotConfig.vitesseOrientation());

            // Dépose sud (face avant)
            if (!rsOdin.pincesAvantEmpty()) {
                mv.gotoPoint(getX(235), 910, GotoOption.AVANT);
                mv.gotoOrientationDeg(rsOdin.team() == ETeam.BLEU ? 180 : 0);
                if (rsOdin.team() == ETeam.BLEU) {
                    pincesAvantService.deposeFondGrandChenalRouge();
                } else {
                    pincesAvantService.deposeFondGrandChenalVert();
                }
            }

            // Dépose nord (face arrière)
            if (!rsOdin.pincesArriereEmpty()) {
                mv.gotoPoint(getX(235), 1480, GotoOption.ARRIERE);
                mv.gotoOrientationDeg(rsOdin.team() == ETeam.BLEU ? 0 : 180);
                if (rsOdin.team() == ETeam.BLEU) {
                    pincesArriereService.deposeFondGrandChenalVert();
                } else {
                    pincesArriereService.deposeFondGrandChenalRouge();
                }
            }

            complete();

        } catch (AvoidingException e) {
            updateValidTime();
            log.error("Erreur d'exécution de l'action : {}", e.toString());
        } finally {
            pincesArriereService.setExpected(null, -1);
            pincesAvantService.setExpected(null, -1);
        }
    }
}

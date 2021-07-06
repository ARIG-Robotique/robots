package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.INerellConstantesServos;
import org.arig.robot.model.ECouleurBouee;
import org.arig.robot.model.GrandChenaux;
import org.arig.robot.model.NerellRobotStatus;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

@Slf4j
public abstract class AbstractNerellPincesArriereService implements INerellPincesArriereService {

    @Autowired
    private NerellServosService srv;

    @Autowired
    private NerellRobotStatus rs;

    @Autowired
    private INerellIOService io;

    @Autowired
    private RobotGroupService group;

    /**
     * Prises des bouees dans un écueil
     */
    @Override
    public boolean preparePriseEcueil() {
        srv.pincesArriereOuvert(false);
        srv.pivotArriereOuvert(false);
        srv.ascenseurArriereEcueil(false);

        return true;
    }

    @Override
    public boolean finalisePriseEcueil(ECouleurBouee... bouees) {
        Assert.isTrue(bouees.length == 5, "Paramètre bouees invalide");

        srv.pincesArriereFerme(true);
        srv.ascenseurArriereHaut(true);
        srv.pivotArriereFerme(false);

        if (io.presencePinceArriere1()) {
            rs.pinceArriere(0, bouees[0]);
        }
        if (io.presencePinceArriere2()) {
            rs.pinceArriere(1, bouees[1]);
        }
        if (io.presencePinceArriere3()) {
            rs.pinceArriere(2, bouees[2]);
        }
        if (io.presencePinceArriere4()) {
            rs.pinceArriere(3, bouees[3]);
        }
        if (io.presencePinceArriere5()) {
            rs.pinceArriere(4, bouees[4]);
        }

        return true;
    }

    /**
     * Déposer la pince arrière dans un chenal
     *
     * @param couleurChenal Couleur du chenal dans lequel a lieu la dépose
     */
    @Override
    public boolean deposeGrandChenal(final ECouleurBouee couleurChenal, final boolean partielle) {
        ECouleurBouee[] boueesPosees = new ECouleurBouee[]{null, null, null, null, null};

        if (partielle) {
            deposeTable(couleurChenal);

            for (int i = 0; i < 5; i++) {
                final ECouleurBouee couleurPince = rs.pincesArriere()[i];
                if (couleurPince == couleurChenal || couleurPince == ECouleurBouee.INCONNU) {
                    boueesPosees[i] = couleurPince;
                    rs.pinceArriere(i, null);
                }
            }

        } else {
            deposeTable(null);

            System.arraycopy(rs.pincesArriere(), 0, boueesPosees, 0, 5);
            rs.clearPincesArriere();
        }

        if (couleurChenal == ECouleurBouee.ROUGE) {
            group.deposeGrandChenalRouge(GrandChenaux.Line.A, boueesPosees);
        } else {
            group.deposeGrandChenalVert(GrandChenaux.Line.A, boueesPosees);
        }

        return true;
    }

    @Override
    public void deposeGrandPort() {
        deposeTable(null);

        group.deposeGrandPort(rs.pincesArriere());
        rs.clearPincesArriere();
    }

    /**
     * Déposer la pince arrière dans le petit port
     */
    @Override
    public boolean deposePetitPort() {
        deposeTable(null);

        group.deposePetitChenalVert(rs.pincesArriere()[0], rs.pincesArriere()[1]);
        group.deposePetitPort(rs.pincesArriere()[2]);
        group.deposePetitChenalRouge(rs.pincesArriere()[3], rs.pincesArriere()[4]);

        rs.clearPincesArriere();

        return true;
    }

    /**
     * @param couleurBouee couleur pour une dépose partielle ou null
     */
    private void deposeTable(final ECouleurBouee couleurBouee) {
        srv.pivotArriereOuvert(true);
        srv.ascenseurArriereTable(true);

        if (couleurBouee == null) {
            // dépose tout
            srv.pincesArriereOuvert(true);
        } else {
            // dépose la bonne couleur et les inconnus
            for (int i = 0; i < rs.pincesArriere().length; i++) {
                final ECouleurBouee couleurPince = rs.pincesArriere()[i];
                if (couleurPince == couleurBouee || couleurPince == ECouleurBouee.INCONNU) {
                    srv.pinceArriereOuvert(i, false);
                }
            }
            ThreadUtils.sleep(INerellConstantesServos.WAIT_PINCE_ARRIERE);
        }

        srv.ascenseurArriereEcueil(true);
        srv.pivotArriereFerme(false);
        srv.pincesArriereFerme(false);
        srv.ascenseurArriereHaut(false);
    }

    @Override
    public void finalizeDeposeTableEchange() {
        srv.pivotArriereFerme(false);
        srv.pincesArriereFerme(false);
        srv.ascenseurArriereHaut(false);

        // s'il reste une bouée en cas de blocage obstacle
        // on lache tout pour pas la trimballer
        int restant = 0;
        if (io.presencePinceArriere1()) restant++;
        if (io.presencePinceArriere2()) restant++;
        if (io.presencePinceArriere3()) restant++;
        if (io.presencePinceArriere4()) restant++;
        if (io.presencePinceArriere5()) restant++;

        if (restant == 1) {
            srv.pincesArriereOuvert(true);
            srv.pivotArriereFerme(true);
        }

        if (!io.presencePinceArriere1()) {
            rs.pinceArriere(0, null);
        }
        if (!io.presencePinceArriere2()) {
            rs.pinceArriere(1, null);
        }
        if (!io.presencePinceArriere3()) {
            rs.pinceArriere(2, null);
        }
        if (!io.presencePinceArriere4()) {
            rs.pinceArriere(3, null);
        }
        if (!io.presencePinceArriere5()) {
            rs.pinceArriere(4, null);
        }
    }
}

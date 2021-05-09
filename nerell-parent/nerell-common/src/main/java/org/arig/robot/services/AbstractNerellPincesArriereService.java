package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesServosNerell;
import org.arig.robot.model.ECouleurBouee;
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
        if (partielle) {
            deposeTable(couleurChenal);

            for (int i = 0; i < rs.pincesArriere().length; i++) {
                final ECouleurBouee couleurPince = rs.pincesArriere()[i];
                if (couleurPince == couleurChenal || couleurPince == ECouleurBouee.INCONNU) {
                    if (couleurChenal == ECouleurBouee.ROUGE) {
                        rs.grandChenaux().addRouge(couleurPince);
                    } else {
                        rs.grandChenaux().addVert(couleurPince);
                    }
                    rs.pinceArriere(i, null);
                }
            }

        } else {
            deposeTable(null);

            if (couleurChenal == ECouleurBouee.ROUGE) {
                rs.grandChenaux().addRouge(rs.pincesArriere());
            } else {
                rs.grandChenaux().addVert(rs.pincesArriere());
            }
            rs.clearPincesArriere();
        }

        return true;
    }

    @Override
    public void deposeGrandPort() {
        deposeTable(null);

        for (ECouleurBouee eCouleurBouee : rs.pincesArriere()) {
            if (eCouleurBouee != null) {
                rs.grandPort().add(eCouleurBouee);
            }
        }
        rs.clearPincesArriere();
    }

    /**
     * Déposer la pince arrière dans le petit port
     */
    @Override
    public boolean deposePetitPort() {
        deposeTable(null);

        rs.petitChenaux().addVert(rs.pincesArriere()[0], rs.pincesArriere()[1]);
        if (rs.pincesArriere()[2] != null) {
            rs.petitPort().add(rs.pincesArriere()[2]);
        }
        rs.petitChenaux().addRouge(rs.pincesArriere()[3], rs.pincesArriere()[4]);

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
            ThreadUtils.sleep(IConstantesServosNerell.WAIT_PINCE_ARRIERE);
        }

        srv.ascenseurArriereEcueil(true);
        srv.pivotArriereFerme(false);
        srv.pincesArriereFerme(false);
        srv.ascenseurArriereHaut(false);
    }
}

package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.ECouleurBouee;
import org.arig.robot.model.NerellStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

@Slf4j
public abstract class AbstractPincesArriereService implements IPincesArriereService {

    @Autowired
    private ServosService srv;

    @Autowired
    private NerellStatus rs;

    @Autowired
    private IIOService io;

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
    public boolean deposeGrandChenal(final ECouleurBouee couleurChenal) {
        deposeTable();

        if (couleurChenal == ECouleurBouee.ROUGE) {
            rs.grandChenaux().addRouge(rs.pincesArriere());
        } else {
            rs.grandChenaux().addVert(rs.pincesArriere());
        }

        rs.clearPincesArriere();

        return true;
    }

    /**
     * Déposer la pince arrière dans le petit port
     */
    @Override
    public boolean deposePetitPort() {
        deposeTable();

        rs.petitChenaux().addVert(rs.pincesArriere()[0], rs.pincesArriere()[1]);
        if (rs.pincesArriere()[2] != null) {
            rs.petitPort().add(rs.pincesArriere()[2]);
        }
        rs.petitChenaux().addRouge(rs.pincesArriere()[3], rs.pincesArriere()[4]);

        rs.clearPincesArriere();

        return true;
    }

    private void deposeTable() {
        srv.pivotArriereOuvert(true);
        srv.ascenseurArriereTable(true);
        srv.pincesArriereOuvert(true);
        srv.ascenseurArriereEcueil(true);
        srv.pivotArriereFerme(false);
        srv.pincesArriereFerme(false);
        srv.ascenseurArriereHaut(false);
    }
}

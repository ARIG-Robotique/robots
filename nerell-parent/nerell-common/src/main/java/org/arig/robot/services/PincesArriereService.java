package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.ECouleurBouee;
import org.arig.robot.model.RobotStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class PincesArriereService {

    @Autowired
    private ServosService srv;

    @Autowired
    private RobotStatus rs;

    @Autowired
    private IIOService io;

    /**
     * Prises des bouees dans un éccueil
     */
    public boolean priseEccueil(ECouleurBouee ...bouees) {
        Assert.isTrue(bouees.length == 5, "Paramètre bouees invalide");

        srv.pincesArriereOuvert(false);
        srv.pivotArriereOuvert(true);
        srv.ascenseurArriereEccueil(true);
        srv.pincesArriereFerme(true);
        srv.ascenseurArriereHaut(true);
        srv.pivotArriereFerme(true);

        if (io.presencePinceArriere1()) {
            rs.setPinceArriere(0, bouees[0]);
        }
        if (io.presencePinceArriere2()) {
            rs.setPinceArriere(1, bouees[1]);
        }
        if (io.presencePinceArriere3()) {
            rs.setPinceArriere(2, bouees[2]);
        }
        if (io.presencePinceArriere4()) {
            rs.setPinceArriere(3, bouees[3]);
        }
        if (io.presencePinceArriere5()) {
            rs.setPinceArriere(4, bouees[4]);
        }

        return true;
    }

    /**
     * Déposer la pince arrière dans un chenal
     * @param chenal Une des liste de RobotStatus
     */
    public boolean deposeArriereChenal(List<ECouleurBouee> chenal) {
        srv.pivotArriereOuvert(true);
        srv.ascenseurArriereTable(true);
        srv.pincesArriereOuvert(true);
        srv.ascenseurArriereHaut(true);
        srv.pivotArriereFerme(false);
        srv.pincesArriereFerme(false);

        Collections.addAll(chenal, rs.getPincesArriere());
        rs.clearPincesArriere();

        return true;
    }

}

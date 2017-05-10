package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesServos;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.system.servos.SD21Servos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author gdepuille on 27/04/15.
 */
@Slf4j
@Service
public class ServosService {

    @Autowired
    private SD21Servos servos;

    @Autowired
    private IIOService ioService;

    @Autowired
    private RobotStatus robotStatus;

    /* **************************************** */
    /* Méthode pour le positionnement d'origine */
    /* **************************************** */

    public void homes() {
        log.info("Servos en position initiale");
        servos.printVersion();

        // Ordre précis car blocage mécanique dans certains cas
        servos.setPositionAndSpeed(IConstantesServos.INCLINAISON_BRAS, IConstantesServos.INCLINAISON_BRAS_VERTICAL, IConstantesServos.SPEED_INC_BRAS);
        servos.waitTime(IConstantesServos.WAIT_INC_BRAS);
        servos.setPositionAndSpeed(IConstantesServos.ROTATION_VENTOUSE, IConstantesServos.ROTATION_VENTOUSE_DEPOSE_MAGASIN, IConstantesServos.SPEED_ROT_VENTOUSE);
        servos.waitTime(IConstantesServos.WAIT_ROT_VENTOUSE);
        servos.setPositionAndSpeed(IConstantesServos.PINCE_MODULE_CENTRE, IConstantesServos.PINCE_MODULE_CENTRE_FERME, IConstantesServos.SPEED_PINCE);
        servos.waitTime(IConstantesServos.WAIT_PINCE);
        servos.setPositionAndSpeed(IConstantesServos.PINCE_MODULE_DROIT, IConstantesServos.PINCE_MODULE_DROIT_FERME, IConstantesServos.SPEED_PINCE);

        // Tous en même temps
        servos.setPositionAndSpeed(IConstantesServos.PORTE_MAGASIN_DROIT, IConstantesServos.PORTE_DROITE_OUVERT, IConstantesServos.SPEED_PORTE_MAG);
        servos.setPositionAndSpeed(IConstantesServos.PORTE_MAGASIN_GAUCHE, IConstantesServos.PORTE_GAUCHE_OUVERT, IConstantesServos.SPEED_PORTE_MAG);
        servos.setPositionAndSpeed(IConstantesServos.BLOCAGE_ENTREE_MAG, IConstantesServos.BLOCAGE_OUVERT, IConstantesServos.SPEED_BLOCAGE_MAG);
        servos.setPositionAndSpeed(IConstantesServos.DEVIDOIR, IConstantesServos.DEVIDOIR_CHARGEMENT, IConstantesServos.SPEED_DEVIDOIR);
        servos.setPositionAndSpeed(IConstantesServos.INCLINAISON_ASPIRATION, IConstantesServos.INCLINAISON_ASPI_FERME, IConstantesServos.SPEED_INC_ASPI);

        // TMP
        servos.setPositionAndSpeed(IConstantesServos.MOTOR_EJECTION, 1500, (byte) 0);
        servos.setPositionAndSpeed(IConstantesServos.MOTOR_ROULEAUX, 1500, (byte) 0);
        servos.setPositionAndSpeed(IConstantesServos.MOTOR_ASPIRATION, 1500, (byte) 0);
    }

    public void ouvrePinceDroite() {
        servos.setPosition(IConstantesServos.PINCE_MODULE_DROIT, IConstantesServos.PINCE_MODULE_DROIT_OUVERT);
    }

    public void calibrationAspiration() {
        log.info("Calibration moteur aspiration");
        aspirationMax();
        ioService.enableAlim8VPuissance();
        while(!ioService.alimPuissance8VOk());
        servos.waitTime(3000);
        aspirationStop();
        servos.waitTime(3000);
    }

    public void aspirationMax() {
        servos.setPosition(IConstantesServos.MOTOR_ASPIRATION, 2000);
    }
    public void aspirationStop() {
        servos.setPosition(IConstantesServos.MOTOR_ASPIRATION, 1000);
    }
}

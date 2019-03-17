package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesServos;
import org.arig.robot.exception.ServoException;
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

    public void cyclePreparation() {
        log.info("Servos en position initiale");
        servos.printVersion();

        // Moteurs
        servos.setPositionAndSpeed(IConstantesServos.MOTOR_DROIT, 1500, (byte) 0);
        servos.setPositionAndSpeed(IConstantesServos.MOTOR_GAUCHE, 1500, (byte) 0);
        servos.setPositionAndSpeed(IConstantesServos.MOTOR_BARILLET, 1500, (byte) 0);

        ioService.enableAlim5VPuissance();
        while (!ioService.alimPuissance5VOk()) ;

        // Tous en même temps
        servos.setPositionAndSpeed(IConstantesServos.PINCE_SERRAGE_PALET_DROIT, IConstantesServos.PINCE_SERRAGE_PALET_DROIT_OUVERT, IConstantesServos.SPEED_SERRAGE_PALET);
        servos.setPositionAndSpeed(IConstantesServos.PINCE_SERRAGE_PALET_GAUCHE, IConstantesServos.PINCE_SERRAGE_PALET_GAUCHE_OUVERT, IConstantesServos.SPEED_SERRAGE_PALET);

        // Cycle spéciaux (/!\ car blocage mécanique possible)
        brasPincesFermes();
    }

    public void homes() {
        // TODO
    }

    public void brasPincesFermes() {
        // Ordre précis car blocage mécanique dans certains cas
        /*
        servos.setPositionAndSpeed(IConstantesServos.ROTATION_VENTOUSE, IConstantesServos.ROTATION_VENTOUSE_DEPOSE_MAGASIN, IConstantesServos.SPEED_ROT_VENTOUSE);
        servos.waitTime(IConstantesServos.WAIT_ROT_VENTOUSE);
        servos.setPositionAndSpeed(IConstantesServos.PINCE_MODULE_CENTRE, IConstantesServos.PINCE_MODULE_CENTRE_FERME, IConstantesServos.SPEED_PINCE);
        servos.waitTime(IConstantesServos.WAIT_PINCE);
        servos.setPositionAndSpeed(IConstantesServos.INCLINAISON_BRAS, IConstantesServos.INCLINAISON_BRAS_DEPOSE, IConstantesServos.SPEED_INC_BRAS);
        servos.waitTime(IConstantesServos.WAIT_INC_BRAS_LONG);
        servos.setPositionAndSpeed(IConstantesServos.PINCE_MODULE_DROIT, IConstantesServos.PINCE_MODULE_DROIT_FERME, IConstantesServos.SPEED_PINCE);
        */
    }

    public void waitPinceSerragePalet() {
        servos.waitTime(IConstantesServos.WAIT_PINCE_SERRAGE_PALET);
    }

    //*******************************************//
    //* Lecture des positions                   *//
    //*******************************************//
    public boolean isPinceSerragePaletDroitOuvert() {
        return servos.getPosition(IConstantesServos.PINCE_SERRAGE_PALET_DROIT) == IConstantesServos.PINCE_SERRAGE_PALET_DROIT_OUVERT;
    }

    //*******************************************//
    //* Déplacements                            *//
    //*******************************************//

    public void pinceSerragePaletDroiteOuvert() {
        servos.setPosition(IConstantesServos.PINCE_SERRAGE_PALET_DROIT, IConstantesServos.PINCE_SERRAGE_PALET_DROIT_OUVERT);
    }

    /*public void pinceDroiteVentouse() {
        if (isPinceCentreFerme() && (isPinceDroiteOuvert() || isPinceDroitePriseProduit())) {
            servos.setPosition(IConstantesServos.PINCE_MODULE_DROIT, IConstantesServos.PINCE_MODULE_DROIT_CHARGEMENT_VENTOUSE);
        } else {
            throw new ServoException("pinceDroite");
        }
    }*/

    /*private boolean checkDescenteBras() {
        return !(isBrasDepose() && isPorteFerme() && ioService.presenceModuleDansBras())
                && !isPinceDroiteFerme()
                && (
                isBrasAttente() ||
                        isBrasVertical() ||
                        isBrasPriseRobot() ||
                        isBrasPriseFusee() ||
                        isPinceCentreFerme() ||
                        isPinceCentreOuvertDansDroit() ||
                        isPinceCentreOuvert() && !ioService.presenceModuleDansBras()
        );
    }*/
}

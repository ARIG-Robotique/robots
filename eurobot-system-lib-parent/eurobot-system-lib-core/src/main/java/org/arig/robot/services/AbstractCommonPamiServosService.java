package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.system.servos.AbstractServos;

import java.util.Map;

@Slf4j
public abstract class AbstractCommonPamiServosService extends AbstractServosService {

    public static final String TOUCHE_PLANTE_GAUCHE = "Touche plante gauche";
    public static final String TOUCHE_PLANTE_DROITE = "Touche plante droite";


    protected static final String POS_FERME = "Fermé";
    protected static final String POS_OUVERT = "Ouvert";

    protected static final String GROUP_TOUCHE_PLANTE = "Touche plante";

    protected static final byte GROUP_TOUCHE_PLANTE_ID = 1;

    protected AbstractCommonPamiServosService(AbstractServos servoDevice, AbstractServos... servoDevices) {
        super(servoDevice, servoDevices);
    }

    /* **************************************** */
    /* Méthode pour le positionnement d'origine */
    /* **************************************** */

    public void homes() {
        groupeTouchePlanteFerme(false);
    }

    //*******************************************//
    //* Déplacements de groupe                  *//
    //*******************************************//

    public void groupeTouchePlanteOuvert(boolean wait) {
        setPositionBatch(GROUP_TOUCHE_PLANTE, POS_OUVERT, wait);
    }
    public void groupeTouchePlanteFerme(boolean wait) {
        setPositionBatch(GROUP_TOUCHE_PLANTE, POS_FERME, wait);
    }

    //*******************************************//
    //* Déplacements de servo                   *//
    //*******************************************//

    public void setTouchePlanteGaucheOuvert(boolean wait) {
        setPosition(TOUCHE_PLANTE_GAUCHE, POS_OUVERT, wait);
    }

    public void setTouchePlanteGaucheFerme(boolean wait) {
        setPosition(TOUCHE_PLANTE_GAUCHE, POS_FERME, wait);
    }

    public void setTouchePlanteDroiteOuvert(boolean wait) {
        setPosition(TOUCHE_PLANTE_DROITE, POS_OUVERT, wait);
    }

    public void setTouchePlanteDroiteFerme(boolean wait) {
        setPosition(TOUCHE_PLANTE_DROITE, POS_FERME, wait);
    }

}

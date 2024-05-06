package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.system.servos.AbstractServos;

@Slf4j
public abstract class AbstractCommonPamiServosService extends AbstractServosService {

    public static final String TOUCHE_PLANTE_GAUCHE = "Touche plante gauche";
    public static final String TOUCHE_PLANTE_DROITE = "Touche plante droite";

    protected static final String POS_FERME = "Fermé";
    protected static final String POS_OUVERT_BLEU = "Ouvert Bleu";
    protected static final String POS_OUVERT_JAUNE = "Ouvert Jaune";

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

    public void groupeTouchePlanteOuvertBleu(boolean wait) {
        setPositionBatch(GROUP_TOUCHE_PLANTE, POS_OUVERT_BLEU, wait);
    }
    public void groupeTouchePlanteOuvertJaune(boolean wait) {
        setPositionBatch(GROUP_TOUCHE_PLANTE, POS_OUVERT_JAUNE, wait);
    }
    public void groupeTouchePlanteFerme(boolean wait) {
        setPositionBatch(GROUP_TOUCHE_PLANTE, POS_FERME, wait);
    }

    //*******************************************//
    //* Déplacements de servo                   *//
    //*******************************************//

    public void setTouchePlanteGaucheOuvertBleu(boolean wait) {
        setPosition(TOUCHE_PLANTE_GAUCHE, POS_OUVERT_BLEU, wait);
    }

    public void setTouchePlanteGaucheOuvertJaune(boolean wait) {
        setPosition(TOUCHE_PLANTE_GAUCHE, POS_OUVERT_JAUNE, wait);
    }

    public void setTouchePlanteGaucheFerme(boolean wait) {
        setPosition(TOUCHE_PLANTE_GAUCHE, POS_FERME, wait);
    }

    public void setTouchePlanteDroiteOuvertBleu(boolean wait) {
        setPosition(TOUCHE_PLANTE_DROITE, POS_OUVERT_BLEU, wait);
    }

    public void setTouchePlanteDroiteOuvertJAUNE(boolean wait) {
        setPosition(TOUCHE_PLANTE_DROITE, POS_OUVERT_JAUNE, wait);
    }

    public void setTouchePlanteDroiteFerme(boolean wait) {
        setPosition(TOUCHE_PLANTE_DROITE, POS_FERME, wait);
    }
}

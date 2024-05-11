package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.Team;
import org.arig.robot.system.servos.AbstractServos;

@Slf4j
public abstract class AbstractCommonPamiServosService extends AbstractServosService {

    public static final String TOUCHE_PLANTE_GAUCHE = "Touche plante gauche";
    public static final String TOUCHE_PLANTE_DROITE = "Touche plante droite";

    protected static final String POS_FERME = "Fermé";
    protected static final String POS_INIT = "Init";
    protected static final String POS_OUVERT = "Ouvert";
    protected static final String POS_OUVERT_JAUNE = "Ouvert Jaune";
    protected static final String POS_OUVERT_BLEU = "Ouvert Bleu";

    protected static final String GROUP_TOUCHE_PLANTE = "Touche plante";

    protected static final byte GROUP_TOUCHE_PLANTE_ID = 1;

    protected AbstractCommonPamiServosService(AbstractServos servoDevice, AbstractServos... servoDevices) {
        super(servoDevice, servoDevices);
    }

    /* **************************************** */
    /* Méthode pour le positionnement d'origine */
    /* **************************************** */

    public void homes() {
        groupeTouchePlanteInit(false);
    }

    //*******************************************//
    //* Déplacements de groupe                  *//
    //*******************************************//

    public void groupeTouchePlanteOuvertMatch(Team team) {
        setPositionBatch(GROUP_TOUCHE_PLANTE, team == Team.JAUNE ? POS_OUVERT_JAUNE : POS_OUVERT_BLEU, false);
    }
    public void groupeTouchePlanteInit(boolean wait) {
        setPositionBatch(GROUP_TOUCHE_PLANTE, POS_INIT, wait);
    }
    public void groupeTouchePlanteOuvert(boolean wait) {
        setPositionBatch(GROUP_TOUCHE_PLANTE, POS_OUVERT, wait);
    }
    public void groupeTouchePlanteFerme(boolean wait) {
        setPositionBatch(GROUP_TOUCHE_PLANTE, POS_FERME, wait);
    }

    //*******************************************//
    //* Déplacements de servo                   *//
    //*******************************************//

}

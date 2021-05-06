package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesServosOdin;
import org.arig.robot.system.servos.SD21Servos;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OdinServosService extends AbstractServosService {

    @Autowired
    private SD21Servos servos;

    /* **************************************** */
    /* Méthode pour le positionnement d'origine */
    /* **************************************** */

    public void homes() {
        servos.setPositionAndSpeed(IConstantesServosOdin.BRAS_DROIT, IConstantesServosOdin.POS_BRAS_DROIT_FERME, IConstantesServosOdin.SPEED_BRAS);
        servos.setPositionAndSpeed(IConstantesServosOdin.BRAS_GAUCHE, IConstantesServosOdin.POS_BRAS_GAUCHE_FERME, IConstantesServosOdin.SPEED_BRAS);
        servos.setPositionAndSpeed(IConstantesServosOdin.PAVILLON, IConstantesServosOdin.POS_PAVILLON_BAS, IConstantesServosOdin.SPEED_PAVILLON);
    }

    //*******************************************//
    //* Lecture des positions                   *//
    //*******************************************//


    //*******************************************//
    //* Déplacements                            *//
    //*******************************************//

    public void brasGaucheMancheAAir(boolean wait) {
        setPosition(IConstantesServosOdin.BRAS_GAUCHE, IConstantesServosOdin.POS_BRAS_GAUCHE_MANCHE_AIR, wait);
    }

    public void brasGauchePhare(boolean wait) {
        setPosition(IConstantesServosOdin.BRAS_GAUCHE, IConstantesServosOdin.POS_BRAS_GAUCHE_PHARE, wait);
    }

    public void brasGaucheFerme(boolean wait) {
        setPosition(IConstantesServosOdin.BRAS_GAUCHE, IConstantesServosOdin.POS_BRAS_GAUCHE_FERME, wait);
    }

    public void brasDroitMancheAAir(boolean wait) {
        setPosition(IConstantesServosOdin.BRAS_DROIT, IConstantesServosOdin.POS_BRAS_DROIT_MANCHE_AIR, wait);
    }

    public void brasDroitPhare(boolean wait) {
        setPosition(IConstantesServosOdin.BRAS_DROIT, IConstantesServosOdin.POS_BRAS_DROIT_PHARE, wait);
    }

    public void brasDroitFerme(boolean wait) {
        setPosition(IConstantesServosOdin.BRAS_DROIT, IConstantesServosOdin.POS_BRAS_DROIT_FERME, wait);
    }

    public void pavillonHaut() {
        setPosition(IConstantesServosOdin.PAVILLON, IConstantesServosOdin.POS_PAVILLON_HAUT, false);
    }
}

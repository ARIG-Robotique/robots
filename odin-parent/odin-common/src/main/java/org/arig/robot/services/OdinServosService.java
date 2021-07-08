package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IOdinConstantesServos;
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
        servos.setPositionAndSpeed(IOdinConstantesServos.BRAS_DROIT, IOdinConstantesServos.POS_BRAS_DROIT_FERME, IOdinConstantesServos.SPEED_BRAS);
        servos.setPositionAndSpeed(IOdinConstantesServos.BRAS_GAUCHE, IOdinConstantesServos.POS_BRAS_GAUCHE_FERME, IOdinConstantesServos.SPEED_BRAS);
        servos.setPositionAndSpeed(IOdinConstantesServos.PAVILLON, IOdinConstantesServos.POS_PAVILLON_BAS, IOdinConstantesServos.SPEED_PAVILLON);
        servos.setPositionAndSpeed(IOdinConstantesServos.POUSSOIR_AVANT_GAUCHE, IOdinConstantesServos.POS_POUSSOIR_AVANT_GAUCHE_BAS, IOdinConstantesServos.SPEED_POUSSOIR);
        servos.setPositionAndSpeed(IOdinConstantesServos.POUSSOIR_AVANT_DROIT, IOdinConstantesServos.POS_POUSSOIR_AVANT_DROIT_BAS, IOdinConstantesServos.SPEED_POUSSOIR);
        servos.setPositionAndSpeed(IOdinConstantesServos.POUSSOIR_ARRIERE_GAUCHE, IOdinConstantesServos.POS_POUSSOIR_ARRIERE_GAUCHE_BAS, IOdinConstantesServos.SPEED_POUSSOIR);
        servos.setPositionAndSpeed(IOdinConstantesServos.POUSSOIR_ARRIERE_DROIT, IOdinConstantesServos.POS_POUSSOIR_ARRIERE_DROIT_BAS, IOdinConstantesServos.SPEED_POUSSOIR);
    }

    //*******************************************//
    //* Lecture des positions                   *//
    //*******************************************//


    //*******************************************//
    //* Déplacements                            *//
    //*******************************************//

    public void brasGaucheMancheAAir(boolean wait) {
        logPositionServo("Bras gauche", "Manche a air", wait);
        setPosition(IOdinConstantesServos.BRAS_GAUCHE, IOdinConstantesServos.POS_BRAS_GAUCHE_MANCHE_AIR, wait);
    }

    public void brasGauchePhare(boolean wait) {
        logPositionServo("Bras gauche", "Phare", wait);
        setPosition(IOdinConstantesServos.BRAS_GAUCHE, IOdinConstantesServos.POS_BRAS_GAUCHE_PHARE, wait);
    }

    public void brasGaucheFerme(boolean wait) {
        logPositionServo("Bras gauche", "Fermé", wait);
        setPosition(IOdinConstantesServos.BRAS_GAUCHE, IOdinConstantesServos.POS_BRAS_GAUCHE_FERME, wait);
    }

    public void brasGaucheGobelet(boolean wait) {
        logPositionServo("Bras gauche", "Gobelet", wait);
        setPosition(IOdinConstantesServos.BRAS_GAUCHE, IOdinConstantesServos.POS_BRAS_GAUCHE_GOBELET, wait);
    }

    public void brasDroitMancheAAir(boolean wait) {
        logPositionServo("Bras droit", "Manche a air", wait);
        setPosition(IOdinConstantesServos.BRAS_DROIT, IOdinConstantesServos.POS_BRAS_DROIT_MANCHE_AIR, wait);
    }

    public void brasDroitPhare(boolean wait) {
        logPositionServo("Bras droit", "Phare", wait);
        setPosition(IOdinConstantesServos.BRAS_DROIT, IOdinConstantesServos.POS_BRAS_DROIT_PHARE, wait);
    }

    public void brasDroitFerme(boolean wait) {
        logPositionServo("Bras droit", "Fermé", wait);
        setPosition(IOdinConstantesServos.BRAS_DROIT, IOdinConstantesServos.POS_BRAS_DROIT_FERME, wait);
    }

    public void brasDroitGobelet(boolean wait) {
        logPositionServo("Bras droit", "Gobelet", wait);
        setPosition(IOdinConstantesServos.BRAS_DROIT, IOdinConstantesServos.POS_BRAS_DROIT_GOBELET, wait);
    }

    public void pavillonHaut() {
        logPositionServo("Pavillon", "Haut", false);
        setPosition(IOdinConstantesServos.PAVILLON, IOdinConstantesServos.POS_PAVILLON_HAUT, false);
    }

    public void pavillonFinMatch() {
        logPositionServo("Pavillon", "Fin match", false);
        setPosition(IOdinConstantesServos.PAVILLON, IOdinConstantesServos.POS_PAVILLON_FIN_MATCH, false);
    }

    public void poussoirAvantGaucheHaut(boolean wait) {
        logPositionServo("Poussoir avant gauche", "Haut", wait);
        setPositionAndSpeed(
                IOdinConstantesServos.POUSSOIR_AVANT_GAUCHE,
                IOdinConstantesServos.POS_POUSSOIR_AVANT_GAUCHE_HAUT,
                IOdinConstantesServos.SPEED_POUSSOIR_POUSSETTE,
                false);
        if (wait) {
            // Position poussoir haut est toujours avec une tempo fixe
            ThreadUtils.sleep(IOdinConstantesServos.WAIT_POUSSOIR_POUSSETTE);
        }
    }

    public void poussoirAvantGaucheBas(boolean wait) {
        logPositionServo("Poussoir avant gauche", "Bas", wait);
        setPositionAndSpeed(
                IOdinConstantesServos.POUSSOIR_AVANT_GAUCHE,
                IOdinConstantesServos.POS_POUSSOIR_AVANT_GAUCHE_BAS,
                IOdinConstantesServos.SPEED_POUSSOIR,
                wait);
    }

    public void poussoirAvantDroitHaut(boolean wait) {
        logPositionServo("Poussoir avant droit", "Haut", wait);
        setPositionAndSpeed(
                IOdinConstantesServos.POUSSOIR_AVANT_DROIT,
                IOdinConstantesServos.POS_POUSSOIR_AVANT_DROIT_HAUT,
                IOdinConstantesServos.SPEED_POUSSOIR_POUSSETTE,
                false);
        if (wait) {
            // Position poussoir haut est toujours avec une tempo fixe
            ThreadUtils.sleep(IOdinConstantesServos.WAIT_POUSSOIR_POUSSETTE);
        }
    }

    public void poussoirAvantDroitBas(boolean wait) {
        logPositionServo("Poussoir avant droit", "Bas", wait);
        setPositionAndSpeed(
                IOdinConstantesServos.POUSSOIR_AVANT_DROIT,
                IOdinConstantesServos.POS_POUSSOIR_AVANT_DROIT_BAS,
                IOdinConstantesServos.SPEED_POUSSOIR,
                wait);
    }

    public void poussoirsAvantHaut(boolean wait) {
        logPositionServo("Poussoirs avant", "Haut", wait);
        setPositionBatchAndSpeed(
                IOdinConstantesServos.BATCH_POUSSOIR_AVANT,
                IOdinConstantesServos.POS_BATCH_POUSSOIR_AVANT_HAUT,
                IOdinConstantesServos.SPEED_POUSSOIR_POUSSETTE,
                false);
        if (wait) {
            // Position poussoir haut est toujours avec une tempo fixe
            ThreadUtils.sleep(IOdinConstantesServos.WAIT_POUSSOIR_POUSSETTE);
        }
    }

    public void poussoirsAvantBas(boolean wait) {
        logPositionServo("Poussoirs avant", "Bas", wait);
        setPositionBatchAndSpeed(
                IOdinConstantesServos.BATCH_POUSSOIR_AVANT,
                IOdinConstantesServos.POS_BATCH_POUSSOIR_AVANT_BAS,
                IOdinConstantesServos.SPEED_POUSSOIR,
                wait);
    }

    public void poussoirArriereGaucheHaut(boolean wait) {
        logPositionServo("Poussoir arriere gauche", "Haut", wait);
        setPositionAndSpeed(
                IOdinConstantesServos.POUSSOIR_ARRIERE_GAUCHE,
                IOdinConstantesServos.POS_POUSSOIR_ARRIERE_GAUCHE_HAUT,
                IOdinConstantesServos.SPEED_POUSSOIR_POUSSETTE,
                false);
        if (wait) {
            // Position poussoir haut est toujours avec une tempo fixe
            ThreadUtils.sleep(IOdinConstantesServos.WAIT_POUSSOIR_POUSSETTE);
        }
    }

    public void poussoirArriereGaucheBas(boolean wait) {
        logPositionServo("Poussoir arriere gauche", "Bas", wait);
        setPositionAndSpeed(
                IOdinConstantesServos.POUSSOIR_ARRIERE_GAUCHE,
                IOdinConstantesServos.POS_POUSSOIR_ARRIERE_GAUCHE_BAS,
                IOdinConstantesServos.SPEED_POUSSOIR,
                wait);
    }

    public void poussoirArriereDroitHaut(boolean wait) {
        logPositionServo("Poussoir arriere droit", "Haut", wait);
        setPositionAndSpeed(
                IOdinConstantesServos.POUSSOIR_ARRIERE_DROIT,
                IOdinConstantesServos.POS_POUSSOIR_ARRIERE_DROIT_HAUT,
                IOdinConstantesServos.SPEED_POUSSOIR_POUSSETTE,
                false);
        if (wait) {
            // Position poussoir haut est toujours avec une tempo fixe
            ThreadUtils.sleep(IOdinConstantesServos.WAIT_POUSSOIR_POUSSETTE);
        }
    }

    public void poussoirArriereDroitBas(boolean wait) {
        logPositionServo("Poussoir arriere droit", "Bas", wait);
        setPositionAndSpeed(
                IOdinConstantesServos.POUSSOIR_ARRIERE_DROIT,
                IOdinConstantesServos.POS_POUSSOIR_ARRIERE_DROIT_BAS,
                IOdinConstantesServos.SPEED_POUSSOIR,
                wait);
    }

    public void poussoirsArriereHaut(boolean wait) {
        logPositionServo("Poussoirs arriere", "Haut", wait);
        setPositionBatchAndSpeed(
                IOdinConstantesServos.BATCH_POUSSOIR_ARRIERE,
                IOdinConstantesServos.POS_BATCH_POUSSOIR_ARRIERE_HAUT,
                IOdinConstantesServos.SPEED_POUSSOIR_POUSSETTE,
                false);
        if (wait) {
            // Position poussoir haut est toujours avec une tempo fixe
            ThreadUtils.sleep(IOdinConstantesServos.WAIT_POUSSOIR_POUSSETTE);
        }
    }

    public void poussoirsArriereBas(boolean wait) {
        logPositionServo("Poussoirs arriere", "Bas", wait);
        setPositionBatchAndSpeed(
                IOdinConstantesServos.BATCH_POUSSOIR_ARRIERE,
                IOdinConstantesServos.POS_BATCH_POUSSOIR_ARRIERE_BAS,
                IOdinConstantesServos.SPEED_POUSSOIR,
                wait);
    }
}

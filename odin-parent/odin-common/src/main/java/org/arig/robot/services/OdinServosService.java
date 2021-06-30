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
        servos.setPositionAndSpeed(IConstantesServosOdin.POUSSOIR_AVANT_GAUCHE, IConstantesServosOdin.POS_POUSSOIR_AVANT_GAUCHE_BAS, IConstantesServosOdin.SPEED_POUSSOIR);
        servos.setPositionAndSpeed(IConstantesServosOdin.POUSSOIR_AVANT_DROIT, IConstantesServosOdin.POS_POUSSOIR_AVANT_DROIT_BAS, IConstantesServosOdin.SPEED_POUSSOIR);
        servos.setPositionAndSpeed(IConstantesServosOdin.POUSSOIR_ARRIERE_GAUCHE, IConstantesServosOdin.POS_POUSSOIR_ARRIERE_GAUCHE_BAS, IConstantesServosOdin.SPEED_POUSSOIR);
        servos.setPositionAndSpeed(IConstantesServosOdin.POUSSOIR_ARRIERE_DROIT, IConstantesServosOdin.POS_POUSSOIR_ARRIERE_DROIT_BAS, IConstantesServosOdin.SPEED_POUSSOIR);
    }

    //*******************************************//
    //* Lecture des positions                   *//
    //*******************************************//


    //*******************************************//
    //* Déplacements                            *//
    //*******************************************//

    public void brasGaucheMancheAAir(boolean wait) {
        logPositionServo("Bras gauche", "Manche a air", wait);
        setPosition(IConstantesServosOdin.BRAS_GAUCHE, IConstantesServosOdin.POS_BRAS_GAUCHE_MANCHE_AIR, wait);
    }

    public void brasGauchePhare(boolean wait) {
        logPositionServo("Bras gauche", "Phare", wait);
        setPosition(IConstantesServosOdin.BRAS_GAUCHE, IConstantesServosOdin.POS_BRAS_GAUCHE_PHARE, wait);
    }

    public void brasGaucheFerme(boolean wait) {
        logPositionServo("Bras gauche", "Fermé", wait);
        setPosition(IConstantesServosOdin.BRAS_GAUCHE, IConstantesServosOdin.POS_BRAS_GAUCHE_FERME, wait);
    }

    public void brasDroitMancheAAir(boolean wait) {
        logPositionServo("Bras droit", "Manche a air", wait);
        setPosition(IConstantesServosOdin.BRAS_DROIT, IConstantesServosOdin.POS_BRAS_DROIT_MANCHE_AIR, wait);
    }

    public void brasDroitPhare(boolean wait) {
        logPositionServo("Bras droit", "Phare", wait);
        setPosition(IConstantesServosOdin.BRAS_DROIT, IConstantesServosOdin.POS_BRAS_DROIT_PHARE, wait);
    }

    public void brasDroitFerme(boolean wait) {
        logPositionServo("Bras droit", "Fermé", wait);
        setPosition(IConstantesServosOdin.BRAS_DROIT, IConstantesServosOdin.POS_BRAS_DROIT_FERME, wait);
    }

    public void pavillonHaut() {
        logPositionServo("Pavillon", "Haut", false);
        setPosition(IConstantesServosOdin.PAVILLON, IConstantesServosOdin.POS_PAVILLON_HAUT, false);
    }

    public void pavillonFinMatch() {
        logPositionServo("Pavillon", "Fin match", false);
        setPosition(IConstantesServosOdin.PAVILLON, IConstantesServosOdin.POS_PAVILLON_FIN_MATCH, false);
    }

    public void poussoirAvantGaucheHaut(boolean wait) {
        logPositionServo("Poussoir avant gauche", "Haut", wait);
        setPositionAndSpeed(
                IConstantesServosOdin.POUSSOIR_AVANT_GAUCHE,
                IConstantesServosOdin.POS_POUSSOIR_AVANT_GAUCHE_HAUT,
                IConstantesServosOdin.SPEED_POUSSOIR_POUSSETTE,
                false);
        if (wait) {
            // Position poussoir haut est toujours avec une tempo fixe
            ThreadUtils.sleep(IConstantesServosOdin.WAIT_POUSSOIR_POUSSETTE);
        }
    }

    public void poussoirAvantGaucheBas(boolean wait) {
        logPositionServo("Poussoir avant gauche", "Bas", wait);
        setPositionAndSpeed(
                IConstantesServosOdin.POUSSOIR_AVANT_GAUCHE,
                IConstantesServosOdin.POS_POUSSOIR_AVANT_GAUCHE_BAS,
                IConstantesServosOdin.SPEED_POUSSOIR,
                wait);
    }

    public void poussoirAvantDroitHaut(boolean wait) {
        logPositionServo("Poussoir avant droit", "Haut", wait);
        setPositionAndSpeed(
                IConstantesServosOdin.POUSSOIR_AVANT_DROIT,
                IConstantesServosOdin.POS_POUSSOIR_AVANT_DROIT_HAUT,
                IConstantesServosOdin.SPEED_POUSSOIR_POUSSETTE,
                false);
        if (wait) {
            // Position poussoir haut est toujours avec une tempo fixe
            ThreadUtils.sleep(IConstantesServosOdin.WAIT_POUSSOIR_POUSSETTE);
        }
    }

    public void poussoirAvantDroitBas(boolean wait) {
        logPositionServo("Poussoir avant droit", "Bas", wait);
        setPositionAndSpeed(
                IConstantesServosOdin.POUSSOIR_AVANT_DROIT,
                IConstantesServosOdin.POS_POUSSOIR_AVANT_DROIT_BAS,
                IConstantesServosOdin.SPEED_POUSSOIR,
                wait);
    }

    public void poussoirsAvantHaut(boolean wait) {
        logPositionServo("Poussoirs avant", "Haut", wait);
        setPositionBatchAndSpeed(
                IConstantesServosOdin.BATCH_POUSSOIR_AVANT,
                IConstantesServosOdin.POS_BATCH_POUSSOIR_AVANT_HAUT,
                IConstantesServosOdin.SPEED_POUSSOIR_POUSSETTE,
                false);
        if (wait) {
            // Position poussoir haut est toujours avec une tempo fixe
            ThreadUtils.sleep(IConstantesServosOdin.WAIT_POUSSOIR_POUSSETTE);
        }
    }

    public void poussoirsAvantBas(boolean wait) {
        logPositionServo("Poussoirs avant", "Bas", wait);
        setPositionBatchAndSpeed(
                IConstantesServosOdin.BATCH_POUSSOIR_AVANT,
                IConstantesServosOdin.POS_BATCH_POUSSOIR_AVANT_BAS,
                IConstantesServosOdin.SPEED_POUSSOIR,
                wait);
    }

    public void poussoirArriereGaucheHaut(boolean wait) {
        logPositionServo("Poussoir arriere gauche", "Haut", wait);
        setPositionAndSpeed(
                IConstantesServosOdin.POUSSOIR_ARRIERE_GAUCHE,
                IConstantesServosOdin.POS_POUSSOIR_ARRIERE_GAUCHE_HAUT,
                IConstantesServosOdin.SPEED_POUSSOIR_POUSSETTE,
                false);
        if (wait) {
            // Position poussoir haut est toujours avec une tempo fixe
            ThreadUtils.sleep(IConstantesServosOdin.WAIT_POUSSOIR_POUSSETTE);
        }
    }

    public void poussoirArriereGaucheBas(boolean wait) {
        logPositionServo("Poussoir arriere gauche", "Bas", wait);
        setPositionAndSpeed(
                IConstantesServosOdin.POUSSOIR_ARRIERE_GAUCHE,
                IConstantesServosOdin.POS_POUSSOIR_ARRIERE_GAUCHE_BAS,
                IConstantesServosOdin.SPEED_POUSSOIR,
                wait);
    }

    public void poussoirArriereDroitHaut(boolean wait) {
        logPositionServo("Poussoir arriere droit", "Haut", wait);
        setPositionAndSpeed(
                IConstantesServosOdin.POUSSOIR_ARRIERE_DROIT,
                IConstantesServosOdin.POS_POUSSOIR_ARRIERE_DROIT_HAUT,
                IConstantesServosOdin.SPEED_POUSSOIR_POUSSETTE,
                false);
        if (wait) {
            // Position poussoir haut est toujours avec une tempo fixe
            ThreadUtils.sleep(IConstantesServosOdin.WAIT_POUSSOIR_POUSSETTE);
        }
    }

    public void poussoirArriereDroitBas(boolean wait) {
        logPositionServo("Poussoir arriere droit", "Bas", wait);
        setPositionAndSpeed(
                IConstantesServosOdin.POUSSOIR_ARRIERE_DROIT,
                IConstantesServosOdin.POS_POUSSOIR_ARRIERE_DROIT_BAS,
                IConstantesServosOdin.SPEED_POUSSOIR,
                wait);
    }

    public void poussoirsArriereHaut(boolean wait) {
        logPositionServo("Poussoirs arriere", "Haut", wait);
        setPositionBatchAndSpeed(
                IConstantesServosOdin.BATCH_POUSSOIR_ARRIERE,
                IConstantesServosOdin.POS_BATCH_POUSSOIR_ARRIERE_HAUT,
                IConstantesServosOdin.SPEED_POUSSOIR_POUSSETTE,
                false);
        if (wait) {
            // Position poussoir haut est toujours avec une tempo fixe
            ThreadUtils.sleep(IConstantesServosOdin.WAIT_POUSSOIR_POUSSETTE);
        }
    }

    public void poussoirsArriereBas(boolean wait) {
        logPositionServo("Poussoirs arriere", "Bas", wait);
        setPositionBatchAndSpeed(
                IConstantesServosOdin.BATCH_POUSSOIR_ARRIERE,
                IConstantesServosOdin.POS_BATCH_POUSSOIR_ARRIERE_BAS,
                IConstantesServosOdin.SPEED_POUSSOIR,
                wait);
    }
}

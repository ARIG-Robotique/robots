package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.INerellConstantesServos;
import org.arig.robot.system.servos.SD21Servos;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NerellServosService extends AbstractServosService {

    @Autowired
    private SD21Servos servos;

    /* **************************************** */
    /* Méthode pour le positionnement d'origine */
    /* **************************************** */

    public void homes() {
        servos.setPositionAndSpeed(INerellConstantesServos.MOUSTACHE_DROITE, INerellConstantesServos.POS_MOUSTACHE_DROITE_FERME, INerellConstantesServos.SPEED_MOUSTACHE);
        servos.setPositionAndSpeed(INerellConstantesServos.MOUSTACHE_GAUCHE, INerellConstantesServos.POS_MOUSTACHE_GAUCHE_FERME, INerellConstantesServos.SPEED_MOUSTACHE);
        servos.setPositionAndSpeed(INerellConstantesServos.BRAS_DROIT, INerellConstantesServos.POS_BRAS_DROIT_FERME, INerellConstantesServos.SPEED_BRAS);
        servos.setPositionAndSpeed(INerellConstantesServos.BRAS_GAUCHE, INerellConstantesServos.POS_BRAS_GAUCHE_FERME, INerellConstantesServos.SPEED_BRAS);
        servos.setPositionAndSpeed(INerellConstantesServos.ASCENSEUR_ARRIERE, INerellConstantesServos.POS_ASCENSEUR_ARRIERE_HAUT, INerellConstantesServos.SPEED_ASCENSEUR_ARRIERE);
        servos.setPositionAndSpeed(INerellConstantesServos.PINCE_ARRIERE_1, INerellConstantesServos.POS_PINCE_ARRIERE_1_FERME, INerellConstantesServos.SPEED_PINCE_ARRIERE);
        servos.setPositionAndSpeed(INerellConstantesServos.PINCE_ARRIERE_2, INerellConstantesServos.POS_PINCE_ARRIERE_2_FERME, INerellConstantesServos.SPEED_PINCE_ARRIERE);
        servos.setPositionAndSpeed(INerellConstantesServos.PINCE_ARRIERE_3, INerellConstantesServos.POS_PINCE_ARRIERE_3_FERME, INerellConstantesServos.SPEED_PINCE_ARRIERE);
        servos.setPositionAndSpeed(INerellConstantesServos.PINCE_ARRIERE_4, INerellConstantesServos.POS_PINCE_ARRIERE_4_FERME, INerellConstantesServos.SPEED_PINCE_ARRIERE);
        servos.setPositionAndSpeed(INerellConstantesServos.PINCE_ARRIERE_5, INerellConstantesServos.POS_PINCE_ARRIERE_5_FERME, INerellConstantesServos.SPEED_PINCE_ARRIERE);
        servos.setPositionAndSpeed(INerellConstantesServos.PIVOT_ARRIERE, INerellConstantesServos.POS_PIVOT_ARRIERE_FERME, INerellConstantesServos.SPEED_PIVOT_ARRIERE);
        servos.setPositionAndSpeed(INerellConstantesServos.ASCENSEUR_AVANT_1, INerellConstantesServos.POS_ASCENSEUR_AVANT_1_BAS, INerellConstantesServos.SPEED_ASCENSEURS_AVANT);
        servos.setPositionAndSpeed(INerellConstantesServos.ASCENSEUR_AVANT_2, INerellConstantesServos.POS_ASCENSEUR_AVANT_2_BAS, INerellConstantesServos.SPEED_ASCENSEURS_AVANT);
        servos.setPositionAndSpeed(INerellConstantesServos.ASCENSEUR_AVANT_3, INerellConstantesServos.POS_ASCENSEUR_AVANT_3_BAS, INerellConstantesServos.SPEED_ASCENSEURS_AVANT);
        servos.setPositionAndSpeed(INerellConstantesServos.ASCENSEUR_AVANT_4, INerellConstantesServos.POS_ASCENSEUR_AVANT_4_BAS, INerellConstantesServos.SPEED_ASCENSEURS_AVANT);
        servos.setPositionAndSpeed(INerellConstantesServos.PAVILLON, INerellConstantesServos.POS_PAVILLON_BAS, INerellConstantesServos.SPEED_PAVILLON);
    }

    //*******************************************//
    //* Lecture des positions                   *//
    //*******************************************//

    public boolean isMoustachesOuvert() {
        return servos.getPosition(INerellConstantesServos.MOUSTACHE_DROITE) == INerellConstantesServos.POS_MOUSTACHE_DROITE_OUVERT;
    }

    //*******************************************//
    //* Déplacements                            *//
    //*******************************************//

    public void moustachesOuvert(boolean wait) {
        logPositionServo("Moustaches", "Ouvert", wait);
        setPositionBatchAndSpeed(
                INerellConstantesServos.BATCH_MOUSTACHES,
                INerellConstantesServos.POS_BATCH_MOUSTACHES_OUVERT,
                INerellConstantesServos.SPEED_MOUSTACHE,
                wait);
    }

    public void moustachesOuvertSpecial(boolean wait) {
        logPositionServo("Moustaches", "Ouvert spécial", wait);
        setPositionBatchAndSpeed(
                INerellConstantesServos.BATCH_MOUSTACHES,
                INerellConstantesServos.POS_BATCH_MOUSTACHES_OUVERT_SPECIAL,
                INerellConstantesServos.SPEED_MOUSTACHE,
                wait);
    }

    public void moustachesFerme(boolean wait) {
        logPositionServo("Moustaches", "Fermé", wait);
        setPositionBatchAndSpeed(
                INerellConstantesServos.BATCH_MOUSTACHES,
                INerellConstantesServos.POS_BATCH_MOUSTACHES_FERME,
                INerellConstantesServos.SPEED_MOUSTACHE,
                wait);
    }

    public void moustachesPoussette(boolean wait) {
        logPositionServo("Moustaches", "Poussette", wait);
        setPositionBatchAndSpeed(
                INerellConstantesServos.BATCH_MOUSTACHES,
                INerellConstantesServos.POS_BATCH_MOUSTACHES_POUSETTE,
                INerellConstantesServos.SPEED_MOUSTACHE_POUSSETTE,
                false);

        if (wait) {
            // Position poussette est toujours avec une tempo fixe
            ThreadUtils.sleep(INerellConstantesServos.WAIT_MOUSTACHE_POUSSETTE);
        }
    }

    public void moustacheGaucheOuvert(boolean wait) {
        logPositionServo("Moustache gauche", "Ouvert", wait);
        setPosition(INerellConstantesServos.MOUSTACHE_GAUCHE, INerellConstantesServos.POS_MOUSTACHE_GAUCHE_OUVERT, wait);
    }

    public void moustacheDroiteOuvert(boolean wait) {
        logPositionServo("Moustache droite", "Ouvert", wait);
        setPosition(INerellConstantesServos.MOUSTACHE_DROITE, INerellConstantesServos.POS_MOUSTACHE_DROITE_OUVERT, wait);
    }

    public void brasGaucheMancheAAir(boolean wait) {
        logPositionServo("Bras gauche", "Manche a air", wait);
        setPosition(INerellConstantesServos.BRAS_GAUCHE, INerellConstantesServos.POS_BRAS_GAUCHE_MANCHE_AIR, wait);
    }

    public void brasGauchePhare(boolean wait) {
        logPositionServo("Bras gauche", "Phare", wait);
        setPosition(INerellConstantesServos.BRAS_GAUCHE, INerellConstantesServos.POS_BRAS_GAUCHE_PHARE, wait);
    }

    public void brasGaucheFerme(boolean wait) {
        logPositionServo("Bras gauche", "Fermé", wait);
        setPosition(INerellConstantesServos.BRAS_GAUCHE, INerellConstantesServos.POS_BRAS_GAUCHE_FERME, wait);
    }

    public void brasDroitMancheAAir(boolean wait) {
        logPositionServo("Bras droit", "Manche a air", wait);
        setPosition(INerellConstantesServos.BRAS_DROIT, INerellConstantesServos.POS_BRAS_DROIT_MANCHE_AIR, wait);
    }

    public void brasDroitPhare(boolean wait) {
        logPositionServo("Bras droit", "Phare", wait);
        setPosition(INerellConstantesServos.BRAS_DROIT, INerellConstantesServos.POS_BRAS_DROIT_PHARE, wait);
    }

    public void brasDroitFerme(boolean wait) {
        logPositionServo("Bras droit", "Fermé", wait);
        setPosition(INerellConstantesServos.BRAS_DROIT, INerellConstantesServos.POS_BRAS_DROIT_FERME, wait);
    }

    public void ascenseurArriereHaut(boolean wait) {
        logPositionServo("Ascenseur arrière", "Haut", wait);
        setPosition(INerellConstantesServos.ASCENSEUR_ARRIERE, INerellConstantesServos.POS_ASCENSEUR_ARRIERE_HAUT, wait);
    }

    public void ascenseurArriereEcueil(boolean wait) {
        logPositionServo("Ascenseur arrière", "Ecueil", wait);
        setPosition(INerellConstantesServos.ASCENSEUR_ARRIERE, INerellConstantesServos.POS_ASCENSEUR_ARRIERE_ECUEIL, wait);
    }

    public void ascenseurArriereTable(boolean wait) {
        logPositionServo("Ascenseur arrière", "Table", wait);
        setPosition(INerellConstantesServos.ASCENSEUR_ARRIERE, INerellConstantesServos.POS_ASCENSEUR_ARRIERE_TABLE, wait);
    }

    public void pivotArriereFerme(boolean wait) {
        logPositionServo("Pivot arrière", "Fermé", wait);
        setPosition(INerellConstantesServos.PIVOT_ARRIERE, INerellConstantesServos.POS_PIVOT_ARRIERE_FERME, wait);
    }

    public void pivotArriereOuvert(boolean wait) {
        logPositionServo("Pivot arrière", "Ouvert", wait);
        setPosition(INerellConstantesServos.PIVOT_ARRIERE, INerellConstantesServos.POS_PIVOT_ARRIERE_OUVERT, wait);
    }

    public void pincesArriereFerme(boolean wait) {
        logPositionServo("Pinces arrière", "Fermé", wait);
        setPositionBatch(INerellConstantesServos.BATCH_PINCES_ARRIERE, INerellConstantesServos.POS_BATCH_PINCES_ARRIERE_FERME, wait);
    }

    public void pinceArriereFerme(int index, boolean wait) {
        logPositionServo("Pince arrière " + (index + 1), "Fermé", wait);
        setSinglePositionBatch(
                INerellConstantesServos.BATCH_PINCES_ARRIERE,
                INerellConstantesServos.POS_BATCH_PINCES_ARRIERE_FERME,
                index, wait);
    }

    public void pincesArriereOuvert(boolean wait) {
        logPositionServo("Pinces arrière", "Ouvert", wait);
        setPositionBatch(INerellConstantesServos.BATCH_PINCES_ARRIERE, INerellConstantesServos.POS_BATCH_PINCES_ARRIERE_OUVERT, wait);
    }

    public void pinceArriereOuvert(int index, boolean wait) {
        logPositionServo("Pince arrière " + (index + 1), "Ouvert", wait);
        setSinglePositionBatch(
                INerellConstantesServos.BATCH_PINCES_ARRIERE,
                INerellConstantesServos.POS_BATCH_PINCES_ARRIERE_OUVERT,
                index, wait);
    }

    public void ascenseursAvantBas(boolean wait) {
        logPositionServo("Ascenseurs avant", "Bas", wait);
        setPositionBatch(INerellConstantesServos.BATCH_ASCENSEURS_AVANT, INerellConstantesServos.POS_BATCH_ASCENSEURS_AVANT_BAS, wait);
    }

    public void ascenseurAvantBas(int index, boolean wait) {
        logPositionServo("Ascenseur avant " + (index + 1), "Bas", wait);
        setSinglePositionBatch(
                INerellConstantesServos.BATCH_ASCENSEURS_AVANT,
                INerellConstantesServos.POS_BATCH_ASCENSEURS_AVANT_BAS,
                index, wait);
    }

    public void ascenseursAvantHaut(boolean wait) {
        logPositionServo("Ascenseurs avant", "Haut", wait);
        setPositionBatch(INerellConstantesServos.BATCH_ASCENSEURS_AVANT, INerellConstantesServos.POS_BATCH_ASCENSEURS_AVANT_HAUT, wait);
    }

    public void ascenseurAvantHaut(int index, boolean wait) {
        logPositionServo("Ascenseur avant " + (index + 1), "Haut", wait);
        setSinglePositionBatch(
                INerellConstantesServos.BATCH_ASCENSEURS_AVANT,
                INerellConstantesServos.POS_BATCH_ASCENSEURS_AVANT_HAUT,
                index, wait);
    }

    public void pavillonHaut() {
        logPositionServo("Pavillon", "Haut", false);
        setPosition(INerellConstantesServos.PAVILLON, INerellConstantesServos.POS_PAVILLON_HAUT, false);
    }

    public void pavillonFinMatch() {
        logPositionServo("Pavillon", "Fin match", false);
        setPosition(INerellConstantesServos.PAVILLON, INerellConstantesServos.POS_PAVILLON_FIN_MATCH, false);
    }

}

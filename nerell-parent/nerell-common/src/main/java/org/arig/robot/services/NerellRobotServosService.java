package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.servos.Servo;
import org.arig.robot.system.servos.i2c.SD21Servos;

@Slf4j
public class NerellRobotServosService extends AbstractCommonRobotServosService {

    public static final byte TIROIR_AVANT_ID = 1;
    public static final byte BEC_AVANT_ID = 2;
    public static final byte ASCENSEUR_AVANT_ID = 3;
    public static final byte PINCE_AVANT_GAUCHE_ID = 4;
    public static final byte DOIGT_AVANT_GAUCHE_ID = 5;
    public static final byte PINCE_AVANT_DROIT_ID = 6;
    public static final byte DOIGT_AVANT_DROIT_ID = 7;
    public static final byte BLOCK_COLONNE_AVANT_GAUCHE_ID = 8;
    public static final byte BLOCK_COLONNE_AVANT_DROIT_ID = 9;

    public static final byte TIROIR_ARRIERE_ID = 10;
    public static final byte BEC_ARRIERE_ID = 11;
    public static final byte ASCENSEUR_ARRIERE_ID = 12;
    public static final byte PINCE_ARRIERE_GAUCHE_ID = 13;
    public static final byte DOIGT_ARRIERE_GAUCHE_ID = 14;
    public static final byte PINCE_ARRIERE_DROIT_ID = 15;
    public static final byte DOIGT_ARRIERE_DROIT_ID = 16;
    public static final byte BLOCK_COLONNE_ARRIERE_GAUCHE_ID = 17;
    public static final byte BLOCK_COLONNE_ARRIERE_DROIT_ID = 18;

    public NerellRobotServosService(SD21Servos servos) {
        super(servos);

        Servo tiroirAvant = servo(TIROIR_AVANT_ID, TIROIR_AVANT)
            .time(500)
            .position(POS_INIT, 1500)
            .position(POS_OUVERT, 1500)
            .position(POS_FERME, 1500);
        Servo tiroirArriere = servo(TIROIR_ARRIERE_ID, TIROIR_ARRIERE)
            .time(500)
            .position(POS_INIT, 1500)
            .position(POS_OUVERT, 1500)
            .position(POS_FERME, 1500);

        Servo becAvant = servo(BEC_AVANT_ID, BEC_AVANT)
            .time(500)
            .position(POS_INIT, 1500)
            .position(POS_OUVERT, 1500)
            .position(POS_FERME, 1500);
        Servo becArriere = servo(BEC_ARRIERE_ID, BEC_ARRIERE)
            .time(500)
            .position(POS_INIT, 1500)
            .position(POS_OUVERT, 1500)
            .position(POS_FERME, 1500);

        Servo ascAvant = servo(ASCENSEUR_AVANT_ID, ASCENSEUR_AVANT)
            .time(500)
            .position(POS_HAUT, 1500)
            .position(POS_HAUT_SPLIT, 1500)
            .position(POS_STOCK, 1500)
            .position(POS_BAS, 1500)
            .position(POS_BAS_PRISE, 1500);
        Servo ascArriere = servo(ASCENSEUR_ARRIERE_ID, ASCENSEUR_ARRIERE)
            .time(500)
            .position(POS_HAUT, 1500)
            .position(POS_HAUT_SPLIT, 1500)
            .position(POS_STOCK, 1500)
            .position(POS_BAS, 1500)
            .position(POS_BAS_PRISE, 1500);

        group(GROUP_INDIVIDUAL_AVANT_ID, GROUP_INDIVIDUAL_AVANT)
            .addServo(tiroirAvant)
            .addServo(becAvant)
            .addServo(ascAvant);
        group(GROUP_INDIVIDUAL_ARRIERE_ID, GROUP_INDIVIDUAL_ARRIERE)
            .addServo(tiroirArriere)
            .addServo(becArriere)
            .addServo(ascArriere);

        Servo pinceAvantGauche = servo(PINCE_AVANT_GAUCHE_ID, PINCE_AVANT_GAUCHE)
            .time(500)
            .position(POS_INIT, 1500)
            .position(POS_OUVERT, 1500)
            .position(POS_OUVERT_PRISE, 1500)
            .position(POS_OUVERT_DEPOSE, 1500)
            .position(POS_FERME, 1500);
        Servo pinceAvantDroite = servo(PINCE_AVANT_DROIT_ID, PINCE_AVANT_DROIT)
            .time(500)
            .position(POS_INIT, 1500)
            .position(POS_OUVERT, 1500)
            .position(POS_OUVERT_PRISE, 1500)
            .position(POS_OUVERT_DEPOSE, 1500)
            .position(POS_FERME, 1500);
        group(GROUP_PINCES_AVANT_ID, GROUP_PINCES_AVANT)
            .addServo(pinceAvantGauche)
            .addServo(pinceAvantDroite)
            .batch(POS_INIT)
            .batch(POS_OUVERT)
            .batch(POS_OUVERT_PRISE)
            .batch(POS_OUVERT_DEPOSE)
            .batch(POS_FERME);

        Servo pinceArriereGauche = servo(PINCE_ARRIERE_GAUCHE_ID, PINCE_ARRIERE_GAUCHE)
            .time(500)
            .position(POS_INIT, 1500)
            .position(POS_OUVERT, 1500)
            .position(POS_OUVERT_PRISE, 1500)
            .position(POS_OUVERT_DEPOSE, 1500)
            .position(POS_FERME, 1500);
        Servo pinceArriereDroite = servo(PINCE_ARRIERE_DROIT_ID, PINCE_ARRIERE_DROIT)
            .time(500)
            .position(POS_INIT, 1500)
            .position(POS_OUVERT, 1500)
            .position(POS_OUVERT_PRISE, 1500)
            .position(POS_OUVERT_DEPOSE, 1500)
            .position(POS_FERME, 1500);
        group(GROUP_PINCES_ARRIERE_ID, GROUP_PINCES_ARRIERE)
            .addServo(pinceArriereGauche)
            .addServo(pinceArriereDroite)
            .batch(POS_INIT)
            .batch(POS_OUVERT)
            .batch(POS_OUVERT_PRISE)
            .batch(POS_OUVERT_DEPOSE)
            .batch(POS_FERME);

        Servo doigtAvantGauche = servo(DOIGT_AVANT_GAUCHE_ID, DOIGT_AVANT_GAUCHE)
            .time(500)
            .position(POS_OUVERT, 1500)
            .position(POS_FERME, 1500);
        Servo doigtAvantDroite = servo(DOIGT_AVANT_DROIT_ID, DOIGT_AVANT_DROIT)
            .time(500)
            .position(POS_OUVERT, 1500)
            .position(POS_FERME, 1500);
        group(GROUP_DOIGTS_AVANT_ID, GROUP_DOIGTS_AVANT)
            .addServo(doigtAvantGauche)
            .addServo(doigtAvantDroite)
            .batch(POS_OUVERT)
            .batch(POS_FERME);

        Servo doigtArriereGauche = servo(DOIGT_ARRIERE_GAUCHE_ID, DOIGT_ARRIERE_GAUCHE)
            .time(500)
            .position(POS_OUVERT, 1500)
            .position(POS_FERME, 1500);
        Servo doigtArriereDroite = servo(DOIGT_ARRIERE_DROIT_ID, DOIGT_ARRIERE_DROIT)
            .time(500)
            .position(POS_OUVERT, 1500)
            .position(POS_FERME, 1500);
        group(GROUP_DOIGTS_ARRIERE_ID, GROUP_DOIGTS_ARRIERE)
            .addServo(doigtArriereGauche)
            .addServo(doigtArriereDroite)
            .batch(POS_OUVERT)
            .batch(POS_FERME);

        Servo blockColonneAvantGauche = servo(BLOCK_COLONNE_AVANT_GAUCHE_ID, BLOCK_COLONNE_AVANT_GAUCHE)
            .time(500)
            .position(POS_OUVERT, 1500)
            .position(POS_FERME, 1500);
        Servo blockColonneAvantDroite = servo(BLOCK_COLONNE_AVANT_DROIT_ID, BLOCK_COLONNE_AVANT_DROIT)
            .time(500)
            .position(POS_OUVERT, 1500)
            .position(POS_FERME, 1500);
        group(GROUP_BLOCK_COLONNE_AVANT_ID, GROUP_BLOCK_COLONNE_AVANT)
            .addServo(blockColonneAvantGauche)
            .addServo(blockColonneAvantDroite)
            .batch(POS_OUVERT)
            .batch(POS_FERME);

        Servo blockColonneArriereGauche = servo(BLOCK_COLONNE_ARRIERE_GAUCHE_ID, BLOCK_COLONNE_ARRIERE_GAUCHE)
            .time(500)
            .position(POS_OUVERT, 1500)
            .position(POS_FERME, 1500);
        Servo blockColonneArriereDroite = servo(BLOCK_COLONNE_ARRIERE_DROIT_ID, BLOCK_COLONNE_ARRIERE_DROIT)
            .time(500)
            .position(POS_OUVERT, 1500)
            .position(POS_FERME, 1500);
        group(GROUP_BLOCK_COLONNE_ARRIERE_ID, GROUP_BLOCK_COLONNE_ARRIERE)
            .addServo(blockColonneArriereGauche)
            .addServo(blockColonneArriereDroite)
            .batch(POS_OUVERT)
            .batch(POS_FERME);
    }
}

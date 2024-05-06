package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.servos.Servo;
import org.arig.robot.system.servos.i2c.SD21Servos;

import static org.arig.robot.services.BrasInstance.*;

@Slf4j
public class NerellRobotServosService extends AbstractCommonRobotServosService {

    // SD21 Avant de 1 à 21.
    // SD21 Arrière de 22 à 42.

    private static final byte BRAS_AVANT_GAUCHE_EPAULE_ID = 15;
    private static final byte BRAS_AVANT_GAUCHE_COUDE_ID = 20;
    private static final byte BRAS_AVANT_GAUCHE_POIGNET_ID = 14;
    private static final byte BRAS_AVANT_GAUCHE_PINCE_ID = 17;

    private static final byte BRAS_AVANT_CENTRE_EPAULE_ID = 12;
    private static final byte BRAS_AVANT_CENTRE_COUDE_ID = 11;
    private static final byte BRAS_AVANT_CENTRE_POIGNET_ID = 6;
    private static final byte BRAS_AVANT_CENTRE_PINCE_ID = 8;

    private static final byte BRAS_AVANT_DROIT_EPAULE_ID = 3;
    private static final byte BRAS_AVANT_DROIT_COUDE_ID = 2;
    private static final byte BRAS_AVANT_DROIT_POIGNET_ID = 5;
    private static final byte BRAS_AVANT_DROIT_PINCE_ID = 13;

    private static final byte BRAS_ARRIERE_GAUCHE_EPAULE_ID = 31;
    private static final byte BRAS_ARRIERE_GAUCHE_COUDE_ID = 32;
    private static final byte BRAS_ARRIERE_GAUCHE_POIGNET_ID = 36;
    private static final byte BRAS_ARRIERE_GAUCHE_PINCE_ID = 35;

    private static final byte BRAS_ARRIERE_CENTRE_EPAULE_ID = 30;
    private static final byte BRAS_ARRIERE_CENTRE_COUDE_ID = 24;
    private static final byte BRAS_ARRIERE_CENTRE_POIGNET_ID = 26;
    private static final byte BRAS_ARRIERE_CENTRE_PINCE_ID = 29;

    private static final byte BRAS_ARRIERE_DROIT_EPAULE_ID = 28;
    private static final byte BRAS_ARRIERE_DROIT_COUDE_ID = 22;
    private static final byte BRAS_ARRIERE_DROIT_POIGNET_ID = 23;
    private static final byte BRAS_ARRIERE_DROIT_PINCE_ID = 27;

    private static final byte BLOQUE_PLANTE_AVANT_GAUCHE_ID = 19;
    private static final byte BLOQUE_PLANTE_AVANT_CENTRE_ID = 10;
    private static final byte BLOQUE_PLANTE_AVANT_DROIT_ID = 16;

    private static final byte PANNEAU_SOLAIRE_ROUE_ID = 18;
    private static final byte PANNEAU_SOLAIRE_SKI_ID = 21;

    public NerellRobotServosService(SD21Servos servosAvant, SD21Servos servosArriere) {
        super(servosAvant, servosArriere);

        Servo brasAvantGaucheEpaule = servo(BRAS_AVANT_GAUCHE_EPAULE_ID, BRAS_AVANT_GAUCHE_EPAULE)
                .time(500)
                .angular()
                .angle(A1_MAX, 2400)
                .angle(0, 2160)
                .angle(-90, 1250)
                .angle(A1_MIN, 570)
                .position(POS_INIT, A1_INIT, 50)
                .position(POS_0DEG, 0, 50)
                .build();
        Servo brasAvantGaucheCoude = servo(BRAS_AVANT_GAUCHE_COUDE_ID, BRAS_AVANT_GAUCHE_COUDE)
                .time(500)
                .angular()
                .angle(A2_MIN, 2450)
                .angle(0, 1900)
                .angle(90, 1050)
                .angle(A2_MAX, 610)
                .position(POS_INIT, A2_INIT, 50)
                .position(POS_0DEG, 0, 50)
                .build();
        Servo brasAvantGauchePoignet = servo(BRAS_AVANT_GAUCHE_POIGNET_ID, BRAS_AVANT_GAUCHE_POIGNET)
                .time(500)
                .angular()
                .angle(A3_MAX, 370)
                .angle(0, 645)
                .angle(-90, 1525)
                .angle(A3_MIN, 1860)
                .position(POS_INIT, A3_INIT, 50)
                .position(POS_0DEG, 0, 50)
                .build();
        group(GROUP_BRAS_AVANT_GAUCHE_ID, GROUP_BRAS_AVANT_GAUCHE)
                .addServo(brasAvantGaucheEpaule)
                .addServo(brasAvantGaucheCoude)
                .addServo(brasAvantGauchePoignet)
                .batch(POS_INIT)
                .batch(POS_0DEG);

        Servo brasAvantCentreEpaule = servo(BRAS_AVANT_CENTRE_EPAULE_ID, BRAS_AVANT_CENTRE_EPAULE)
                .time(500)
                .angular()
                .angle(A1_MAX, 2420)
                .angle(0, 2170)
                .angle(-90, 1220)
                .angle(A1_MIN, 520)
                .position(POS_INIT, A1_INIT, 50)
                .position(POS_0DEG, 0, 50)
                .build();
        Servo brasAvantCentreCoude = servo(BRAS_AVANT_CENTRE_COUDE_ID, BRAS_AVANT_CENTRE_COUDE)
                .time(500)
                .angular()
                .angle(A2_MIN, 560)
                .angle(0, 1080)
                .angle(90, 2010)
                .angle(A2_MAX, 2490)
                .position(POS_INIT, A2_INIT, 50)
                .position(POS_0DEG, 0, 50)
                .build();
        Servo brasAvantCentrePoignet = servo(BRAS_AVANT_CENTRE_POIGNET_ID, BRAS_AVANT_CENTRE_POIGNET)
                .time(500)
                .angular()
                .angle(A3_MAX, 2370)
                .angle(0, 2070)
                .angle(-90, 1190)
                .angle(A3_MIN, 860)
                .position(POS_INIT, A3_INIT, 50)
                .position(POS_0DEG, 0, 50)
                .build();
        group(GROUP_BRAS_AVANT_CENTRE_ID, GROUP_BRAS_AVANT_CENTRE)
                .addServo(brasAvantCentreEpaule)
                .addServo(brasAvantCentreCoude)
                .addServo(brasAvantCentrePoignet)
                .batch(POS_INIT)
                .batch(POS_0DEG);

        Servo brasAvantDroitEpaule = servo(BRAS_AVANT_DROIT_EPAULE_ID, BRAS_AVANT_DROIT_EPAULE)
                .time(500)
                .angular()
                .angle(A1_MAX, 2420)
                .angle(0, 2190)
                .angle(-90, 1250)
                .angle(A1_MIN, 550)
                .position(POS_INIT, A1_INIT, 50)
                .position(POS_0DEG, 0, 50)
                .build();
        Servo brasAvantDroitCoude = servo(BRAS_AVANT_DROIT_COUDE_ID, BRAS_AVANT_DROIT_COUDE)
                .time(500)
                .angular()
                .angle(A2_MIN, 2530)
                .angle(0, 1960)
                .angle(90, 1050)
                .angle(A2_MAX, 580)
                .position(POS_INIT, A2_INIT, 50)
                .position(POS_0DEG, 0, 50)
                .build();
        Servo brasAvantDroitPoignet = servo(BRAS_AVANT_DROIT_POIGNET_ID, BRAS_AVANT_DROIT_POIGNET)
                .time(500)
                .angular()
                .angle(A3_MAX, 515)
                .angle(0, 840)
                .angle(-90, 1750)
                .angle(A3_MIN, 2120)
                .position(POS_INIT, A3_INIT, 50)
                .position(POS_0DEG, 0, 50)
                .build();
        group(GROUP_BRAS_AVANT_DROIT_ID, GROUP_BRAS_AVANT_DROIT)
                .addServo(brasAvantDroitEpaule)
                .addServo(brasAvantDroitCoude)
                .addServo(brasAvantDroitPoignet)
                .batch(POS_INIT)
                .batch(POS_0DEG);

        Servo brasArriereGaucheEpaule = servo(BRAS_ARRIERE_GAUCHE_EPAULE_ID, BRAS_ARRIERE_GAUCHE_EPAULE)
                .time(500)
                .angular()
                .angle(A1_MAX, 2460)
                .angle(0, 2250)
                .angle(-90, 1290)
                .angle(A1_MIN, 540)
                .position(POS_INIT, A1_INIT, 50)
                .position(POS_0DEG, 0, 50)
                .build();
        Servo brasArriereGaucheCoude = servo(BRAS_ARRIERE_GAUCHE_COUDE_ID, BRAS_ARRIERE_GAUCHE_COUDE)
                .time(500)
                .angular()
                .angle(A2_MIN, 650)
                .angle(0, 1090)
                .angle(90, 2020)
                .angle(A2_MAX, 2540)
                .position(POS_INIT, A2_INIT, 50)
                .position(POS_0DEG, 0, 50)
                .build();
        Servo brasArriereGauchePoignet = servo(BRAS_ARRIERE_GAUCHE_POIGNET_ID, BRAS_ARRIERE_GAUCHE_POIGNET)
                .time(500)
                .angular()
                .angle(A3_MAX, 2420)
                .angle(0, 2050)
                .angle(-90, 1170)
                .angle(A3_MIN, 850)
                .position(POS_INIT, A3_INIT, 50)
                .position(POS_0DEG, 0, 50)
                .build();
        group(GROUP_BRAS_ARRIERE_GAUCHE_ID, GROUP_BRAS_ARRIERE_GAUCHE)
                .addServo(brasArriereGaucheEpaule)
                .addServo(brasArriereGaucheCoude)
                .addServo(brasArriereGauchePoignet)
                .batch(POS_INIT)
                .batch(POS_0DEG);

        Servo brasArriereCentreEpaule = servo(BRAS_ARRIERE_CENTRE_EPAULE_ID, BRAS_ARRIERE_CENTRE_EPAULE)
                .time(500)
                .angular()
                .angle(A1_MAX, 2520)
                .angle(0, 2270)
                .angle(-90, 1370)
                .angle(A1_MIN, 630)
                .position(POS_INIT, A1_INIT, 50)
                .position(POS_0DEG, 0, 50)
                .build();
        Servo brasArriereCentreCoude = servo(BRAS_ARRIERE_CENTRE_COUDE_ID, BRAS_ARRIERE_CENTRE_COUDE)
                .time(500)
                .angular()
                .angle(A2_MIN, 2570)
                .angle(0, 2080)
                .angle(90, 1200)
                .angle(A2_MAX, 700)
                .position(POS_INIT, A2_INIT, 50)
                .position(POS_0DEG, 0, 50)
                .build();
        Servo brasArriereCentrePoignet = servo(BRAS_ARRIERE_CENTRE_POIGNET_ID, BRAS_ARRIERE_CENTRE_POIGNET)
                .time(500)
                .angular()
                .angle(A3_MAX, 410)
                .angle(0, 770)
                .angle(-90, 1670)
                .angle(A3_MIN, 2040)
                .position(POS_INIT, A3_INIT, 50)
                .position(POS_0DEG, 0, 50)
                .build();
        group(GROUP_BRAS_ARRIERE_CENTRE_ID, GROUP_BRAS_ARRIERE_CENTRE)
                .addServo(brasArriereCentreEpaule)
                .addServo(brasArriereCentreCoude)
                .addServo(brasArriereCentrePoignet)
                .batch(POS_INIT)
                .batch(POS_0DEG);

        Servo brasArriereDroitEpaule = servo(BRAS_ARRIERE_DROIT_EPAULE_ID, BRAS_ARRIERE_DROIT_EPAULE)
                .time(500)
                .angular()
                .angle(A1_MAX, 2490)
                .angle(0, 2270)
                .angle(-90, 1330)
                .angle(A1_MIN, 620)
                .position(POS_INIT, A1_INIT, 50)
                .position(POS_0DEG, 0, 50)
                .build();
        Servo brasArriereDroitCoude = servo(BRAS_ARRIERE_DROIT_COUDE_ID, BRAS_ARRIERE_DROIT_COUDE)
                .time(500)
                .angular()
                .angle(A2_MIN, 640)
                .angle(0, 1070)
                .angle(90, 1960)
                .angle(A2_MAX, 2460)
                .position(POS_INIT, A2_INIT, 50)
                .position(POS_0DEG, 0, 50)
                .build();
        Servo brasArriereDroitPoignet = servo(BRAS_ARRIERE_DROIT_POIGNET_ID, BRAS_ARRIERE_DROIT_POIGNET)
                .time(500)
                .angular()
                .angle(A3_MAX, 2470)
                .angle(0, 2140)
                .angle(-90, 1240)
                .angle(A3_MIN, 890)
                .position(POS_INIT, A3_INIT, 50)
                .position(POS_0DEG, 0, 50)
                .build();
        group(GROUP_BRAS_ARRIERE_DROIT_ID, GROUP_BRAS_ARRIERE_DROIT)
                .addServo(brasArriereDroitEpaule)
                .addServo(brasArriereDroitCoude)
                .addServo(brasArriereDroitPoignet)
                .batch(POS_INIT)
                .batch(POS_0DEG);

        Servo pinceAvantGauche = servo(BRAS_AVANT_GAUCHE_PINCE_ID, BRAS_AVANT_GAUCHE_PINCE)
                .time(300)
                .position(POS_FERME, 700)
                .position(POS_OUVERT, 1600)
                .position(POS_PRISE_POT, 1050)
                .position(POS_PRISE_POT_INT, 1200)
                .position(POS_PRISE_PLANTE, 1000);
        Servo pinceAvantCentre = servo(BRAS_AVANT_CENTRE_PINCE_ID, BRAS_AVANT_CENTRE_PINCE)
                .time(300)
                .position(POS_FERME, 700)
                .position(POS_OUVERT, 1700)
                .position(POS_PRISE_POT, 1150)
                .position(POS_PRISE_POT_INT, 1350)
                .position(POS_PRISE_PLANTE, 1000);
        Servo pinceAvantDroit = servo(BRAS_AVANT_DROIT_PINCE_ID, BRAS_AVANT_DROIT_PINCE)
                .time(300)
                .position(POS_FERME, 1200)
                .position(POS_OUVERT, 2100)
                .position(POS_PRISE_POT, 1700)
                .position(POS_PRISE_POT_INT, 1650)
                .position(POS_PRISE_PLANTE, 1500);
        group(GROUP_PINCE_AVANT_ID, GROUP_PINCE_AVANT)
                .addServo(pinceAvantGauche)
                .addServo(pinceAvantCentre)
                .addServo(pinceAvantDroit)
                .batch(POS_FERME)
                .batch(POS_OUVERT)
                .batch(POS_PRISE_POT)
                .batch(POS_PRISE_POT_INT)
                .batch(POS_PRISE_PLANTE);

        Servo pinceArriereGauche = servo(BRAS_ARRIERE_GAUCHE_PINCE_ID, BRAS_ARRIERE_GAUCHE_PINCE)
                .time(300)
                .position(POS_FERME, 730)
                .position(POS_OUVERT, 1650)
                .position(POS_PRISE_POT, 1200)
                .position(POS_PRISE_POT_INT, 1500)
                .position(POS_PRISE_PLANTE, 900);
        Servo pinceArriereCentre = servo(BRAS_ARRIERE_CENTRE_PINCE_ID, BRAS_ARRIERE_CENTRE_PINCE)
                .time(300)
                .position(POS_FERME, 1220)
                .position(POS_OUVERT, 2200)
                .position(POS_PRISE_POT, 1700)
                .position(POS_PRISE_POT_INT, 2000)
                .position(POS_PRISE_PLANTE, 1500);
        Servo pinceArriereDroit = servo(BRAS_ARRIERE_DROIT_PINCE_ID, BRAS_ARRIERE_DROIT_PINCE)
                .time(300)
                .position(POS_FERME, 960)
                .position(POS_OUVERT, 1900)
                .position(POS_PRISE_POT, 1400)
                .position(POS_PRISE_POT_INT, 1800)
                .position(POS_PRISE_PLANTE, 1200);
        group(GROUP_PINCE_ARRIERE_ID, GROUP_PINCE_ARRIERE)
                .addServo(pinceArriereGauche)
                .addServo(pinceArriereCentre)
                .addServo(pinceArriereDroit)
                .batch(POS_FERME)
                .batch(POS_OUVERT)
                .batch(POS_PRISE_PLANTE)
                .batch(POS_PRISE_POT)
                .batch(POS_PRISE_POT_INT);

        Servo bloquePlanteAvantGauche = servo(BLOQUE_PLANTE_AVANT_GAUCHE_ID, BLOQUE_PLANTE_AVANT_GAUCHE)
                .time(300)
                .position(POS_OUVERT, 2310)
                .position(POS_PRISE_POT, 1840)
                .position(POS_PRISE_PLANTE, 1800, 50)
                .position(POS_FERME, 1000);
        Servo bloquePlanteAvantCentre = servo(BLOQUE_PLANTE_AVANT_CENTRE_ID, BLOQUE_PLANTE_AVANT_CENTRE)
                .time(300)
                .position(POS_OUVERT, 1860)
                .position(POS_PRISE_POT, 1400)
                .position(POS_PRISE_PLANTE, 1290, 50)
                .position(POS_FERME, 690);
        Servo bloquePlanteAvantDroit = servo(BLOQUE_PLANTE_AVANT_DROIT_ID, BLOQUE_PLANTE_AVANT_DROIT)
                .time(300)
                .position(POS_OUVERT, 1310)
                .position(POS_PRISE_POT, 1750)
                .position(POS_PRISE_PLANTE, 1820, 50)
                .position(POS_FERME, 2540);
        group(GROUP_BLOQUE_PLANTE_AVANT_ID, GROUP_BLOQUE_PLANTE_AVANT)
                .addServo(bloquePlanteAvantGauche)
                .addServo(bloquePlanteAvantCentre)
                .addServo(bloquePlanteAvantDroit)
                .batch(POS_OUVERT)
                .batch(POS_PRISE_POT)
                .batch(POS_PRISE_PLANTE)
                .batch(POS_FERME);

        Servo panneauSolaireSki = servo(PANNEAU_SOLAIRE_SKI_ID, PANNEAU_SOLAIRE_SKI)
                .time(400)
                .position(POS_FERME, 810)
                .position(POS_OUVERT, 1800);
        Servo panneauSolaireRoue = servo(PANNEAU_SOLAIRE_ROUE_ID, PANNEAU_SOLAIRE_ROUE)
                .time(500)
                .position(POS_FERME, 1360)
                .position(POS_OUVERT, 2150, 50);
        group(GROUP_PANNEAU_SOLAIRE_ID, GROUP_PANNEAU_SOLAIRE)
                .addServo(panneauSolaireSki)
                .addServo(panneauSolaireRoue)
                .batch(POS_FERME)
                .batch(POS_OUVERT);
    }
}

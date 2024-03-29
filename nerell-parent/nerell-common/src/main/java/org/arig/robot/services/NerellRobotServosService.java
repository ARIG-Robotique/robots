package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.servos.Servo;
import org.arig.robot.system.servos.SD21Servos;
import org.springframework.stereotype.Service;

import static org.arig.robot.model.servos.Servo.POS_0DEG;

@Slf4j
public class NerellRobotServosService extends AbstractCommonRobotServosService {

    // SD21 Avant de 1 à 21.
    // SD21 Arrière de 22 à 42.

    private static final byte BRAS_AVANT_GAUCHE_EPAULE_ID = 1;
    private static final byte BRAS_AVANT_GAUCHE_COUDE_ID = 2;
    private static final byte BRAS_AVANT_GAUCHE_POIGNET_ID = 3;
    private static final byte BRAS_AVANT_GAUCHE_PINCE_ID = 4;

    private static final byte BRAS_AVANT_CENTRE_EPAULE_ID = 5;
    private static final byte BRAS_AVANT_CENTRE_COUDE_ID = 6;
    private static final byte BRAS_AVANT_CENTRE_POIGNET_ID = 7;
    private static final byte BRAS_AVANT_CENTRE_PINCE_ID = 8;

    private static final byte BRAS_AVANT_DROIT_EPAULE_ID = 9;
    private static final byte BRAS_AVANT_DROIT_COUDE_ID = 10;
    private static final byte BRAS_AVANT_DROIT_POIGNET_ID = 11;
    private static final byte BRAS_AVANT_DROIT_PINCE_ID = 12;

    private static final byte BRAS_ARRIERE_GAUCHE_EPAULE_ID = 13;
    private static final byte BRAS_ARRIERE_GAUCHE_COUDE_ID = 14;
    private static final byte BRAS_ARRIERE_GAUCHE_POIGNET_ID = 15;
    private static final byte BRAS_ARRIERE_GAUCHE_PINCE_ID = 16;

    private static final byte BRAS_ARRIERE_CENTRE_EPAULE_ID = 17;
    private static final byte BRAS_ARRIERE_CENTRE_COUDE_ID = 18;
    private static final byte BRAS_ARRIERE_CENTRE_POIGNET_ID = 19;
    private static final byte BRAS_ARRIERE_CENTRE_PINCE_ID = 20;

    private static final byte BRAS_ARRIERE_DROIT_EPAULE_ID = 21;
    private static final byte BRAS_ARRIERE_DROIT_COUDE_ID = 22;
    private static final byte BRAS_ARRIERE_DROIT_POIGNET_ID = 23;
    private static final byte BRAS_ARRIERE_DROIT_PINCE_ID = 24;

    private static final byte BLOQUE_PLANTE_AVANT_GAUCHE_ID = 25;
    private static final byte BLOQUE_PLANTE_AVANT_CENTRE_ID = 26;
    private static final byte BLOQUE_PLANTE_AVANT_DROIT_ID = 27;

    private static final byte PANNEAU_SOLAIRE_ROUE_ID = 28;
    private static final byte PANNEAU_SOLAIRE_SKI_ID = 29;

    private static final byte PORTE_POT_ID = 30;
    private static final byte PORTE_POT_GLISSIERE_ID = 31;

    public NerellRobotServosService(SD21Servos servosAvant, SD21Servos servosArriere) {
        super(servosAvant, servosArriere);

        Servo brasAvantGaucheEpaule = servo(BRAS_AVANT_GAUCHE_EPAULE_ID, BRAS_AVANT_GAUCHE_EPAULE)
                .time(500)
                .angular()
                .center(1850)
                .mult(-1)
                .angleMin(-10)
                .angleMax(110)
                .position(POS_INIT, 101, 50)
                .build();
        Servo brasAvantGaucheCoude = servo(BRAS_AVANT_GAUCHE_COUDE_ID, BRAS_AVANT_GAUCHE_COUDE)
                .time(500)
                .angular()
                .center(1520)
                .mult(-1)
                .angleMin(-105)
                .angleMax(110)
                .position(POS_INIT, 33, 50)
                .build();
        Servo brasAvantGauchePoignet = servo(BRAS_AVANT_GAUCHE_POIGNET_ID, BRAS_AVANT_GAUCHE_POIGNET)
                .time(500)
                .angular()
                .center(1450)
                .mult(-1)
                .angleMin(-100)
                .angleMax(100)
                .position(POS_INIT, -44, 50)
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
                .center(2360)
                .mult(1.1)
                .angleMin(-135)
                .angleMax(0)
                .position(POS_INIT, -76, 50)
                .build();
        Servo brasAvantCentreCoude = servo(BRAS_AVANT_CENTRE_COUDE_ID, BRAS_AVANT_CENTRE_COUDE)
                .time(500)
                .angular()
                .center(1610)
                .mult(1.1)
                .angleMin(-90)
                .angleMax(90)
                .position(POS_INIT, -16, 50)
                .build();
        Servo brasAvantCentrePoignet = servo(BRAS_AVANT_CENTRE_POIGNET_ID, BRAS_AVANT_CENTRE_POIGNET)
                .time(500)
                .angular()
                .center(1460)
                .mult(1)
                .angleMin(-100)
                .angleMax(100)
                .position(POS_INIT, 22, 50)
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
                .center(2360)
                .mult(1.1)
                .angleMin(-135)
                .angleMax(0)
                .position(POS_INIT, -76, 50)
                .build();
        Servo brasAvantDroitCoude = servo(BRAS_AVANT_DROIT_COUDE_ID, BRAS_AVANT_DROIT_COUDE)
                .time(500)
                .angular()
                .center(1610)
                .mult(1.1)
                .angleMin(-90)
                .angleMax(90)
                .position(POS_INIT, -16, 50)
                .build();
        Servo brasAvantDroitPoignet = servo(BRAS_AVANT_DROIT_POIGNET_ID, BRAS_AVANT_DROIT_POIGNET)
                .time(500)
                .angular()
                .center(1460)
                .mult(1)
                .angleMin(-100)
                .angleMax(100)
                .position(POS_INIT, 22, 50)
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
                .center(1850)
                .mult(-1)
                .angleMin(-10)
                .angleMax(110)
                .position(POS_INIT, 101, 50)
                .build();
        Servo brasArriereGaucheCoude = servo(BRAS_ARRIERE_GAUCHE_COUDE_ID, BRAS_ARRIERE_GAUCHE_COUDE)
                .time(500)
                .angular()
                .center(1520)
                .mult(-1)
                .angleMin(-105)
                .angleMax(110)
                .position(POS_INIT, 33, 50)
                .build();
        Servo brasArriereGauchePoignet = servo(BRAS_ARRIERE_GAUCHE_POIGNET_ID, BRAS_ARRIERE_GAUCHE_POIGNET)
                .time(500)
                .angular()
                .center(1450)
                .mult(-1)
                .angleMin(-100)
                .angleMax(100)
                .position(POS_INIT, -44, 50)
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
                .center(2360)
                .mult(1.1)
                .angleMin(-135)
                .angleMax(0)
                .position(POS_INIT, -76, 50)
                .build();
        Servo brasArriereCentreCoude = servo(BRAS_ARRIERE_CENTRE_COUDE_ID, BRAS_ARRIERE_CENTRE_COUDE)
                .time(500)
                .angular()
                .center(1610)
                .mult(1.1)
                .angleMin(-90)
                .angleMax(90)
                .position(POS_INIT, -16, 50)
                .build();
        Servo brasArriereCentrePoignet = servo(BRAS_ARRIERE_CENTRE_POIGNET_ID, BRAS_ARRIERE_CENTRE_POIGNET)
                .time(500)
                .angular()
                .center(1460)
                .mult(1)
                .angleMin(-100)
                .angleMax(100)
                .position(POS_INIT, 22, 50)
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
                .center(2360)
                .mult(1.1)
                .angleMin(-135)
                .angleMax(0)
                .position(POS_INIT, -76, 50)
                .build();
        Servo brasArriereDroitCoude = servo(BRAS_ARRIERE_DROIT_COUDE_ID, BRAS_ARRIERE_DROIT_COUDE)
                .time(500)
                .angular()
                .center(1610)
                .mult(1.1)
                .angleMin(-90)
                .angleMax(90)
                .position(POS_INIT, -16, 50)
                .build();
        Servo brasArriereDroitPoignet = servo(BRAS_ARRIERE_DROIT_POIGNET_ID, BRAS_ARRIERE_DROIT_POIGNET)
                .time(500)
                .angular()
                .center(1460)
                .mult(1)
                .angleMin(-100)
                .angleMax(100)
                .position(POS_INIT, 22, 50)
                .build();
        group(GROUP_BRAS_ARRIERE_DROIT_ID, GROUP_BRAS_ARRIERE_DROIT)
                .addServo(brasArriereDroitEpaule)
                .addServo(brasArriereDroitCoude)
                .addServo(brasArriereDroitPoignet)
                .batch(POS_INIT)
                .batch(POS_0DEG);

        Servo pinceAvantGauche = servo(BRAS_AVANT_GAUCHE_PINCE_ID, BRAS_AVANT_GAUCHE_PINCE)
                .time(0)
                .position(POS_INIT, 0, 50);
        Servo pinceAvantCentre = servo(BRAS_AVANT_CENTRE_PINCE_ID, BRAS_AVANT_CENTRE_PINCE)
                .time(0)
                .position(POS_INIT, 0, 50);
        Servo pinceAvantDroit = servo(BRAS_AVANT_DROIT_PINCE_ID, BRAS_AVANT_DROIT_PINCE)
                .time(0)
                .position(POS_INIT, 0, 50);
        group(GROUP_PINCE_AVANT_ID, GROUP_PINCE_AVANT)
                .addServo(pinceAvantGauche)
                .addServo(pinceAvantCentre)
                .addServo(pinceAvantDroit)
                .batch(POS_INIT);

        Servo pinceArriereGauche = servo(BRAS_ARRIERE_GAUCHE_PINCE_ID, BRAS_ARRIERE_GAUCHE_PINCE)
                .time(0)
                .position(POS_INIT, 0, 50);
        Servo pinceArriereCentre = servo(BRAS_ARRIERE_CENTRE_PINCE_ID, BRAS_ARRIERE_CENTRE_PINCE)
                .time(0)
                .position(POS_INIT, 0, 50);
        Servo pinceArriereDroit = servo(BRAS_ARRIERE_DROIT_PINCE_ID, BRAS_ARRIERE_DROIT_PINCE)
                .time(0)
                .position(POS_INIT, 0, 50);
        group(GROUP_PINCE_ARRIERE_ID, GROUP_PINCE_ARRIERE)
                .addServo(pinceArriereGauche)
                .addServo(pinceArriereCentre)
                .addServo(pinceArriereDroit)
                .batch(POS_INIT);

        Servo bloquePlanteAvantGauche = servo(BLOQUE_PLANTE_AVANT_GAUCHE_ID, BLOQUE_PLANTE_AVANT_GAUCHE)
                .time(500)
                .position(POS_INIT, 0, 50);
        Servo bloquePlanteAvantCentre = servo(BLOQUE_PLANTE_AVANT_CENTRE_ID, BLOQUE_PLANTE_AVANT_CENTRE)
                .time(500)
                .position(POS_INIT, 0, 50);
        Servo bloquePlanteAvantDroit = servo(BLOQUE_PLANTE_AVANT_DROIT_ID, BLOQUE_PLANTE_AVANT_DROIT)
                .time(500)
                .position(POS_INIT, 0, 50);
        group(GROUP_BLOQUE_PLANTE_AVANT_ID, GROUP_BLOQUE_PLANTE_AVANT)
                .addServo(bloquePlanteAvantGauche)
                .addServo(bloquePlanteAvantCentre)
                .addServo(bloquePlanteAvantDroit)
                .batch(POS_INIT);

        Servo panneauSolaireSki = servo(PANNEAU_SOLAIRE_ROUE_ID, PANNEAU_SOLAIRE_ROUE)
                .time(225)
                .position(POS_FERME, 2210)
                .position(POS_OUVERT, 1770);
        Servo panneauSolaireRoue = servo(PANNEAU_SOLAIRE_SKI_ID, PANNEAU_SOLAIRE_SKI)
                .time(250)
                .position(POS_FERME, 450)
                .position(POS_OUVERT, 450);
        group(GROUP_PANNEAU_SOLAIRE_ID, GROUP_PANNEAU_SOLAIRE)
                .addServo(panneauSolaireSki)
                .addServo(panneauSolaireRoue)
                .batch(POS_FERME)
                .batch(POS_OUVERT);

        Servo portePot = servo(PORTE_POT_ID, PORTE_POT)
                .time(500)
                .position(POS_FERME, 0)
                .position(POS_OUVERT, 180);
        Servo portePotGlissiere = servo(PORTE_POT_GLISSIERE_ID, PORTE_POT_GLISSIERE)
                .time(500)
                .position(POS_FERME, 0)
                .position(POS_OUVERT, 180);
        group(GROUP_PORTE_POT_ID, GROUP_PORTE_POT)
                .addServo(portePot)
                .addServo(portePotGlissiere)
                .batch(POS_FERME)
                .batch(POS_OUVERT);;
    }
}

package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.servos.Servo;
import org.springframework.stereotype.Service;

import static org.arig.robot.model.servos.Servo.POS_0DEG;

@Slf4j
@Service
public class NerellServosService extends AbstractCommonServosService {

    private static final byte BRAS_BAS_EPAULE_ID = 5;
    private static final byte BRAS_BAS_COUDE_ID = 4;
    private static final byte BRAS_BAS_POIGNET_ID = 6;

    private static final byte BRAS_HAUT_EPAULE_ID = 21;
    private static final byte BRAS_HAUT_COUDE_ID = 17;
    private static final byte BRAS_HAUT_POIGNET_ID = 16;

    private static final byte CARRE_FOUILLE_OHMMETRE_ID = 1;
    private static final byte CARRE_FOUILLE_POUSSOIR_ID = 18;

    private static final byte FOURCHE_STATUETTE_ID = 2;

    private static final byte MOUSTACHE_GAUCHE_ID = 3;
    private static final byte LANGUE_ID = 20;
    private static final byte MOUSTACHE_DROITE_ID = 19;

    public NerellServosService() {
        super();

        Servo brasBasEpaule = servo(BRAS_BAS_EPAULE_ID, BRAS_BAS_EPAULE)
                .time(500)
                .angular()
                .center(1880)
                .mult(-1)
                .angleMin(-10)
                .angleMax(110)
                .position(POS_INIT, 110, 50)
                .build();
        Servo brasBasCoude = servo(BRAS_BAS_COUDE_ID, BRAS_BAS_COUDE)
                .time(500)
                .angular()
                .center(1520)
                .mult(-1)
                .angleMin(-105)
                .angleMax(110)
                .position(POS_INIT, 30, 50)
                .build();
        Servo brasBasPoignet = servo(BRAS_BAS_POIGNET_ID, BRAS_BAS_POIGNET)
                .time(500)
                .angular()
                .center(1450)
                .mult(-1)
                .angleMin(-100)
                .angleMax(100)
                .position(POS_INIT, -50, 50)
                .build();
        group(GROUP_BRAS_BAS_ID, GROUP_BRAS_BAS)
                .addServo(brasBasEpaule)
                .addServo(brasBasCoude)
                .addServo(brasBasPoignet)
                .batch(POS_INIT)
                .batch(POS_0DEG);

        Servo brasHautEpaule = servo(BRAS_HAUT_EPAULE_ID, BRAS_HAUT_EPAULE)
                .time(500)
                .angular()
                .center(2360)
                .mult(1.1)
                .angleMin(-135)
                .angleMax(0)
                .position(POS_INIT, -90, 50)
                .build();
        Servo brasHautCoude = servo(BRAS_HAUT_COUDE_ID, BRAS_HAUT_COUDE)
                .time(500)
                .angular()
                .center(1610)
                .mult(1.1)
                .angleMin(-90)
                .angleMax(90)
                .position(POS_INIT, 0, 50)
                .build();
        Servo brasHautPoignet = servo(BRAS_HAUT_POIGNET_ID, BRAS_HAUT_POIGNET)
                .time(500)
                .angular()
                .center(1460)
                .mult(1)
                .angleMin(-100)
                .angleMax(100)
                .position(POS_INIT, 20, 50)
                .build();
        group(GROUP_BRAS_HAUT_ID, GROUP_BRAS_HAUT)
                .addServo(brasHautEpaule)
                .addServo(brasHautCoude)
                .addServo(brasHautPoignet)
                .batch(POS_INIT)
                .batch(POS_0DEG);

        Servo carreFouilleOhmmetre = servo(CARRE_FOUILLE_OHMMETRE_ID, CARRE_FOUILLE_OHMMETRE)
                .time(225)
                .position(POS_FERME, 2210)
                .position(POS_OUVERT, 1770)
                .position(POS_MESURE, 1550);
        Servo carreFouillePoussoir = servo(CARRE_FOUILLE_POUSSOIR_ID, CARRE_FOUILLE_POUSSOIR)
                .time(250)
                .position(POS_FERME, 450)
                .position(POS_POUSSETTE, 1050);
        group(GROUP_BRAS_MESURE_ID, GROUP_BRAS_MESURE)
                .addServo(carreFouilleOhmmetre)
                .addServo(carreFouillePoussoir);

        Servo fourcheStatuette = servo(FOURCHE_STATUETTE_ID, FOURCHE_STATUETTE)
                .time(475)
                .position(POS_FERME, 2270)
                .position(POS_VIBRATION, 2070)
                .position(POS_PRISE_DEPOSE, 940);
        group(GROUP_STATUETTE_ID, GROUP_STATUETTE)
                .addServo(fourcheStatuette);

        Servo moustacheGauche = servo(MOUSTACHE_GAUCHE_ID, MOUSTACHE_GAUCHE)
                .time(325)
                .position(POS_FERME, 2280)
                .position(POS_POUSSETTE, 1640)
                .position(POS_OUVERT, 1440);
        Servo langue = servo(LANGUE_ID, LANGUE)
                .time(350)
                .position(POS_FERME, 2270)
                .position(POS_OUVERT, 1330);
        Servo moustacheDroite = servo(MOUSTACHE_DROITE_ID, MOUSTACHE_DROITE)
                .time(325)
                .position(POS_FERME, 670)
                .position(POS_POUSSETTE, 1250)
                .position(POS_OUVERT, 1450);

        group(GROUP_ARRIERE_ID, GROUP_ARRIERE)
                .addServo(moustacheGauche)
                .addServo(langue)
                .addServo(moustacheDroite)
                .batch(POS_OUVERT)
                .batch(POS_FERME);
        group(GROUP_MOUSTACHE_ID, GROUP_MOUSTACHE)
                .addServo(moustacheGauche)
                .addServo(moustacheDroite)
                .batch(POS_OUVERT)
                .batch(POS_POUSSETTE)
                .batch(POS_FERME);
    }
}

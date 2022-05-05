package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.servos.Servo;
import org.springframework.stereotype.Service;

import static org.arig.robot.model.servos.Servo.POS_0DEG;

@Slf4j
@Service
public class OdinServosService extends AbstractCommonServosService {

    private static final byte BRAS_BAS_EPAULE_ID = 19;
    private static final byte BRAS_BAS_COUDE_ID = 11;
    private static final byte BRAS_BAS_POIGNET_ID = 12;

    private static final byte BRAS_HAUT_EPAULE_ID = 7;
    private static final byte BRAS_HAUT_COUDE_ID = 21;
    private static final byte BRAS_HAUT_POIGNET_ID = 20;

    private static final byte CARRE_FOUILLE_OHMMETRE_ID = 4;
    private static final byte CARRE_FOUILLE_POUSSOIR_ID = 2;

    private static final byte FOURCHE_STATUETTE_ID = 3;
    private static final byte POUSSE_REPLIQUE_ID = 5;

    private static final byte MOUSTACHE_GAUCHE_ID = 6;
    private static final byte LANGUE_ID = 13;
    private static final byte MOUSTACHE_DROITE_ID = 1;

    public OdinServosService() {
        super();

        Servo brasBasEpaule = servo(BRAS_BAS_EPAULE_ID, BRAS_BAS_EPAULE)
                .time(500)
                .angular()
                .center(1770)
                .mult(-1)
                .angleMin(-10)
                .angleMax(110)
                .position(POS_INIT, 110, 50)
                .build();
        Servo brasBasCoude = servo(BRAS_BAS_COUDE_ID, BRAS_BAS_COUDE)
                .time(500)
                .angular()
                .center(1500)
                .mult(-1)
                .angleMin(-105)
                .angleMax(105) // FIXME ?
                .position(POS_INIT, 28, 50)
                .build();
        Servo brasBasPoignet = servo(BRAS_BAS_POIGNET_ID, BRAS_BAS_POIGNET)
                .time(500)
                .angular()
                .center(1400)
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
                .center(2280)
                .mult(1.1)
                .angleMin(-135)
                .angleMax(0)
                .position(POS_INIT, -90, 50)
                .build();
        Servo brasHautCoude = servo(BRAS_HAUT_COUDE_ID, BRAS_HAUT_COUDE)
                .time(500)
                .angular()
                .center(1840)
                .mult(1)
                .angleMin(-90)
                .angleMax(70) // FIXME ? démonter le bras
                .position(POS_INIT, 0, 50)
                .build();
        Servo brasHautPoignet = servo(BRAS_HAUT_POIGNET_ID, BRAS_HAUT_POIGNET)
                .time(500)
                .angular()
                .center(1390)
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
                .position(POS_FERME, 2310)
                .position(POS_OUVERT, 1810)
                .position(POS_MESURE, 1610);
        Servo carreFouillePoussoir = servo(CARRE_FOUILLE_POUSSOIR_ID, CARRE_FOUILLE_POUSSOIR)
                .time(250)
                .position(POS_FERME, 730)
                .position(POS_POUSSETTE, 1300);
        group(GROUP_BRAS_MESURE_ID, GROUP_BRAS_MESURE)
                .addServo(carreFouilleOhmmetre)
                .addServo(carreFouillePoussoir);

        Servo fourcheStatuette = servo(FOURCHE_STATUETTE_ID, FOURCHE_STATUETTE)
                .time(475)
                .position(POS_FERME, 2370)
                .position(POS_PRISE_DEPOSE, 980);
        Servo pousseReplique = servo(POUSSE_REPLIQUE_ID, POUSSE_REPLIQUE)
                .time(375)
                .position(POS_FERME, 1870)
                .position(POS_POUSSETTE, 670);
        group(GROUP_STATUETTE_ID, GROUP_STATUETTE)
                .addServo(fourcheStatuette)
                .addServo(pousseReplique);

        Servo moustacheGauche = servo(MOUSTACHE_GAUCHE_ID, MOUSTACHE_GAUCHE)
                .time(325)
                .position(POS_FERME, 2330)
                .position(POS_POUSSETTE, 1700)
                .position(POS_OUVERT, 1500);
        Servo langue = servo(LANGUE_ID, LANGUE)
                .time(350)
                .position(POS_FERME, 2270)
                .position(POS_FERME, 2270)
                .position(POS_OUVERT, 1340);
        Servo moustacheDroite = servo(MOUSTACHE_DROITE_ID, MOUSTACHE_DROITE)
                .time(325)
                .position(POS_FERME, 700)
                .position(POS_POUSSETTE, 1360)
                .position(POS_OUVERT, 1560);

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

    @Override
    public boolean pousseReplique() {
        return true;
    }

    @Override
    public void homes() {
        super.homes();
        pousseRepliqueFerme(false);
    }
}

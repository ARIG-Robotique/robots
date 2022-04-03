package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.servos.Servo;
import org.springframework.stereotype.Service;

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
                .position(POS_REPOS, 1500);
        Servo brasBasCoude = servo(BRAS_BAS_COUDE_ID, BRAS_BAS_COUDE)
                .time(500)
                .position(POS_REPOS, 1500);
        Servo brasBasPoignet = servo(BRAS_BAS_POIGNET_ID, BRAS_BAS_POIGNET)
                .time(500)
                .position(POS_REPOS, 1500);
        group(GROUP_BRAS_BAS_ID, GROUP_BRAS_BAS)
                .addServo(brasBasEpaule)
                .addServo(brasBasCoude)
                .addServo(brasBasPoignet)
                .batch(POS_FERME);

        Servo brasHautEpaule = servo(BRAS_HAUT_EPAULE_ID, BRAS_HAUT_EPAULE)
                .time(500)
                .position(POS_REPOS, 1500);
        Servo brasHautCoude = servo(BRAS_HAUT_COUDE_ID, BRAS_HAUT_COUDE)
                .time(500)
                .position(POS_REPOS, 1500);
        Servo brasHautPoignet = servo(BRAS_HAUT_POIGNET_ID, BRAS_HAUT_POIGNET)
                .time(500)
                .position(POS_REPOS, 1500);
        group(GROUP_BRAS_HAUT_ID, GROUP_BRAS_HAUT)
                .addServo(brasHautEpaule)
                .addServo(brasHautCoude)
                .addServo(brasHautPoignet)
                .batch(POS_FERME);

        Servo carreFouilleOhmmetre = servo(CARRE_FOUILLE_OHMMETRE_ID, CARRE_FOUILLE_OHMMETRE)
                .time(500)
                .position(POS_FERME, 1500)
                .position(POS_MESURE, 1500);
        Servo carreFouillePoussoir = servo(CARRE_FOUILLE_POUSSOIR_ID, CARRE_FOUILLE_POUSSOIR)
                .time(500)
                .position(POS_FERME, 1500)
                .position(POS_POUSSETTE, 1500);
        group(GROUP_BRAS_MESURE_ID, GROUP_BRAS_MESURE)
                .addServo(carreFouilleOhmmetre)
                .addServo(carreFouillePoussoir);

        Servo fourcheStatuette = servo(FOURCHE_STATUETTE_ID, FOURCHE_STATUETTE)
                .time(500)
                .position(POS_FERME, 1500)
                .position(POS_PRISE_DEPOSE, 1500);
        group(GROUP_STATUETTE_ID, GROUP_STATUETTE)
                .addServo(fourcheStatuette);

        Servo moustacheGauche = servo(MOUSTACHE_GAUCHE_ID, MOUSTACHE_GAUCHE)
                .time(500)
                .position(POS_FERME, 1500)
                .position(POS_OUVERT, 1500);
        Servo langue = servo(LANGUE_ID, LANGUE)
                .time(500)
                .position(POS_FERME, 1500)
                .position(POS_OUVERT, 1500);
        Servo moustacheDroite = servo(MOUSTACHE_DROITE_ID, MOUSTACHE_DROITE)
                .time(500)
                .position(POS_FERME, 1500)
                .position(POS_OUVERT, 1500);

        group(GROUP_ARRIERE_ID, GROUP_ARRIERE)
                .addServo(moustacheGauche)
                .addServo(langue)
                .addServo(moustacheDroite)
                .batch(POS_OUVERT)
                .batch(POS_FERME);
    }

}

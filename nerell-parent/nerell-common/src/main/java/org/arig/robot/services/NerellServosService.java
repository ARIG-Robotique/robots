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

    private static final byte BRAS_MESURE_CARRE_FOUILLE_ID = 1;
    private static final byte BRAS_POUSSE_CARRE_FOUILLE_ID = 18;

    private static final byte PINCE_STATUETTE_ID = 2;

    private static final byte TRAPPE_ARRIERE_GAUCHE_ID = 3;
    private static final byte TRAPPE_ARRIERE_CENTRE_ID = 20;
    private static final byte TRAPPE_ARRIERE_DROITE_ID = 19;

    private static final byte GROUP_BRAS_BAS_ID = 1;
    private static final byte GROUP_BRAS_HAUT_ID = 2;
    private static final byte GROUP_BRAS_MESURE_ID = 3;
    private static final byte GROUP_STATUETTE_ID = 4;
    private static final byte GROUP_TRAPPES_ARRIERE_ID = 5;

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
                .addServo(brasBasPoignet);

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
                .addServo(brasHautPoignet);

        Servo brasMesureCarreFouille = servo(BRAS_MESURE_CARRE_FOUILLE_ID, BRAS_MESURE_CARRE_FOUILLE)
                .time(500)
                .position(POS_FERME, 1500)
                .position(POS_MESURE, 1500);
        Servo brasPousseCarreFouille = servo(BRAS_POUSSE_CARRE_FOUILLE_ID, BRAS_POUSSE_CARRE_FOUILLE)
                .time(500)
                .position(POS_FERME, 1500)
                .position(POS_POUSSETTE, 1500);
        group(GROUP_BRAS_MESURE_ID, GROUP_BRAS_MESURE)
                .addServo(brasMesureCarreFouille)
                .addServo(brasPousseCarreFouille);

        Servo pinceStatuette = servo(PINCE_STATUETTE_ID, PINCE_STATUETTE)
                .time(500)
                .position(POS_FERME, 1500)
                .position(POS_PRISE_DEPOSE, 1500);
        group(GROUP_STATUETTE_ID, GROUP_STATUETTE)
                .addServo(pinceStatuette);

        Servo trappeArriereGauche = servo(TRAPPE_ARRIERE_GAUCHE_ID, TRAPPE_ARRIERE_GAUCHE)
                .time(500)
                .position(POS_FERME, 1500)
                .position(POS_OUVERT, 1500);
        Servo trappeArriereCentre = servo(TRAPPE_ARRIERE_CENTRE_ID, TRAPPE_ARRIERE_CENTRE)
                .time(500)
                .position(POS_FERME, 1500)
                .position(POS_OUVERT, 1500);
        Servo trappeArriereDroite = servo(TRAPPE_ARRIERE_DROITE_ID, TRAPPE_ARRIERE_DROITE)
                .time(500)
                .position(POS_FERME, 1500)
                .position(POS_OUVERT, 1500);

        group(GROUP_TRAPPES_ARRIERE_ID, GROUP_TRAPPES_LATERAL_ARRIERE)
                .addServo(trappeArriereGauche)
                .addServo(trappeArriereCentre)
                .addServo(trappeArriereDroite)
                .batch(POS_OUVERT)
                .batch(POS_FERME);
    }

}

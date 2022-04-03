package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.servos.Servo;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class OdinServosService extends AbstractCommonServosService {

    protected static final String POUSSE_REPLIQUE = "Pousse replique";

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
                .max(1770).angleMax(0)
                .min(890).angleMin(90)
                .position(POS_REPOS, 700);
        Servo brasBasCoude = servo(BRAS_BAS_COUDE_ID, BRAS_BAS_COUDE)
                .time(500)
                .max(2390).angleMax(-90)
                .min(600).angleMin(90)
                .position(POS_REPOS, 1190);
        Servo brasBasPoignet = servo(BRAS_BAS_POIGNET_ID, BRAS_BAS_POIGNET)
                .time(500)
                .max(2290).angleMax(-90)
                .min(500).angleMin(90)
                .position(POS_REPOS, 1830);
        group(GROUP_BRAS_BAS_ID, GROUP_BRAS_BAS)
                .addServo(brasBasEpaule)
                .addServo(brasBasCoude)
                .addServo(brasBasPoignet)
                .batch(POS_FERME);

        Servo brasHautEpaule = servo(BRAS_HAUT_EPAULE_ID, BRAS_HAUT_EPAULE)
                .time(500)
                .max(2280).angleMax(0)
                .min(1330).angleMin(-90)
                .position(POS_REPOS, 1330);
        Servo brasHautCoude = servo(BRAS_HAUT_COUDE_ID, BRAS_HAUT_COUDE)
                .time(500)
                .max(1840).angleMax(0)
                .min(920).angleMin(-90)
                .position(POS_REPOS, 1840);
        Servo brasHautPoignet = servo(BRAS_HAUT_POIGNET_ID, BRAS_HAUT_POIGNET)
                .time(500)
                .max(2310).angleMax(90)
                .min(460).angleMin(-90)
                .position(POS_REPOS, 1700);
        group(GROUP_BRAS_HAUT_ID, GROUP_BRAS_HAUT)
                .addServo(brasHautEpaule)
                .addServo(brasHautCoude)
                .addServo(brasHautPoignet)
                .batch(POS_FERME);

        Servo carreFouilleOhmmetre = servo(CARRE_FOUILLE_OHMMETRE_ID, CARRE_FOUILLE_OHMMETRE)
                .time(500)
                .position(POS_FERME, 2310)
                .position(POS_OUVERT, 1810)
                .position(POS_MESURE, 1610);
        Servo carreFouillePoussoir = servo(CARRE_FOUILLE_POUSSOIR_ID, CARRE_FOUILLE_POUSSOIR)
                .time(500)
                .position(POS_FERME, 730)
                .position(POS_POUSSETTE, 1300);
        group(GROUP_BRAS_MESURE_ID, GROUP_BRAS_MESURE)
                .addServo(carreFouilleOhmmetre)
                .addServo(carreFouillePoussoir);

        Servo fourcheStatuette = servo(FOURCHE_STATUETTE_ID, FOURCHE_STATUETTE)
                .time(500)
                .position(POS_FERME, 2310)
                .position(POS_PRISE_DEPOSE, 980);
        Servo pousseReplique = servo(POUSSE_REPLIQUE_ID, POUSSE_REPLIQUE)
                .time(500)
                .position(POS_FERME, 1870)
                .position(POS_POUSSETTE, 670);
        group(GROUP_STATUETTE_ID, GROUP_STATUETTE)
                .addServo(fourcheStatuette)
                .addServo(pousseReplique);

        Servo moustacheGauche = servo(MOUSTACHE_GAUCHE_ID, MOUSTACHE_GAUCHE)
                .time(500)
                .position(POS_FERME, 2330)
                .position(POS_OUVERT, 1450);
        Servo langue = servo(LANGUE_ID, LANGUE)
                .time(500)
                .position(POS_FERME, 2270)
                .position(POS_OUVERT, 1340);
        Servo moustacheDroite = servo(MOUSTACHE_DROITE_ID, MOUSTACHE_DROITE)
                .time(500)
                .position(POS_FERME, 700)
                .position(POS_OUVERT, 1590);

        group(GROUP_ARRIERE_ID, GROUP_ARRIERE)
                .addServo(moustacheGauche)
                .addServo(langue)
                .addServo(moustacheDroite)
                .batch(POS_OUVERT)
                .batch(POS_FERME);
    }

    @Override
    public void homes() {
        super.homes();
        pousseRepliqueFerme(false);
    }

    public void pousseRepliqueFerme(boolean wait) {
        setPosition(POUSSE_REPLIQUE, POS_FERME, wait);
    }

    public void pousseRepliquePoussette(boolean wait) {
        setPosition(POUSSE_REPLIQUE, POS_POUSSETTE, wait);
    }

}

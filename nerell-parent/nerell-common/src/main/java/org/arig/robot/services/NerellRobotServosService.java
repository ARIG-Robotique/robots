package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.servos.Servo;
import org.arig.robot.system.servos.i2c.SD21Servos;

@Slf4j
public class NerellRobotServosService extends AbstractCommonRobotServosService {

  public static final byte TIROIR_AVANT_ID = 17;
  public static final byte BEC_AVANT_ID = 21;
  public static final byte ASCENSEUR_AVANT_ID = 16;
  public static final byte PINCE_AVANT_GAUCHE_ID = 12;
  public static final byte DOIGT_AVANT_GAUCHE_ID = 15;
  public static final byte PINCE_AVANT_DROIT_ID = 14;
  public static final byte DOIGT_AVANT_DROIT_ID = 13;
  public static final byte BLOCK_COLONNE_AVANT_GAUCHE_ID = 4;
  public static final byte BLOCK_COLONNE_AVANT_DROIT_ID = 2;

  public static final byte TIROIR_ARRIERE_ID = 8;
  public static final byte BEC_ARRIERE_ID = 7;
  public static final byte ASCENSEUR_ARRIERE_ID = 9;
  public static final byte PINCE_ARRIERE_GAUCHE_ID = 10;
  public static final byte DOIGT_ARRIERE_GAUCHE_ID = 6;
  public static final byte PINCE_ARRIERE_DROIT_ID = 11;
  public static final byte DOIGT_ARRIERE_DROIT_ID = 5;
  public static final byte BLOCK_COLONNE_ARRIERE_GAUCHE_ID = 1;
  public static final byte BLOCK_COLONNE_ARRIERE_DROIT_ID = 3;

  public static final int TIME_TIROIR = 350;
  public static final int TIME_BEC = 480;
  public static final int TIME_ASCENSEUR = 1000;
  public static final int TIME_PINCES = 750;
  public static final int TIME_DOIGTS = 450;
  public static final int TIME_BLOCK_COLONNE = 250;

  public NerellRobotServosService(SD21Servos servos) {
    super(servos);

    Servo tiroirAvant = servo(TIROIR_AVANT_ID, TIROIR_AVANT)
      .time(TIME_TIROIR)
      .position(POS_DEPOSE, 930)
      .position(POS_PRISE, 1190)
      .position(POS_STOCK, 1830);
    Servo becAvant = servo(BEC_AVANT_ID, BEC_AVANT)
      .time(TIME_BEC)
      .position(POS_REPOS, 800)
      .position(POS_OUVERT, 1900)
      .position(POS_SPLIT, 1730)
      .position(POS_FERME, 1610);
    Servo ascAvant = servo(ASCENSEUR_AVANT_ID, ASCENSEUR_AVANT)
      .time(TIME_ASCENSEUR)
      .position(POS_HAUT, 580)
      .position(POS_SPLIT, 750)
      .position(POS_STOCK, 870)
      .position(POS_ETAGE_2, 980)
      .position(POS_BANDEROLE, 2060)
      .position(POS_REPOS, 2160) // 106 mm
      .position(POS_BAS, 2290);
    group(GROUP_INDIVIDUAL_AVANT_ID, GROUP_INDIVIDUAL_AVANT)
      .addServo(tiroirAvant)
      .addServo(becAvant)
      .addServo(ascAvant);

    Servo tiroirArriere = servo(TIROIR_ARRIERE_ID, TIROIR_ARRIERE)
      .time(TIME_TIROIR)
      .position(POS_DEPOSE, 940)
      .position(POS_PRISE, 1210)
      .position(POS_STOCK, 1880);
    Servo becArriere = servo(BEC_ARRIERE_ID, BEC_ARRIERE)
      .time(TIME_BEC)
      .position(POS_REPOS, 630)
      .position(POS_OUVERT, 1800)
      .position(POS_SPLIT, 1620)
      .position(POS_FERME, 1530);
    Servo ascArriere = servo(ASCENSEUR_ARRIERE_ID, ASCENSEUR_ARRIERE)
      .time(TIME_ASCENSEUR)
      .position(POS_HAUT, 570)
      .position(POS_SPLIT, 800)
      .position(POS_STOCK, 830)
      .position(POS_ETAGE_2, 940)
      .position(POS_REPOS, 2100) // 107 mm
      .position(POS_BAS, 2280);
    group(GROUP_INDIVIDUAL_ARRIERE_ID, GROUP_INDIVIDUAL_ARRIERE)
      .addServo(tiroirArriere)
      .addServo(becArriere)
      .addServo(ascArriere);

    Servo pinceAvantGauche = servo(PINCE_AVANT_GAUCHE_ID, PINCE_AVANT_GAUCHE)
      .time(TIME_PINCES)
      .position(POS_OUVERT, 670)
      .position(POS_PRISE, 1320)
      .position(POS_PRISE_SOL, 1860)
      .position(POS_STOCK, 2025)
      .position(POS_REPOS, 2340);
    Servo pinceAvantDroite = servo(PINCE_AVANT_DROIT_ID, PINCE_AVANT_DROIT)
      .time(TIME_PINCES)
      .position(POS_OUVERT, 2350)
      .position(POS_PRISE, 1700)
      .position(POS_PRISE_SOL, 1190)
      .position(POS_STOCK, 1010) // 216mm
      .position(POS_REPOS, 730);
    group(GROUP_PINCES_AVANT_ID, GROUP_PINCES_AVANT)
      .addServo(pinceAvantGauche)
      .addServo(pinceAvantDroite)
      .batch(POS_OUVERT)
      .batch(POS_PRISE)
      .batch(POS_PRISE_SOL)
      .batch(POS_STOCK)
      .batch(POS_REPOS);

    Servo doigtAvantGauche = servo(DOIGT_AVANT_GAUCHE_ID, DOIGT_AVANT_GAUCHE)
      .time(TIME_DOIGTS)
      .position(POS_OUVERT, 2260)
      .position(POS_PRISE_SOL, 1980)
      .position(POS_LACHE, 1720)
      .position(POS_SERRE, 1425)
      .position(POS_BANDEROLE, 910)
      .position(POS_FERME, 870);
    Servo doigtAvantDroite = servo(DOIGT_AVANT_DROIT_ID, DOIGT_AVANT_DROIT)
      .time(TIME_DOIGTS)
      .position(POS_OUVERT, 810)
      .position(POS_PRISE_SOL, 1090)
      .position(POS_LACHE, 1310)
      .position(POS_SERRE, 1595)
      .position(POS_BANDEROLE, 2130)
      .position(POS_FERME, 2190);
    group(GROUP_DOIGTS_AVANT_ID, GROUP_DOIGTS_AVANT)
      .addServo(doigtAvantGauche)
      .addServo(doigtAvantDroite)
      .batch(POS_OUVERT)
      .batch(POS_PRISE_SOL)
      .batch(POS_LACHE)
      .batch(POS_SERRE)
      .batch(POS_BANDEROLE)
      .batch(POS_FERME);

    Servo pinceArriereGauche = servo(PINCE_ARRIERE_GAUCHE_ID, PINCE_ARRIERE_GAUCHE)
      .time(TIME_PINCES)
      .position(POS_OUVERT, 2360)
      .position(POS_PRISE, 1730)
      .position(POS_PRISE_SOL, 1210)
      .position(POS_STOCK, 1065)
      .position(POS_REPOS, 780);
    Servo pinceArriereDroite = servo(PINCE_ARRIERE_DROIT_ID, PINCE_ARRIERE_DROIT)
      .time(TIME_PINCES)
      .position(POS_OUVERT, 610)
      .position(POS_PRISE, 1270)
      .position(POS_PRISE_SOL, 1780)
      .position(POS_STOCK, 1950)
      .position(POS_REPOS, 2230);
    group(GROUP_PINCES_ARRIERE_ID, GROUP_PINCES_ARRIERE)
      .addServo(pinceArriereGauche)
      .addServo(pinceArriereDroite)
      .batch(POS_OUVERT)
      .batch(POS_PRISE)
      .batch(POS_PRISE_SOL)
      .batch(POS_STOCK)
      .batch(POS_REPOS);

    Servo doigtArriereGauche = servo(DOIGT_ARRIERE_GAUCHE_ID, DOIGT_ARRIERE_GAUCHE)
      .time(TIME_DOIGTS)
      .position(POS_OUVERT, 1080)
      .position(POS_PRISE_SOL, 1250)
      .position(POS_LACHE, 1510)
      .position(POS_SERRE, 1820)
      .position(POS_FERME, 2420);
    Servo doigtArriereDroite = servo(DOIGT_ARRIERE_DROIT_ID, DOIGT_ARRIERE_DROIT)
      .time(TIME_DOIGTS)
      .position(POS_OUVERT, 2160)
      .position(POS_PRISE_SOL, 1900)
      .position(POS_LACHE, 1670)
      .position(POS_SERRE, 1370)
      .position(POS_FERME, 820);
    group(GROUP_DOIGTS_ARRIERE_ID, GROUP_DOIGTS_ARRIERE)
      .addServo(doigtArriereGauche)
      .addServo(doigtArriereDroite)
      .batch(POS_OUVERT)
      .batch(POS_PRISE_SOL)
      .batch(POS_LACHE)
      .batch(POS_SERRE)
      .batch(POS_FERME);

    Servo blockColonneAvantGauche = servo(BLOCK_COLONNE_AVANT_GAUCHE_ID, BLOCK_COLONNE_AVANT_GAUCHE)
      .time(TIME_BLOCK_COLONNE)
      .position(POS_OUVERT, 1720)
      .position(POS_FERME, 1320);
    Servo blockColonneAvantDroite = servo(BLOCK_COLONNE_AVANT_DROIT_ID, BLOCK_COLONNE_AVANT_DROIT)
      .time(TIME_BLOCK_COLONNE)
      .position(POS_OUVERT, 1320)
      .position(POS_FERME, 1730);
    group(GROUP_BLOCK_COLONNE_AVANT_ID, GROUP_BLOCK_COLONNE_AVANT)
      .addServo(blockColonneAvantGauche)
      .addServo(blockColonneAvantDroite)
      .batch(POS_OUVERT)
      .batch(POS_FERME);

    Servo blockColonneArriereGauche = servo(BLOCK_COLONNE_ARRIERE_GAUCHE_ID, BLOCK_COLONNE_ARRIERE_GAUCHE)
      .time(TIME_BLOCK_COLONNE)
      .position(POS_OUVERT, 1300)
      .position(POS_FERME, 1740);
    Servo blockColonneArriereDroite = servo(BLOCK_COLONNE_ARRIERE_DROIT_ID, BLOCK_COLONNE_ARRIERE_DROIT)
      .time(TIME_BLOCK_COLONNE)
      .position(POS_OUVERT, 1700)
      .position(POS_FERME, 1300);
    group(GROUP_BLOCK_COLONNE_ARRIERE_ID, GROUP_BLOCK_COLONNE_ARRIERE)
      .addServo(blockColonneArriereGauche)
      .addServo(blockColonneArriereDroite)
      .batch(POS_OUVERT)
      .batch(POS_FERME);
  }
}

package org.arig.robot.services;

public interface IOService {

  // --------------------------------------------------------- //
  // --------------------- INFOS TECHNIQUE ------------------- //
  // --------------------------------------------------------- //

  default void refreshAllIO() {
  }

  boolean auOk();

  boolean puissanceServosOk();

  boolean puissanceMoteursOk();

  boolean tirette();

  // --------------------------------------------------------- //
  // -------------------------- INPUT ------------------------ //
  // --------------------------------------------------------- //

  // Numerique
  boolean calageAvantGauche();

  boolean calageAvantDroit();

  boolean calageArriereGauche();

  boolean calageArriereDroit();

  boolean calagePriseProduitPinceAvant();

  boolean calagePriseProduitPinceAvant(int mandatorySensors);

  boolean calagePriseProduitPinceArriere();

  boolean calagePriseProduitPinceArriere(int mandatorySensors);

  boolean calagePriseProduitSolAvant();

  boolean calagePriseProduitSolAvant(int mandatorySensors);

  boolean calagePriseProduitSolArriere();

  boolean calagePriseProduitSolArriere(int mandatorySensors);

  // --------------------------------------------------------- //
  // -------------------------- OUTPUT ----------------------- //
  // --------------------------------------------------------- //

  void enableAlimServos();

  void disableAlimServos();

  void enableAlimMoteurs();

  void disableAlimMoteurs();

}

package org.arig.robot.services;

import lombok.RequiredArgsConstructor;
import org.arig.robot.model.NerellRobotStatus;
import org.arig.robot.model.enums.TypeCalage;
import org.springframework.stereotype.Service;

@Service("IOService")
@RequiredArgsConstructor
public class NerellIOServiceSimulator extends AbstractIOServiceBouchon implements NerellIOService {

  private final NerellRobotStatus rs;

  // --------------------------------------------------------- //
  // -------------------------- INPUT ------------------------ //
  // --------------------------------------------------------- //

  // Calages
  @Override
  public boolean calagePriseProduitPinceAvant() {
    return calagePriseProduitPinceAvant(1);
  }

  @Override
  public boolean calagePriseProduitPinceAvant(int mandatorySensors) {
    return true;
  }

  @Override
  public boolean calagePriseProduitPinceArriere() {
    return calagePriseProduitPinceArriere(1);
  }

  @Override
  public boolean calagePriseProduitPinceArriere(int mandatorySensors) {
    return true;
  }

  @Override
  public boolean calagePriseProduitSolAvant() {
    return calagePriseProduitSolAvant(1);
  }

  @Override
  public boolean calagePriseProduitSolAvant(int mandatorySensors) {
    return true;
  }

  @Override
  public boolean calagePriseProduitSolArriere() {
    return calagePriseProduitSolArriere(1);
  }

  @Override
  public boolean calagePriseProduitSolArriere(int mandatorySensors) {
    return true;
  }

  @Override
  public boolean calageAvantGauche() {
    return rs.calage().contains(TypeCalage.AVANT);
  }

  @Override
  public boolean calageAvantDroit() {
    return rs.calage().contains(TypeCalage.AVANT);
  }

  @Override
  public boolean calageArriereGauche() {
    return rs.calage().contains(TypeCalage.ARRIERE);
  }

  @Override
  public boolean calageArriereDroit() {
    return rs.calage().contains(TypeCalage.ARRIERE);
  }

  // Numerique

  @Override
  public boolean pinceAvantGauche(boolean expectedSimulator) {
    return expectedSimulator;
  }

  @Override
  public boolean pinceAvantDroite(boolean expectedSimulator) {
    return expectedSimulator;
  }

  @Override
  public boolean pinceArriereGauche(boolean expectedSimulator) {
    return expectedSimulator;
  }

  @Override
  public boolean pinceArriereDroite(boolean expectedSimulator) {
    return expectedSimulator;
  }

  @Override
  public boolean solAvantGauche(boolean expectedSimulator) {
    return expectedSimulator;
  }

  @Override
  public boolean solAvantDroite(boolean expectedSimulator) {
    return expectedSimulator;
  }

  @Override
  public boolean solArriereGauche(boolean expectedSimulator) {
    return expectedSimulator;
  }

  @Override
  public boolean solArriereDroite(boolean expectedSimulator) {
    return expectedSimulator;
  }

  @Override
  public boolean tiroirAvantHaut(boolean expectedSimulator) {
    return expectedSimulator;
  }

  @Override
  public boolean tiroirAvantBas(boolean expectedSimulator) {
    return expectedSimulator;
  }

  @Override
  public boolean tiroirArriereHaut(boolean expectedSimulator) {
    return expectedSimulator;
  }

  @Override
  public boolean tiroirArriereBas(boolean expectedSimulator) {
    return expectedSimulator;
  }

  // --------------------------------------------------------- //
  // -------------------------- OUTPUT ----------------------- //
  // --------------------------------------------------------- //


  // ----------------------------------------------------------- //
  // -------------------------- BUSINESS ----------------------- //
  // ----------------------------------------------------------- //

}

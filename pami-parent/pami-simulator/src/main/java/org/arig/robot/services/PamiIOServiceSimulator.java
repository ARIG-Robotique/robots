package org.arig.robot.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.PamiRobotStatus;
import org.arig.robot.model.enums.TypeCalage;
import org.springframework.stereotype.Service;

@Slf4j
@Service("IOService")
@RequiredArgsConstructor
public class PamiIOServiceSimulator extends AbstractIOServiceBouchon implements PamiIOService {

  private final PamiRobotStatus rs;

  @Override
  public void sound() {
    log.info("Bip Bip Bip ...");
  }

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
    return false;
  }

  @Override
  public boolean calagePriseProduitPinceArriere() {
    return calagePriseProduitPinceArriere(1);
  }

  @Override
  public boolean calagePriseProduitPinceArriere(int mandatorySensors) {
    return false;
  }

  @Override
  public boolean calagePriseProduitSolAvant() {
    return calagePriseProduitSolAvant(1);
  }

  @Override
  public boolean calagePriseProduitSolAvant(int mandatorySensors) {
    return false;
  }

  @Override
  public boolean calagePriseProduitSolArriere() {
    return calagePriseProduitSolArriere(1);
  }

  @Override
  public boolean calagePriseProduitSolArriere(int mandatorySensors) {
    return false;
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
  public boolean presenceSolGauche(boolean expectedSimulator) {
    return expectedSimulator;
  }

  @Override
  public boolean presenceSolDroit(boolean expectedSimulator) {
    return expectedSimulator;
  }

  // Analogique

  // --------------------------------------------------------- //
  // -------------------------- OUTPUT ----------------------- //
  // --------------------------------------------------------- //

  // ----------------------------------------------------------- //
  // -------------------------- BUSINESS ----------------------- //
  // ----------------------------------------------------------- //

}

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
    public boolean calagePriseProduitAvant() {
        return calagePriseProduitAvant(1);
    }

    @Override
    public boolean calagePriseProduitAvant(int mandatorySensors) {
        return false;
    }

    @Override
    public boolean calagePriseProduitArriere() {
        return calagePriseProduitArriere(1);
    }

    @Override
    public boolean calagePriseProduitArriere(int mandatorySensors) {
        return false;
    }

    @Override
    public boolean calageElectroaimant() {
        return calageElectroaimant(1);
    }

    @Override
    public boolean calageElectroaimant(int mandatorySensors) {
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
    public boolean pinceAvantGauche() {
        return false;
    }

    @Override
    public boolean pinceAvantCentre() {
        return false;
    }

    @Override
    public boolean pinceAvantDroite() {
        return false;
    }

    @Override
    public boolean pinceArriereGauche() {
        return false;
    }

    @Override
    public boolean pinceArriereCentre() {
        return false;
    }

    @Override
    public boolean pinceArriereDroite() {
        return false;
    }

    @Override
    public boolean presenceAvantGauche() {
        return true;
    }

    @Override
    public boolean presenceAvantCentre() {
        return true;
    }

    @Override
    public boolean presenceAvantDroite() {
        return true;
    }

    @Override
    public boolean presenceArriereGauche() {
        return false;
    }

    @Override
    public boolean presenceArriereCentre() {
        return false;
    }

    @Override
    public boolean presenceArriereDroite() {
        return false;
    }

    @Override
    public boolean presenceStockGauche() {
        return false;
    }

    @Override
    public boolean presenceStockCentre() {
        return false;
    }

    @Override
    public boolean presenceStockDroite() {
        return false;
    }

    @Override
    public boolean inductifGauche() {
        return true;
    }

    @Override
    public boolean inductifDroit() {
        return true;
    }

    @Override public boolean inductifGaucheAverage() { return false; }
    @Override public boolean inductifDroitAverage() { return false; }
    @Override public boolean pinceAvantGaucheAverage() { return false; }
    @Override public boolean pinceAvantCentreAverage() { return false; }
    @Override public boolean pinceAvantDroiteAverage() { return false; }
    @Override public boolean pinceArriereGaucheAverage() { return false; }
    @Override public boolean pinceArriereCentreAverage() { return false; }
    @Override public boolean pinceArriereDroiteAverage() { return false; }

    // --------------------------------------------------------- //
    // -------------------------- OUTPUT ----------------------- //
    // --------------------------------------------------------- //

    @Override
    public void enableElectroAimant() {

    }

    @Override
    public void disableElectroAimant() {

    }

    @Override
    public void tournePanneauJaune() {

    }

    @Override
    public void tournePanneauJaune(int speed) {

    }

    @Override
    public void tournePanneauBleu() {

    }

    @Override
    public void tournePanneauBleu(int speed) {

    }

    @Override
    public void stopTournePanneau() {

    }

    // ----------------------------------------------------------- //
    // -------------------------- BUSINESS ----------------------- //
    // ----------------------------------------------------------- //

}

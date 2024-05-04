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
    public boolean pinceAvantGauche(boolean expectedSimulator) {
        return expectedSimulator;
    }

    @Override
    public boolean pinceAvantCentre(boolean expectedSimulator) {
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
    public boolean pinceArriereCentre(boolean expectedSimulator) {
        return expectedSimulator;
    }

    @Override
    public boolean pinceArriereDroite(boolean expectedSimulator) {
        return expectedSimulator;
    }

    @Override
    public boolean presenceAvantGauche(boolean expectedSimulator) {
        return expectedSimulator;
    }

    @Override
    public boolean presenceAvantCentre(boolean expectedSimulator) {
        return expectedSimulator;
    }

    @Override
    public boolean presenceAvantDroite(boolean expectedSimulator) {
        return expectedSimulator;
    }

    @Override
    public boolean presenceArriereGauche(boolean expectedSimulator) {
        return expectedSimulator;
    }

    @Override
    public boolean presenceArriereCentre(boolean expectedSimulator) {
        return expectedSimulator;
    }

    @Override
    public boolean presenceArriereDroite(boolean expectedSimulator) {
        return expectedSimulator;
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
    @Override public boolean pinceAvantGaucheAverage(boolean expectedSimulateur) { return expectedSimulateur; }
    @Override public boolean pinceAvantCentreAverage(boolean expectedSimulateur) { return expectedSimulateur; }
    @Override public boolean pinceAvantDroiteAverage(boolean expectedSimulateur) { return expectedSimulateur; }
    @Override public boolean pinceArriereGaucheAverage(boolean expectedSimulateur) { return expectedSimulateur; }
    @Override public boolean pinceArriereCentreAverage(boolean expectedSimulateur) { return expectedSimulateur; }
    @Override public boolean pinceArriereDroiteAverage(boolean expectedSimulateur) { return expectedSimulateur; }

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

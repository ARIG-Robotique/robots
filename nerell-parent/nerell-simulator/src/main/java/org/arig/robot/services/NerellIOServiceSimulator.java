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
        return true;
    }

    @Override
    public boolean calagePriseProduitArriere() {
        return calagePriseProduitArriere(1);
    }

    @Override
    public boolean calagePriseProduitArriere(int mandatorySensors) {
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
    public boolean stockAvantGauche(boolean expectedSimulator) {
        return expectedSimulator;
    }

    @Override
    public boolean stockAvantDroite(boolean expectedSimulator) {
        return expectedSimulator;
    }

    @Override
    public boolean stockArriereGauche(boolean expectedSimulator) {
        return expectedSimulator;
    }

    @Override
    public boolean stockArriereDroite(boolean expectedSimulator) {
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

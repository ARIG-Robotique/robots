package org.arig.robot.services;

import lombok.RequiredArgsConstructor;
import org.arig.robot.model.PamiRobotStatus;
import org.arig.robot.model.enums.TypeCalage;
import org.springframework.stereotype.Service;

@Service("IOService")
@RequiredArgsConstructor
public class PamiIOServiceSimulator extends AbstractIOServiceBouchon implements PamiIOService {

    private final PamiRobotStatus rs;

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


    // Analogique
    @Override
    public double distanceGauche() {
        return 0;
    }

    @Override
    public double distanceCentre() {
        return 0;
    }

    @Override
    public double distanceDroit() {
        return 0;
    }


    // --------------------------------------------------------- //
    // -------------------------- OUTPUT ----------------------- //
    // --------------------------------------------------------- //

    // ----------------------------------------------------------- //
    // -------------------------- BUSINESS ----------------------- //
    // ----------------------------------------------------------- //

}

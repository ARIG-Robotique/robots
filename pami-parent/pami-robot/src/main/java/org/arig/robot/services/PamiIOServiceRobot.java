package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.arig.robot.system.capteurs.can.ARIG2024AlimentationController;
import org.arig.robot.system.capteurs.i2c.ARIG2024IoPamiSensors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service("IOService")
public class PamiIOServiceRobot implements PamiIOService {

    @Autowired
    private ARIG2024AlimentationController alimentationController;

    @Autowired
    private ARIG2024IoPamiSensors arig2024IoPamiSensors;

    @Override
    public void refreshAllIO() {
        arig2024IoPamiSensors.refreshSensors();
    }

    // --------------------------------------------------------- //
    // --------------------- INFOS TECHNIQUE ------------------- //
    // --------------------------------------------------------- //

    @Override
    public boolean auOk() {
        return alimentationController.auOk();
    }

    @Override
    public boolean tirette() {
        // TODO : Implementer la tirette en réseau
        return false;
    }

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
        throw new NotImplementedException("calagePriseProduitAvant not implemented on PAMI");
    }

    @Override
    public boolean calagePriseProduitArriere() {
        return calagePriseProduitArriere(1);
    }

    @Override
    public boolean calagePriseProduitArriere(int mandatorySensors) {
        throw new NotImplementedException("calagePriseProduitArriere not implemented on PAMI");
    }

    @Override
    public boolean calageElectroaimant() {
        return calageElectroaimant(1);
    }

    @Override
    public boolean calageElectroaimant(int mandatorySensors) {
        throw new NotImplementedException("calageElectroaimant not implemented on PAMI");
    }

    @Override
    public boolean calageAvantGauche() {
        throw new NotImplementedException("calageAvantGauche not implemented on PAMI");
    }

    @Override
    public boolean calageAvantDroit() {
        throw new NotImplementedException("calageAvantDroit not implemented on PAMI");
    }

    @Override
    public boolean calageArriereGauche() {
        return arig2024IoPamiSensors.isArriereGauche();
    }

    @Override
    public boolean calageArriereDroit() {
        return arig2024IoPamiSensors.isArriereDroite();
    }

    // Numerique


    // Analogique

    @Override
    public double distanceGauche() {
        return arig2024IoPamiSensors.getGp2dGauche();
    }

    @Override
    public double distanceCentre() {
        return arig2024IoPamiSensors.getGp2dCentre();
    }

    @Override
    public double distanceDroit() {
        return arig2024IoPamiSensors.getGp2dDroite();
    }


    // --------------------------------------------------------- //
    // -------------------------- OUTPUT ----------------------- //
    // --------------------------------------------------------- //

    @Override
    public void enableAlimServos() {
        log.warn("Activation puissance servos -> NOT IMPLEMENTED");
    }

    @Override
    public void disableAlimServos() {
        log.warn("Desactivation puissance servos -> NOT IMPLEMENTED");
    }

    @Override
    public void enableAlimMoteurs() {
        log.info("Activation puissance moteurs");
        alimentationController.setInternalAlimentation(true);
    }

    @Override
    public void disableAlimMoteurs() {
        log.info("Desactivation puissance moteurs");
        alimentationController.setInternalAlimentation(false);
    }

    // ----------------------------------------------------------- //
    // -------------------------- BUSINESS ----------------------- //
    // ----------------------------------------------------------- //

}

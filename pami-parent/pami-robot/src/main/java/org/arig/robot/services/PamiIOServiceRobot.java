package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.system.capteurs.can.ARIG2024AlimentationController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service("IOService")
public class PamiIOServiceRobot implements PamiIOService {

    @Autowired
    private ARIG2024AlimentationController alimentationController;

    // --------------------------------------------------------- //
    // --------------------- INFOS TECHNIQUE ------------------- //
    // --------------------------------------------------------- //

    @Override
    public boolean auOk() {
        return alimentationController.auOk();
    }

    public boolean puissanceServosOk() {
        return auOk();
    }

    public boolean puissanceMoteursOk() {
        return auOk();
    }

    @Override
    public boolean tirette() {
        return false;
    }

    // --------------------------------------------------------- //
    // -------------------------- INPUT ------------------------ //
    // --------------------------------------------------------- //

    // Calages

    @Override
    public boolean calagePriseProduitAvant() {
        return false;
    }

    @Override
    public boolean calagePriseProduitAvant(int mandatorySensors) {
        return false;
    }

    @Override
    public boolean calagePriseProduitArriere() {
        return false;
    }

    @Override
    public boolean calagePriseProduitArriere(int mandatorySensors) {
        return false;
    }

    @Override
    public boolean calagePrisePotArriere() {
        return false;
    }

    @Override
    public boolean calagePrisePotArriere(int mandatorySensors) {
        return false;
    }

    @Override
    public boolean calageAvantGauche() {
        return false;
    }

    @Override
    public boolean calageAvantDroit() {
        return false;
    }

    @Override
    public boolean calageArriereGauche() {
        return false;
    }

    @Override
    public boolean calageArriereDroit() {
        return false;
    }

    // Numerique


    // Analogique


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

package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.arig.robot.model.RobotName;
import org.arig.robot.system.capteurs.can.ARIG2024AlimentationController;
import org.arig.robot.system.capteurs.i2c.ARIG2025IoPamiSensors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service("IOService")
public class PamiIOServiceRobot implements PamiIOService {

    @Autowired
    private ARIG2024AlimentationController alimentationController;

    @Autowired
    private ARIG2025IoPamiSensors arig2025IoPamiSensors;

    @Autowired
    private RobotName robotName;

    @Override
    public void refreshAllIO() {
        arig2025IoPamiSensors.refreshSensors();
    }

    @Override
    public void sound() {
        alimentationController.sound();
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
    public boolean calageAvantGauche() {
        throw new NotImplementedException("calageAvantGauche not implemented on PAMI");
    }

    @Override
    public boolean calageAvantDroit() {
        throw new NotImplementedException("calageAvantDroit not implemented on PAMI");
    }

    @Override
    public boolean calageArriereGauche() {
        return arig2025IoPamiSensors.isArriereGauche();
    }

    @Override
    public boolean calageArriereDroit() {
        return arig2025IoPamiSensors.isArriereDroite();
    }

    // Numerique

    @Override
    public boolean presenceSolGauche(boolean expectedSimulator) {
        return arig2025IoPamiSensors.isSolGauche();
    }

    @Override
    public boolean presenceSolDroit(boolean expectedSimulator) {
        return arig2025IoPamiSensors.isSolDroit();
    }

    // Analogique


    // --------------------------------------------------------- //
    // -------------------------- OUTPUT ----------------------- //
    // --------------------------------------------------------- //

    @Override
    public void enableAlimServos() {
        log.warn("Activation puissance servos -> NOT EXISTS");
    }

    @Override
    public void disableAlimServos() {
        log.warn("Desactivation puissance servos -> NOT EXISTS");
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

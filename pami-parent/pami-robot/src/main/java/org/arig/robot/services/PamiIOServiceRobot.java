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
    public boolean calagePriseProduitPinceAvant() {
        return calagePriseProduitPinceAvant(1);
    }

    @Override
    public boolean calagePriseProduitPinceAvant(int mandatorySensors) {
        throw new NotImplementedException("calagePriseProduitPinceAvant not implemented on PAMI");
    }

    @Override
    public boolean calagePriseProduitPinceArriere() {
        return calagePriseProduitPinceArriere(1);
    }

    @Override
    public boolean calagePriseProduitPinceArriere(int mandatorySensors) {
        throw new NotImplementedException("calagePriseProduitPinceArriere not implemented on PAMI");
    }

    @Override
    public boolean calagePriseProduitSolAvant() {
        return calagePriseProduitSolAvant(1);
    }

    @Override
    public boolean calagePriseProduitSolAvant(int mandatorySensors) {
        throw new NotImplementedException("calagePriseProduitSolAvant not implemented on PAMI");
    }

    @Override
    public boolean calagePriseProduitSolArriere() {
        return calagePriseProduitSolArriere(1);
    }

    @Override
    public boolean calagePriseProduitSolArriere(int mandatorySensors) {
        throw new NotImplementedException("calagePriseProduitSolArriere not implemented on PAMI");
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
        if (robotName.id() == RobotName.RobotIdentification.PAMI_TRIANGLE) {
            return arig2025IoPamiSensors.isInput2();
        } else if (robotName.id() == RobotName.RobotIdentification.PAMI_CARRE) {
            return arig2025IoPamiSensors.isInput2();
        } else if (robotName.id() == RobotName.RobotIdentification.PAMI_ROND) {
            return arig2025IoPamiSensors.isInput1();
        }

        // PAMI Star
        return arig2025IoPamiSensors.isInput1();
    }

    @Override
    public boolean calageArriereDroit() {
        if (robotName.id() == RobotName.RobotIdentification.PAMI_TRIANGLE) {
            return arig2025IoPamiSensors.isInput1();
        } else if (robotName.id() == RobotName.RobotIdentification.PAMI_CARRE) {
            return arig2025IoPamiSensors.isInput1();
        } else if (robotName.id() == RobotName.RobotIdentification.PAMI_ROND) {
            return arig2025IoPamiSensors.isInput2();
        }

        // PAMI Star
        return arig2025IoPamiSensors.isInput1();
    }

    // Numerique

    @Override
    public boolean presenceSolGauche(boolean expectedSimulator) {
        if (robotName.id() == RobotName.RobotIdentification.PAMI_TRIANGLE) {
            return arig2025IoPamiSensors.isInput4();
        }
        // Pas branché sur les autres.
        return true;
    }

    @Override
    public boolean presenceSolDroit(boolean expectedSimulator) {
        if (robotName.id() == RobotName.RobotIdentification.PAMI_TRIANGLE) {
            return arig2025IoPamiSensors.isInput3();
        }
        // Pas branché sur les autres.
        return true;
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

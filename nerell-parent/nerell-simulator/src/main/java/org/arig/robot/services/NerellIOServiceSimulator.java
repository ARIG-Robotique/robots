package org.arig.robot.services;

import lombok.RequiredArgsConstructor;
import org.arig.robot.model.CouleurEchantillon;
import org.arig.robot.model.NerellRobotStatus;
import org.arig.robot.model.enums.TypeCalage;
import org.arig.robot.system.capteurs.TCS34725ColorSensor;
import org.springframework.stereotype.Service;

@Service("IOService")
@RequiredArgsConstructor
public class NerellIOServiceSimulator extends AbstractIOServiceBouchon implements NerellIOService {

    private final NerellRobotStatus rs;

    private boolean presVentouseBas = false;
    private boolean presVentouseHaut = false;

    // --------------------------------------------------------- //
    // -------------------------- INPUT ------------------------ //
    // --------------------------------------------------------- //

    // Calages
    /*@Override
    public boolean calageArriereDroit() {
        return rs.calage().contains(TypeCalage.ARRIERE);
    }

    @Override
    public boolean calageArriereGauche() {
        return rs.calage().contains(TypeCalage.ARRIERE);
    }
    */

    // Numerique
    @Override
    public boolean in1_1() {
        return false;
    }

    @Override
    public boolean in1_2() {
        return false;
    }

    @Override
    public boolean in1_3() {
        return false;
    }

    @Override
    public boolean in1_4() {
        return false;
    }

    @Override
    public boolean in1_5() {
        return false;
    }

    @Override
    public boolean in1_6() {
        return false;
    }

    @Override
    public boolean in1_7() {
        return false;
    }

    @Override
    public boolean in1_8() {
        return false;
    }

    @Override
    public boolean in2_1() {
        return false;
    }

    @Override
    public boolean in2_2() {
        return false;
    }

    @Override
    public boolean in2_3() {
        return false;
    }

    @Override
    public boolean in2_4() {
        return false;
    }

    @Override
    public boolean in2_5() {
        return false;
    }

    @Override
    public boolean in2_6() {
        return false;
    }

    @Override
    public boolean in2_7() {
        return false;
    }

    @Override
    public boolean in2_8() {
        return false;
    }

    @Override
    public boolean in3_1() {
        return false;
    }

    @Override
    public boolean in3_2() {
        return false;
    }

    @Override
    public boolean in3_3() {
        return false;
    }

    @Override
    public boolean in3_4() {
        return false;
    }

    @Override
    public boolean in3_5() {
        return false;
    }

    @Override
    public boolean in3_6() {
        return false;
    }

    @Override
    public boolean in3_7() {
        return false;
    }

    @Override
    public boolean in3_8() {
        return false;
    }


    // --------------------------------------------------------- //
    // -------------------------- OUTPUT ----------------------- //
    // --------------------------------------------------------- //

    // ----------------------------------------------------------- //
    // -------------------------- BUSINESS ----------------------- //
    // ----------------------------------------------------------- //

}

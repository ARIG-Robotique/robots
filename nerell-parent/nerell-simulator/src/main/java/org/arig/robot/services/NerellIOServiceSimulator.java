package org.arig.robot.services;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.arig.robot.model.CouleurEchantillon;
import org.arig.robot.model.NerellRobotStatus;
import org.arig.robot.system.capteurs.TCS34725ColorSensor;
import org.springframework.stereotype.Service;

@Service("IOService")
@RequiredArgsConstructor
public class NerellIOServiceSimulator extends AbstractIOServiceBouchon implements NerellIOService {

    private final NerellRobotStatus nerellRobotStatus;

    private boolean presVentouseBas = false;
    private boolean presVentouseHaut = false;

    // --------------------------------------------------------- //
    // -------------------------- INPUT ------------------------ //
    // --------------------------------------------------------- //

    // Numerique

    @Override
    public boolean presenceVentouseBas() {
        return presVentouseBas;
    }

    @Override
    public boolean presenceVentouseHaut() {
        return presVentouseHaut;
    }

    @Override
    public boolean presencePriseBras() {
        return true;
    }

    @Override
    public boolean presenceStatuette() {
        return nerellRobotStatus.statuettePriseDansCeRobot();
    }

    @Override
    public boolean presenceCarreFouille(final boolean expected) {
        return expected;
    }

    @Override
    public boolean presenceStock1() {
        return nerellRobotStatus.stock()[0] != null;
    }

    @Override
    public boolean presenceStock2() {
        return nerellRobotStatus.stock()[1] != null;
    }

    @Override
    public boolean presenceStock3() {
        return nerellRobotStatus.stock()[2] != null;
    }

    @Override
    public boolean presenceStock4() {
        return nerellRobotStatus.stock()[3] != null;
    }

    @Override
    public boolean presenceStock5() {
        return nerellRobotStatus.stock()[4] != null;
    }

    @Override
    public boolean presenceStock6() {
        return nerellRobotStatus.stock()[5] != null;
    }

    @Override
    public CouleurEchantillon couleurVentouseBas() {
        return CouleurEchantillon.INCONNU;
    }

    @Override
    public CouleurEchantillon couleurVentouseHaut() {
        return CouleurEchantillon.INCONNU;
    }

    @Override
    public TCS34725ColorSensor.ColorData couleurVentouseHautRaw() {
        return new TCS34725ColorSensor.ColorData();
    }

    @Override
    public TCS34725ColorSensor.ColorData couleurVentouseBasRaw() {
        return new TCS34725ColorSensor.ColorData();
    }

    // --------------------------------------------------------- //
    // -------------------------- OUTPUT ----------------------- //
    // --------------------------------------------------------- //

    @Override
    public void enableLedCapteurCouleur() {
        // Nothing to do
    }

    @Override
    public void disableLedCapteurCouleur() {
        // Nothing to do
    }

    // ----------------------------------------------------------- //
    // -------------------------- BUSINESS ----------------------- //
    // ----------------------------------------------------------- //


    @Override
    public void disableAllPompes() {
        releaseAllPompes();
    }

    @Override
    public void enableAllPompes() {
        enablePompeVentouseBas();
        enablePompeVentouseHaut();
    }

    @Override
    public void enableForceAllPompes() {
        enableAllPompes();
    }

    @Override
    public void releaseAllPompes() {
        releasePompeVentouseBas();
        releasePompeVentouseHaut();
    }

    @Override
    public void enableForcePompeVentouseBas() {
        enablePompeVentouseBas();
    }

    @Override
    public void enableForcePompeVentouseHaut() {
        enablePompeVentouseHaut();
    }

    @Override
    public void enablePompeVentouseBas() {
        presVentouseBas = true;
    }

    @Override
    public void enablePompeVentouseHaut() {
        presVentouseHaut = true;
    }

    @Override
    public void releasePompeVentouseBas() {
        presVentouseBas = false;
    }

    @Override
    public void releasePompeVentouseHaut() {
        presVentouseBas = false;
    }
}

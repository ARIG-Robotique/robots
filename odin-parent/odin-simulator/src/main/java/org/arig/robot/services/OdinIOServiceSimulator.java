package org.arig.robot.services;

import lombok.RequiredArgsConstructor;
import org.arig.robot.model.CouleurEchantillon;
import org.arig.robot.model.OdinRobotStatus;
import org.arig.robot.system.capteurs.TCS34725ColorSensor;
import org.springframework.stereotype.Service;

@Service("IOService")
@RequiredArgsConstructor
public class OdinIOServiceSimulator extends AbstractIOServiceBouchon implements OdinIOService {

    private final OdinRobotStatus odinRobotStatus;

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
        return odinRobotStatus.statuettePriseDansCeRobot();
    }

    @Override
    public boolean presenceCarreFouille(final boolean expected) {
        return expected;
    }

    @Override
    public boolean presenceStock1() {
        return odinRobotStatus.stockDisponible() <= 6;
    }

    @Override
    public boolean presenceStock2() {
        return odinRobotStatus.stockDisponible() <= 5;
    }

    @Override
    public boolean presenceStock3() {
        return odinRobotStatus.stockDisponible() <= 4;
    }

    @Override
    public boolean presenceStock4() {
        return odinRobotStatus.stockDisponible() <= 3;
    }

    @Override
    public boolean presenceStock5() {
        return odinRobotStatus.stockDisponible() <= 2;
    }

    @Override
    public boolean presenceStock6() {
        return odinRobotStatus.stockDisponible() <= 1;
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

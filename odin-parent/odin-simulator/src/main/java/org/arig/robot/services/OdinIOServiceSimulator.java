package org.arig.robot.services;

import lombok.RequiredArgsConstructor;
import org.arig.robot.model.CouleurEchantillon;
import org.arig.robot.model.OdinRobotStatus;
import org.arig.robot.model.enums.TypeCalage;
import org.arig.robot.system.capteurs.TCS34725ColorSensor;
import org.springframework.stereotype.Service;

@Service("IOService")
@RequiredArgsConstructor
public class OdinIOServiceSimulator extends AbstractIOServiceBouchon implements OdinIOService {

    private final OdinRobotStatus rs;

    private boolean presVentouseBas = false;
    private boolean presVentouseHaut = false;

    // --------------------------------------------------------- //
    // -------------------------- INPUT ------------------------ //
    // --------------------------------------------------------- //

    // Calages
    @Override
    public boolean calageArriereDroit() {
        return rs.calage().contains(TypeCalage.ARRIERE);
    }

    @Override
    public boolean calageArriereGauche() {
        return rs.calage().contains(TypeCalage.ARRIERE);
    }

    @Override
    public boolean calageAvantBasDroit() {
        return rs.calage().contains(TypeCalage.AVANT_BAS);
    }

    @Override
    public boolean calageAvantBasGauche() {
        return rs.calage().contains(TypeCalage.AVANT_BAS);
    }

    @Override
    public boolean calageAvantHautDroit() {
        return rs.calage().contains(TypeCalage.AVANT_HAUT);
    }

    @Override
    public boolean calageAvantHautGauche() {
        return rs.calage().contains(TypeCalage.AVANT_HAUT);
    }

    @Override
    public boolean calageLatteralDroit() {
        return rs.calage().contains(TypeCalage.LATTERAL_DROIT);
    }

    @Override
    public boolean calagePriseEchantillon() {
        return rs.calage().contains(TypeCalage.PRISE_ECHANTILLON);
    }

    @Override
    public boolean calageVentouseBas() {
        return rs.calage().contains(TypeCalage.VENTOUSE_BAS) && presenceVentouseBas();
    }

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
    public boolean presencePriseBras(boolean expectedSimulation) {
        return expectedSimulation;
    }

    @Override
    public boolean presenceStatuette(boolean expectedSimulation) {
        return rs.statuettePriseDansCeRobot() || expectedSimulation;
    }

    @Override
    public boolean presenceCarreFouille(final boolean expectedSimulation) {
        return expectedSimulation;
    }

    @Override
    public boolean presenceStock1(boolean expectedSimulation) {
        return rs.stock()[0] != null || expectedSimulation;
    }

    @Override
    public boolean presenceStock2(boolean expectedSimulation) {
        return rs.stock()[1] != null || expectedSimulation;
    }

    @Override
    public boolean presenceStock3(boolean expectedSimulation) {
        return rs.stock()[2] != null || expectedSimulation;
    }

    @Override
    public boolean presenceStock4(boolean expectedSimulation) {
        return rs.stock()[3] != null || expectedSimulation;
    }

    @Override
    public boolean presenceStock5(boolean expectedSimulation) {
        return rs.stock()[4] != null || expectedSimulation;
    }

    @Override
    public boolean presenceStock6(boolean expectedSimulation) {
        return rs.stock()[5] != null || expectedSimulation;
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

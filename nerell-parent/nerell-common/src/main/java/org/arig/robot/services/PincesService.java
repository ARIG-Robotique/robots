package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.ModuleLunaire;
import org.arig.robot.model.RobotStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PincesService {

    @Autowired
    private RobotStatus rs;

    @Autowired
    private ServosService servosService;

    @Autowired
    private IIOService ioService;

    private boolean enabled = false;

    public void enable() {
        if (!enabled && !ioService.presencePinceCentre() && !ioService.presencePinceDroite()) {
            servosService.homes();
            enabled = true;
        }
    }

    public void disable() {
        if (enabled && !ioService.presencePinceCentre() && !ioService.presencePinceDroite()) {
            servosService.brasPincesFermes();
            enabled = false;
        }
    }

    public void process() {
        if (ioService.presencePinceDroite() && !servosService.isPinceDroitePriseProduit()) {
            servosService.pinceDroitePriseProduit();

            if (rs.getModuleLunaireExpected() != null) {
                rs.setModuleLunaireDroite(rs.getModuleLunaireExpected());
                if (rs.getModuleLunaireExpected().numero() != null) {
                    rs.setModuleRecupere(rs.getModuleLunaireExpected().numero());
                }
            } else {
                rs.setModuleLunaireDroite(ModuleLunaire.polychrome());
            }
            rs.setModuleLunaireExpected(null);
        }

        if (ioService.presencePinceCentre() && !servosService.isBrasPriseRobot()) {
            servosService.brasPriseRobot();

            if (rs.getModuleLunaireExpected() != null) {
                rs.setModuleLunaireCentre(rs.getModuleLunaireExpected());
                if (rs.getModuleLunaireExpected().numero() != null) {
                    rs.setModuleRecupere(rs.getModuleLunaireExpected().numero());
                }
            } else {
                rs.setModuleLunaireCentre(ModuleLunaire.polychrome());
            }
            rs.setModuleLunaireExpected(null);
        }
    }
}

package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
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

    @Autowired
    private BrasService brasService;

    private boolean enabled = false;

    public void enable() {
        /*if (!enabled && !ioService.presencePinceCentre() && !ioService.presencePinceDroite()) {
            log.info("Ouverture du bras suite à activation du service pinces");
            servosService.homes();
            enabled = true;
        }*/
    }

    public void disable() {
        /*if (enabled && !ioService.presencePinceCentre() && !ioService.presencePinceDroite()) {
            log.info("Rangement du bras suite à désactivation du service pinces");
            servosService.brasPincesFermes();
            enabled = false;
        }*/
    }

    public void process() {
        if (ioService.presencePinceDroite()) {
            if (servosService.isBrasDepose()) {
                servosService.pinceCentreOuvert();
                servosService.pinceDroitePriseProduit();
            }
            else {
                if (brasService.stockerModuleRobot()) {
                    rs.addModuleDansMagasin(rs.nextModuleLunaireExpected());
                }
            }
        }

        if (ioService.presencePinceCentre()) {
            if (brasService.stockerModuleRobot()) {
                rs.addModuleDansMagasin(rs.nextModuleLunaireExpected());
            }
        }
    }
}

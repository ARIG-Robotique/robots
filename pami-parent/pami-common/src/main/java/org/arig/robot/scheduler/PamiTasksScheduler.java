package org.arig.robot.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.PamiRobotStatus;
import org.arig.robot.model.Team;
import org.arig.robot.monitoring.MonitoringWrapper;
import org.arig.robot.services.AbstractCommonPamiServosService;
import org.arig.robot.services.AbstractEnergyService;
import org.arig.robot.services.BaliseService;
import org.arig.robot.services.IOService;
import org.arig.robot.services.PamiEcranService;
import org.arig.robot.services.PamiRobotServosService;
import org.arig.robot.system.avoiding.AvoidingService;
import org.arig.robot.system.blockermanager.SystemBlockerManager;
import org.arig.robot.system.leds.ARIG2025IoPamiLeds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PamiTasksScheduler {

    @Autowired
    private PamiRobotStatus rs;

    @Autowired
    private AvoidingService avoidingService;

    @Autowired(required = false)
    private SystemBlockerManager systemBlockerManager;

    @Autowired
    private PamiEcranService ecranService;

    @Autowired
    private IOService ioService;

    @Autowired
    private AbstractEnergyService energyService;

    @Autowired
    private PamiRobotServosService pamiRobotServosService;

    @Autowired
    private ARIG2025IoPamiLeds leds;

    @Scheduled(fixedRate = 1000)
    public void ecranTask() {
        if (rs.ecranEnabled()) {
            ecranService.process();
        }
    }

    @Scheduled(fixedDelay = 20)
    public void obstacleAvoidanceTask() {
        if (rs.avoidanceEnabled()) {
            avoidingService.process();
        }
    }

    @Scheduled(fixedDelay = 500)
    public void systemBlockerManagerTask() {
        if (systemBlockerManager != null && rs.matchEnabled() && !rs.simulateur()) {
            systemBlockerManager.process();
        }
    }

    @Scheduled(fixedDelay = 5000)
    public void systemCheckTensionTask() {
        if (rs.matchEnabled()) {
            if (!energyService.checkServos()) {
                ioService.disableAlimServos();
            }
            if (!energyService.checkMoteurs()) {
                ioService.disableAlimMoteurs();
            }
        }
    }

    private byte ledsCounter = 0;

    @Scheduled(fixedDelay = 300)
    public void showTimeTask() {
        if (rs.showTime()) {
            if (pamiRobotServosService.isOuvert1()) {
                pamiRobotServosService.handOuvert2(false);
            } else {
                pamiRobotServosService.handOuvert1(false);
            }

            ARIG2025IoPamiLeds.LedColor ledColor = rs.team() == Team.JAUNE ?
                  ARIG2025IoPamiLeds.LedColor.Yellow : ARIG2025IoPamiLeds.LedColor.Blue;
            switch(ledsCounter) {
                case 0:
                case 9:
                    leds.setAllLeds(ARIG2025IoPamiLeds.LedColor.Black);
                    break;
                case 1:
                    leds.setLedColor(ARIG2025IoPamiLeds.LedId.LED1, ledColor);
                    break;
                case 2:
                    leds.setLedColor(ARIG2025IoPamiLeds.LedId.LED1, ledColor);
                    leds.setLedColor(ARIG2025IoPamiLeds.LedId.LED2, ledColor);
                    break;
                case 3:
                  leds.setLedColor(ARIG2025IoPamiLeds.LedId.LED2, ledColor);
                  leds.setLedColor(ARIG2025IoPamiLeds.LedId.LED3, ledColor);
                  break;
                case 4:
                  leds.setLedColor(ARIG2025IoPamiLeds.LedId.LED3, ledColor);
                  leds.setLedColor(ARIG2025IoPamiLeds.LedId.LED4, ledColor);
                  break;
                case 5:
                  leds.setLedColor(ARIG2025IoPamiLeds.LedId.LED4, ledColor);
                  leds.setLedColor(ARIG2025IoPamiLeds.LedId.LED5, ledColor);
                  break;
                case 6:
                  leds.setLedColor(ARIG2025IoPamiLeds.LedId.LED5, ledColor);
                  leds.setLedColor(ARIG2025IoPamiLeds.LedId.LED6, ledColor);
                  break;
                case 7:
                  leds.setLedColor(ARIG2025IoPamiLeds.LedId.LED6, ledColor);
                  leds.setLedColor(ARIG2025IoPamiLeds.LedId.LED7, ledColor);
                  break;
                case 8:
                  leds.setLedColor(ARIG2025IoPamiLeds.LedId.LED7, ledColor);
                  break;
            }
            ledsCounter++;
            if (ledsCounter > 15) {
                ledsCounter = 0;
            }
        }
    }
}

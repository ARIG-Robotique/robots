package org.arig.robot.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.PamiRobotStatus;
import org.arig.robot.model.Team;
import org.arig.robot.services.AbstractEnergyService;
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

  private boolean enableLed = false;

  @Scheduled(fixedDelay = 1000)
  public void showTimeTask() {
    if (rs.showTime()) {
      if (pamiRobotServosService.isOuvert1()) {
        pamiRobotServosService.handOuvert2(false);
      } else {
        pamiRobotServosService.handOuvert1(false);
      }
      if (!enableLed) {
        enableLed = true;
        ARIG2025IoPamiLeds.LedColor ledColor = rs.team() == Team.JAUNE ?
          ARIG2025IoPamiLeds.LedColor.Yellow : ARIG2025IoPamiLeds.LedColor.Blue;
        leds.setAllLeds(ledColor);
      }
    }
  }
}

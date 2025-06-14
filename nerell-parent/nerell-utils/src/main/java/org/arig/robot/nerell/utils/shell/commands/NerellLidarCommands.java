package org.arig.robot.nerell.utils.shell.commands;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.lidar.Scan;
import org.arig.robot.model.lidar.ScanInfos;
import org.arig.robot.system.capteurs.socket.ILidarTelemeter;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;

@Slf4j
@ShellComponent
@ShellCommandGroup("lidar")
public class NerellLidarCommands {

  private final ILidarTelemeter lidar;

  private boolean startScanRunned;

  @Autowired
  public NerellLidarCommands(@Qualifier("lidar") ILidarTelemeter lidar) {
    this.lidar = lidar;
  }

  public Availability lidarCmdAvailable() {
    return startScanRunned ? Availability.available() : Availability.unavailable("Lancer le start scan avant cette méthode");
  }

  @ShellMethod("Controle fonctionnement du lidar")
  public void infoLidar() {
    lidar.printDeviceInfo();
  }

  @ShellMethod("Demarre le scan du Lidar")
  public void startScan() {
    lidar.startScan();
    startScanRunned = true;
  }

  @ShellMethodAvailability("lidarCmdAvailable")
  @ShellMethod("Arrete le scan du Lidar")
  public void stopScan() {
    lidar.stopScan();
    startScanRunned = false;
  }

  @ShellMethodAvailability("lidarCmdAvailable")
  @ShellMethod("Vitesse de rotation du Lidar")
  public void setLidarSpeed(@NotNull @Min(250) @Max(1023) short speed) {
    lidar.setSpeed(speed);
  }

  @ShellMethodAvailability("lidarCmdAvailable")
  @ShellMethod("Grab des données Lidar")
  public void grabLidarData() {
    grabData(lidar, 100);
  }

  private void grabData(ILidarTelemeter telemeter, int it) {
    int cpt = 0;
    do {
      final ScanInfos scanInfos = telemeter.grabData();
      StringBuilder res = new StringBuilder();
      for (int i = 0; i < Math.min(10, scanInfos.getScan().size()); i++) {
        Scan s = scanInfos.getScan().get(i);

        if (i > 0) {
          res.append(" ; ");
        }
        res.append(s.getAngleDeg());
        res.append(" ° , ");
        res.append(s.getDistanceMm());
        res.append(" mm");
      }
      log.info("{} data ignoré, {} data acquise (sample : {})", scanInfos.getIgnored(), scanInfos.getScan().size(), res);
      ThreadUtils.sleep(50);
    } while (++cpt < it);
  }
}

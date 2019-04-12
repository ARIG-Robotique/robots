package org.arig.robot.nerell.utils.shell;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.lidar.Scan;
import org.arig.robot.model.lidar.ScanInfos;
import org.arig.robot.system.capteurs.RPLidarA2TelemeterOverSocket;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Slf4j
@ShellComponent
@AllArgsConstructor
@ShellCommandGroup("Lidar")
public class LidarCommands {

    private final RPLidarA2TelemeterOverSocket lidar;

    private boolean startScanRunned;

    @ShellMethodAvailability({"stopScan", "setSpeed", "grabDatas"})
    public Availability lidarCmdAvailable() {
        return startScanRunned ? Availability.available() : Availability.unavailable("Lancer le start scan avant cette méthode");
    }

    @ShellMethod("Controle fonctionnement du lidar")
    public void info() {
        lidar.printDeviceInfo();
    }

    @ShellMethod("Demarre le scan")
    public void startScan() {
        lidar.startScan();
        startScanRunned = true;
    }

    @ShellMethod("Arrete le scan")
    public void stopScan() {
        lidar.stopScan();
        startScanRunned = false;
    }

    @ShellMethod("Vitesse de rotation")
    public void setSpeed(@NotNull @Min(250) @Max(1023) short speed) {
        lidar.setSpeed(speed);
    }

    @SneakyThrows
    @ShellMethod("Grab des données")
    public void grabDatas() {
        int cpt = 0;
        do {
            final ScanInfos scanInfos = lidar.grabDatas();
            StringBuilder res = new StringBuilder();
            for (int i = 0 ; i < Math.min(10, scanInfos.getScan().size()) ; i++) {
                Scan s = scanInfos.getScan().get(i);

                if (i > 0) {
                    res.append(" ; ");
                }
                res.append(s.getAngleDeg());
                res.append(" ° , ");
                res.append(s.getDistanceMm());
                res.append(" mm");
            }
            log.info("{} datas ignoré, {} datas acquise (sample : {})", scanInfos.getIgnored(), scanInfos.getScan().size(), res.toString());
            Thread.sleep(50);
        } while(++cpt < 100);
    }
}

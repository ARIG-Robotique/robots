package org.arig.robot;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.config.spring.NerellUtilsCheckLidarContext;
import org.arig.robot.model.lidar.Scan;
import org.arig.robot.model.lidar.ScanInfos;
import org.arig.robot.system.capteurs.RPLidarA2OverSocketTelemeter;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author gdepuille on 01/11/16.
 */
@Slf4j
public class CheckLidar {

    @SneakyThrows
    public static void main(String ... args) {
        log.info("Demarrage de Nerell en mode contrôle du fonctionnement du Lidar ...");

        AnnotationConfigApplicationContext rootContext = new AnnotationConfigApplicationContext();
        rootContext.register(NerellUtilsCheckLidarContext.class);
        rootContext.refresh();

        RPLidarA2OverSocketTelemeter rplidar = rootContext.getBean(RPLidarA2OverSocketTelemeter.class);

        rplidar.printDeviceInfo();

        log.info("Start SCAN");
        rplidar.startScan();
        Thread.sleep(3000);

        log.info("LOW speed");
        rplidar.setSpeed(RPLidarA2OverSocketTelemeter.LOW_MORTOR_PWM);
        Thread.sleep(3000);

        log.info("MAX speed");
        rplidar.setSpeed(RPLidarA2OverSocketTelemeter.MAX_MOTOR_PWM);
        Thread.sleep(3000);

        log.info("Default speed");
        rplidar.setSpeed(RPLidarA2OverSocketTelemeter.DEFAULT_MOTOR_PWM);
        Thread.sleep(3000);

        int cpt = 0;
        do {
            final ScanInfos scanInfos = rplidar.grabDatas();
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

        } while(++cpt < 60);

        log.info("Fin");
        rplidar.end();

        rootContext.close();
    }
}

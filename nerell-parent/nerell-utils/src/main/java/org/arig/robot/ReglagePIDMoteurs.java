package org.arig.robot;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.config.spring.ReglagePIDContext;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.filters.pid.IPidFilter;
import org.arig.robot.monitoring.IMonitoringWrapper;
import org.arig.robot.system.encoders.Abstract2WheelsEncoders;
import org.arig.robot.system.motors.AbstractPropulsionsMotors;
import org.arig.robot.utils.ConvertionRobotUnit;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author gdepuille on 01/11/16.
 */
@Slf4j
public class ReglagePIDMoteurs {

    @SneakyThrows
    public static void main(String ... args) {

        AnnotationConfigApplicationContext rootContext = new AnnotationConfigApplicationContext();
        rootContext.register(ReglagePIDContext.class);
        rootContext.refresh();

        IPidFilter pidMotG = rootContext.getBean("pidMoteurGauche", IPidFilter.class);
        IPidFilter pidMotD = rootContext.getBean("pidMoteurDroit", IPidFilter.class);
        AbstractPropulsionsMotors motors = rootContext.getBean(AbstractPropulsionsMotors.class);
        Abstract2WheelsEncoders encoders = rootContext.getBean(Abstract2WheelsEncoders.class);
        ConvertionRobotUnit conv = rootContext.getBean(ConvertionRobotUnit.class);
        IMonitoringWrapper monitor = rootContext.getBean(IMonitoringWrapper.class);

        double consigneVitessePulse = conv.mmToPulse(100);
        double cmdMotG, cmdMotD;
        encoders.reset();
        for (int i = 0 ; i < 1000 ; i++) {
            encoders.lectureValeurs();
            cmdMotG = pidMotG.compute(consigneVitessePulse, encoders.getGauche());
            cmdMotD = pidMotD.compute(consigneVitessePulse, encoders.getDroit());

            log.info("Consigne : {} ; mesure G : {} ; mesure D : {} ; cmd G : {} ; cmd D : {}",
                    consigneVitessePulse, encoders.getGauche(), encoders.getDroit(), cmdMotG, cmdMotD);
            motors.generateMouvement((int) cmdMotG, (int) cmdMotD);
            Thread.sleep((long) IConstantesNerellConfig.asservTimeMs);
        }

        motors.stopAll();
        monitor.save();

        log.info("FIN");
        rootContext.close();
    }
}

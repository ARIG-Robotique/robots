package org.arig.prehistobot;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by mythril on 20/12/13.
 */
@Slf4j
public class RobotApplication {

    public static void main(final String [] args) throws Exception {
        log.info("Demarrage du robot principal ...");

        // Configuration de Jetty
        JettyEmbeddedRunner jetty = new JettyEmbeddedRunner();
        jetty.config();
        jetty.join();

        /*
        // Bus I2C
        II2CManager i2CManager = ctx.getBean(RaspiI2CManager.class);
        i2CManager.executeScan();

        // Init servos
        SD21Servos servos = ctx.getBean(SD21Servos.class);
        servos.printVersion();
        servos.setPositionAndSpeed(ConstantesServos.SERVO_BRAS_DROIT, ConstantesServos.BRAS_DROIT_HOME, ConstantesServos.SPEED_BRAS);
        servos.setPositionAndSpeed(ConstantesServos.SERVO_BRAS_GAUCHE, ConstantesServos.BRAS_GAUCHE_HOME, ConstantesServos.SPEED_BRAS);
        servos.setPositionAndSpeed(ConstantesServos.SERVO_PORTE_DROITE, ConstantesServos.PORTE_DROITE_CLOSE, ConstantesServos.SPEED_PORTE);
        servos.setPositionAndSpeed(ConstantesServos.SERVO_PORTE_GAUCHE, ConstantesServos.PORTE_GAUCHE_CLOSE, ConstantesServos.SPEED_PORTE);

        // Initialisation Robot Manager
        RobotManager rm = ctx.getBean(RobotManager.class);
        rm.init();
        */
    }
}

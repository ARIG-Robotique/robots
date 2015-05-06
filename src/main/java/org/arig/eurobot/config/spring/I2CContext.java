package org.arig.eurobot.config.spring;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CFactory;
import org.arig.eurobot.constants.IConstantesI2C;
import org.arig.robot.communication.II2CManager;
import org.arig.robot.communication.raspi.RaspiI2CManager;
import org.arig.robot.exception.I2CException;
import org.arig.robot.system.capteurs.SRF02I2CSonar;
import org.arig.robot.system.encoders.ARIG2WheelsEncoders;
import org.arig.robot.system.motors.AbstractPropulsionsMotors;
import org.arig.robot.system.motors.MD22Motors;
import org.arig.robot.system.servos.SD21Servos;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * Created by mythril on 21/12/13.
 */
@Configuration
public class I2CContext {

    @Bean(destroyMethod = "close")
    public I2CBus i2cBus() throws IOException {
        return I2CFactory.getInstance(I2CBus.BUS_1);
    }

    @Bean
    public II2CManager i2cManager(I2CBus i2cBus) throws I2CException, IOException {
        final RaspiI2CManager manager = new RaspiI2CManager(i2cBus);
        manager.registerDevice(IConstantesI2C.SERVO_DEVICE_NAME, IConstantesI2C.SD21_ADDRESS);
        manager.registerDevice(IConstantesI2C.PROPULSION_DEVICE_NAME, IConstantesI2C.MD22_ADDRESS);
        manager.registerDevice(IConstantesI2C.CODEUR_MOTEUR_DROIT, IConstantesI2C.CODEUR_DROIT_ADDRESS);
        manager.registerDevice(IConstantesI2C.CODEUR_MOTEUR_GAUCHE, IConstantesI2C.CODEUR_GAUCHE_ADDRESS);
        manager.registerDevice(IConstantesI2C.US_FRONT, IConstantesI2C.US_FRONT_ADDRESS);
        manager.registerDevice(IConstantesI2C.US_GAUCHE, IConstantesI2C.US_GAUCHE_ADDRESS);
        manager.registerDevice(IConstantesI2C.US_DROIT, IConstantesI2C.US_DROIT_ADDRESS);
        manager.registerDevice(IConstantesI2C.US_BACK, IConstantesI2C.US_BACK_ADDRESS);
        manager.registerDevice(IConstantesI2C.CAPTEUR_RGB, IConstantesI2C.CAPTEUR_RGB_ADDRESS);

        // Enregistrement juste pour le scan.
        manager.registerDevice(IConstantesI2C.PCF_ALIM_DEVICE_NAME, IConstantesI2C.PCF_ALIM_ADDRESS);
        manager.registerDevice(IConstantesI2C.PCF_NUM1_DEVICE_NAME, IConstantesI2C.PCF_NUM1_ADDRESS);
        manager.registerDevice(IConstantesI2C.PCF_NUM2_DEVICE_NAME, IConstantesI2C.PCF_NUM2_ADDRESS);

        return manager;
    }

    @Bean
    public AbstractPropulsionsMotors motors() {
        // Configuration de la carte moteur propulsion.
        MD22Motors md22 = new MD22Motors(IConstantesI2C.PROPULSION_DEVICE_NAME, (byte) 1, (byte) 0);
        md22.assignMotors(2, 1);
        return md22;
    }

    @Bean
    public SD21Servos servos() {
        return new SD21Servos(IConstantesI2C.SERVO_DEVICE_NAME);
    }

    @Bean
    public ARIG2WheelsEncoders encoders() {
        return new ARIG2WheelsEncoders(IConstantesI2C.CODEUR_MOTEUR_GAUCHE, IConstantesI2C.CODEUR_MOTEUR_DROIT);
    }

    @Bean(name = "usFront")
    public SRF02I2CSonar usFront() {
        return new SRF02I2CSonar(IConstantesI2C.US_FRONT);
    }

    @Bean(name = "usGauche")
    public SRF02I2CSonar usGauche() {
        return new SRF02I2CSonar(IConstantesI2C.US_GAUCHE);
    }

    @Bean(name = "usDroit")
    public SRF02I2CSonar usDroit() {
        return new SRF02I2CSonar(IConstantesI2C.US_DROIT);
    }

    @Bean(name = "usBack")
    public SRF02I2CSonar usBack() {
        return new SRF02I2CSonar(IConstantesI2C.US_BACK);
    }

}

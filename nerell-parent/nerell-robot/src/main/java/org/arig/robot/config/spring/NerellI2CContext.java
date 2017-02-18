package org.arig.robot.config.spring;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CFactory;
import com.pi4j.io.i2c.I2CFactory.UnsupportedBusNumberException;
import org.arig.robot.communication.II2CManager;
import org.arig.robot.communication.raspi.RaspiI2CManager;
import org.arig.robot.constants.IConstantesI2C;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.constants.IConstantesServos;
import org.arig.robot.exception.I2CException;
import org.arig.robot.system.capteurs.I2CAdcAnalogInput;
import org.arig.robot.system.capteurs.TCS34725ColorSensor;
import org.arig.robot.system.encoders.ARIG2WheelsEncoders;
import org.arig.robot.system.motors.AbstractPropulsionsMotors;
import org.arig.robot.system.motors.SD21Motors;
import org.arig.robot.system.servos.SD21Servos;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * @author gdepuille on 21/12/13.
 */
@Configuration
public class NerellI2CContext {

    @Bean(destroyMethod = "close")
    public I2CBus i2cBus() throws IOException, UnsupportedBusNumberException {
        return I2CFactory.getInstance(I2CBus.BUS_1);
    }

    @Bean
    public II2CManager i2cManager(I2CBus i2cBus) throws I2CException, IOException {
        final RaspiI2CManager manager = new RaspiI2CManager(i2cBus);
        manager.registerDevice(IConstantesI2C.SERVO_DEVICE_NAME, IConstantesI2C.SD21_ADDRESS);
        manager.registerDevice(IConstantesI2C.CODEUR_MOTEUR_DROIT, IConstantesI2C.CODEUR_DROIT_ADDRESS);
        manager.registerDevice(IConstantesI2C.CODEUR_MOTEUR_GAUCHE, IConstantesI2C.CODEUR_GAUCHE_ADDRESS);
        manager.registerDevice(IConstantesI2C.I2C_ADC_DEVICE_NAME, IConstantesI2C.I2C_ADC_ADDRESS);
        manager.registerDevice(IConstantesI2C.TCS34725_DEVICE_NAME, IConstantesI2C.TCS34725_ADDRESS);

        // Enregistrement juste pour le scan.
        manager.registerDevice(IConstantesI2C.PCF_ALIM_DEVICE_NAME, IConstantesI2C.PCF_ALIM_ADDRESS);
        manager.registerDevice(IConstantesI2C.PCF_NUM1_DEVICE_NAME, IConstantesI2C.PCF_NUM1_ADDRESS);
        manager.registerDevice(IConstantesI2C.PCF_NUM2_DEVICE_NAME, IConstantesI2C.PCF_NUM2_ADDRESS);

        return manager;
    }

    @Bean
    public AbstractPropulsionsMotors motors() {
        // Configuration de la carte moteur propulsion.
        final SD21Motors motors = new SD21Motors(IConstantesServos.MOTOR_1, IConstantesServos.MOTOR_2);
        motors.assignMotors(IConstantesNerellConfig.numeroMoteurGauche, IConstantesNerellConfig.numeroMoteurDroit);
        return motors;
    }

    @Bean
    public SD21Servos servos() {
        return new SD21Servos(IConstantesI2C.SERVO_DEVICE_NAME);
    }

    @Bean
    public ARIG2WheelsEncoders encoders() {
        return new ARIG2WheelsEncoders(IConstantesI2C.CODEUR_MOTEUR_GAUCHE, IConstantesI2C.CODEUR_MOTEUR_DROIT);
    }

    @Bean
    public I2CAdcAnalogInput analogInput() {
        return new I2CAdcAnalogInput(IConstantesI2C.I2C_ADC_DEVICE_NAME);
    }

    @Bean
    public TCS34725ColorSensor frontColorSensor() {
        return new TCS34725ColorSensor(IConstantesI2C.TCS34725_DEVICE_NAME);
    }
}

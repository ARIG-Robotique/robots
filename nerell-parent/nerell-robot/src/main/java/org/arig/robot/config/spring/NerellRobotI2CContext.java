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
public class NerellRobotI2CContext {

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
        manager.registerDevice(IConstantesI2C.US_LAT_GAUCHE_NAME, IConstantesI2C.US_LAT_GAUCHE_ADDRESS);
        manager.registerDevice(IConstantesI2C.US_GAUCHE_NAME, IConstantesI2C.US_GAUCHE_ADDRESS);
        //manager.registerDevice(IConstantesI2C.US_DROIT_NAME, IConstantesI2C.US_DROIT_ADDRESS);
        manager.registerDevice(IConstantesI2C.US_LAT_DROIT_NAME, IConstantesI2C.US_LAT_DROIT_ADDRESS);

        // Enregistrement juste pour le scan.
        manager.registerDevice(IConstantesI2C.PCF_ALIM_DEVICE_NAME, IConstantesI2C.PCF_ALIM_ADDRESS);
        manager.registerDevice(IConstantesI2C.PCF1_DEVICE_NAME, IConstantesI2C.PCF1_ADDRESS);
        manager.registerDevice(IConstantesI2C.PCF2_DEVICE_NAME, IConstantesI2C.PCF2_ADDRESS);
        manager.registerDevice(IConstantesI2C.PCF3_DEVICE_NAME, IConstantesI2C.PCF3_ADDRESS);

        return manager;
    }

    @Bean
    public AbstractPropulsionsMotors motors() {
        // Configuration de la carte moteur propulsion.
        final SD21Motors motors = new SD21Motors(IConstantesServos.MOTOR_DROIT, IConstantesServos.MOTOR_GAUCHE);
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
        I2CAdcAnalogInput adc = new I2CAdcAnalogInput(IConstantesI2C.I2C_ADC_DEVICE_NAME);
        adc.setPowerMode(I2CAdcAnalogInput.PowerMode.POWER_DOWN_BETWEEN_AD);
        return adc;
    }

    @Bean
    public TCS34725ColorSensor frontColorSensor() {
        return new TCS34725ColorSensor(IConstantesI2C.TCS34725_DEVICE_NAME);
    }
}

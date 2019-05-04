package org.arig.robot.config.spring;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CFactory;
import com.pi4j.io.i2c.I2CFactory.UnsupportedBusNumberException;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.communication.II2CManager;
import org.arig.robot.communication.raspi.RaspiI2CManager;
import org.arig.robot.constants.IConstantesI2C;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.exception.I2CException;
import org.arig.robot.model.RobotName;
import org.arig.robot.monitoring.IMonitoringWrapper;
import org.arig.robot.monitoring.MonitoringJsonWrapper;
import org.arig.robot.services.avoiding.BasicAvoidingService;
import org.arig.robot.services.avoiding.CompleteAvoidingService;
import org.arig.robot.system.avoiding.IAvoidingService;
import org.arig.robot.system.capteurs.I2CAdcAnalogInput;
import org.arig.robot.system.capteurs.ILidarTelemeter;
import org.arig.robot.system.capteurs.IVisionBalise;
import org.arig.robot.system.capteurs.RPLidarA2TelemeterOverSocket;
import org.arig.robot.system.capteurs.TCS34725ColorSensor;
import org.arig.robot.system.capteurs.TinyLidar;
import org.arig.robot.system.capteurs.VisionBaliseOverSocket;
import org.arig.robot.system.encoders.ARIG2WheelsEncoders;
import org.arig.robot.system.encoders.ARIGEncoder;
import org.arig.robot.system.process.RPLidarBridgeProcess;
import org.arig.robot.system.servos.SD21Servos;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;

import java.io.File;
import java.io.IOException;

/**
 * @author gdepuille on 21/12/13.
 */
@Slf4j
@Configuration
public class NerellRobotContext {

    protected RobotName robotName() {
        return new RobotName().name("Nerell (The Big One)").version("latest");
    }

    @Bean
    public RobotName robotNameBean() {
        return robotName();
    }

    @Bean
    public IMonitoringWrapper monitoringWrapper(Environment env) {
        MonitoringJsonWrapper mjw = new MonitoringJsonWrapper();
        mjw.setEnabled(env.getProperty("monitoring.points.enable", Boolean.class, true));
        return mjw;
    }

    @Bean(destroyMethod = "close")
    public I2CBus i2cBus() throws IOException, UnsupportedBusNumberException {
        return I2CFactory.getInstance(I2CBus.BUS_1);
    }

    @Bean
    public II2CManager i2cManager(I2CBus i2cBus) throws I2CException {
        final RaspiI2CManager manager = new RaspiI2CManager(i2cBus);
        manager.registerDevice(IConstantesI2C.SERVO_DEVICE_NAME, IConstantesI2C.SD21_ADDRESS);
        manager.registerDevice(IConstantesI2C.CODEUR_MOTEUR_DROIT, IConstantesI2C.CODEUR_DROIT_ADDRESS);
        manager.registerDevice(IConstantesI2C.CODEUR_MOTEUR_GAUCHE, IConstantesI2C.CODEUR_GAUCHE_ADDRESS);
        manager.registerDevice(IConstantesI2C.CODEUR_MOTEUR_CAROUSEL, IConstantesI2C.CODEUR_CAROUSEL_ADDRESS);
        manager.registerDevice(IConstantesI2C.I2C_ADC_DEVICE_NAME, IConstantesI2C.I2C_ADC_ADDRESS);
        manager.registerDevice(IConstantesI2C.TCS34725_DEVICE_NAME, IConstantesI2C.TCS34725_ADDRESS);
//        manager.registerDevice(IConstantesI2C.TINY_LIDAR_MAGASIN_DROIT_DEVICE_NAME, IConstantesI2C.TINY_LIDAR_MAGASIN_DROIT_ADDRESS, (byte) 0x44);
//        manager.registerDevice(IConstantesI2C.TINY_LIDAR_MAGASIN_GAUCHE_DEVICE_NAME, IConstantesI2C.TINY_LIDAR_MAGASIN_GAUCHE_ADDRESS, (byte) 0x44);
//        manager.registerDevice(IConstantesI2C.TINY_LIDAR_AVANT_DROIT_DEVICE_NAME, IConstantesI2C.TINY_LIDAR_AVANT_DROIT_ADDRESS, (byte) 0x44);
//        manager.registerDevice(IConstantesI2C.TINY_LIDAR_AVANT_GAUCHE_DEVICE_NAME, IConstantesI2C.TINY_LIDAR_AVANT_GAUCHE_ADDRESS, (byte) 0x44);

        // Enregistrement juste pour le scan.
        manager.registerDevice(IConstantesI2C.PCF_ALIM_DEVICE_NAME, IConstantesI2C.PCF_ALIM_ADDRESS);
        manager.registerDevice(IConstantesI2C.PCF1_DEVICE_NAME, IConstantesI2C.PCF1_ADDRESS);
        manager.registerDevice(IConstantesI2C.PCF2_DEVICE_NAME, IConstantesI2C.PCF2_ADDRESS);
        manager.registerDevice(IConstantesI2C.PCF3_DEVICE_NAME, IConstantesI2C.PCF3_ADDRESS);

        return manager;
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
    public ARIGEncoder encoderCarousel() {
        return new ARIGEncoder(IConstantesI2C.CODEUR_MOTEUR_CAROUSEL);
    }

    @Bean
    public I2CAdcAnalogInput analogInput() {
        I2CAdcAnalogInput adc = new I2CAdcAnalogInput(IConstantesI2C.I2C_ADC_DEVICE_NAME);
        adc.setPowerMode(I2CAdcAnalogInput.PowerMode.POWER_DOWN_BETWEEN_AD);
        return adc;
    }

    @Bean
    public TCS34725ColorSensor colorSensor() {
        return new TCS34725ColorSensor(IConstantesI2C.TCS34725_DEVICE_NAME);
    }

    @Bean
    public TinyLidar stockMagasinDroit() {
        return new TinyLidar(IConstantesI2C.TINY_LIDAR_MAGASIN_DROIT_DEVICE_NAME);
    }

    @Bean
    public TinyLidar stockMagasinGauche() {
        return new TinyLidar(IConstantesI2C.TINY_LIDAR_MAGASIN_GAUCHE_DEVICE_NAME);
    }

    @Bean
    public TinyLidar distanceAvantDroit() {
        return new TinyLidar(IConstantesI2C.TINY_LIDAR_AVANT_DROIT_DEVICE_NAME);
    }

    @Bean
    public TinyLidar distanceAvantGauche() {
        return new TinyLidar(IConstantesI2C.TINY_LIDAR_AVANT_GAUCHE_DEVICE_NAME);
    }

    @Bean
    public RPLidarBridgeProcess rplidarBridgeProcess() {
        return new RPLidarBridgeProcess("/opt/rplidar_bridge");
    }

    @Bean
    @DependsOn("rplidarBridgeProcess")
    public ILidarTelemeter rplidar() throws Exception {
        final File socketFile = new File(RPLidarBridgeProcess.socketPath);
        return new RPLidarA2TelemeterOverSocket(socketFile);
    }

    @Bean
    public IVisionBalise visionBalise(Environment env) {
        final String host = env.getRequiredProperty("balise.socket.host");
        final Integer port = env.getRequiredProperty("balise.socket.port", Integer.class);
        return new VisionBaliseOverSocket(host, port);
    }

    @Bean
    public IAvoidingService avoidingService(Environment env) {
        IConstantesNerellConfig.AvoidingSelection avoidingImplementation = env.getProperty("avoidance.service.implementation", IConstantesNerellConfig.AvoidingSelection.class);
        if (avoidingImplementation == IConstantesNerellConfig.AvoidingSelection.BASIC) {
            return new BasicAvoidingService();
        } else {
            return new CompleteAvoidingService();
        }
    }
}

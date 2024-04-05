package org.arig.robot.config.spring;

import com.pi4j.gpio.extension.pca.PCA9685GpioProvider;
import com.pi4j.gpio.extension.pca.PCA9685Pin;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import com.pi4j.io.i2c.I2CFactory.UnsupportedBusNumberException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.communication.i2c.I2CManager;
import org.arig.robot.communication.i2c.I2CManagerDevice;
import org.arig.robot.communication.raspi.RaspiI2CManager;
import org.arig.robot.constants.OdinConstantesConfig;
import org.arig.robot.constants.OdinConstantesI2C;
import org.arig.robot.model.RobotName;
import org.arig.robot.model.RobotName.RobotIdentification;
import org.arig.robot.model.balise.StatutBalise;
import org.arig.robot.system.avoiding.AvoidingService;
import org.arig.robot.system.avoiding.BasicAvoidingService;
import org.arig.robot.system.avoiding.BasicRetryAvoidingService;
import org.arig.robot.system.avoiding.CompleteAvoidingService;
import org.arig.robot.system.avoiding.SemiCompleteAvoidingService;
import org.arig.robot.system.capteurs.VisionBaliseOverSocket;
import org.arig.robot.system.capteurs.i2c.TCA9548MultiplexerI2C;
import org.arig.robot.system.capteurs.i2c.TCS34725ColorSensor;
import org.arig.robot.system.capteurs.i2c.TCS34725ColorSensor.Gain;
import org.arig.robot.system.capteurs.i2c.TCS34725ColorSensor.IntegrationTime;
import org.arig.robot.system.capteurs.socket.ILidarTelemeter;
import org.arig.robot.system.capteurs.socket.IVisionBalise;
import org.arig.robot.system.capteurs.socket.RPLidarA2TelemeterOverSocket;
import org.arig.robot.system.encoders.i2c.ARIGI2C2WheelsEncoders;
import org.arig.robot.system.motors.AbstractPropulsionsMotors;
import org.arig.robot.system.motors.PropulsionsPCA9685SimpleMotors;
import org.arig.robot.system.process.RPLidarBridgeProcess;
import org.arig.robot.system.servos.i2c.SD21Servos;
import org.arig.robot.system.vacuum.ARIGVacuumController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;

@Slf4j
@Configuration
public class OdinRobotContext {

    @Bean
    public RobotName robotName() {
        return RobotName.builder()
                .id(RobotIdentification.ODIN)
                .name("Odin (The replicator)")
                .version("2022 (Age of bots)")
                .build();
    }

    @Bean(destroyMethod = "close")
    public I2CBus i2cBus() throws IOException, UnsupportedBusNumberException {
        return I2CFactory.getInstance(I2CBus.BUS_1);
    }

    @Bean
    public I2CManager i2cManager(I2CBus i2cBus) throws IOException {
        final RaspiI2CManager manager = new RaspiI2CManager();

        final I2CManagerDevice<I2CDevice> pca9685 = I2CManagerDevice.<I2CDevice>builder()
                .deviceName(OdinConstantesI2C.PCA9685_DEVICE_NAME)
                .device(i2cBus.getDevice(OdinConstantesI2C.PCA9685_ADDRESS))
                .build();
        final I2CManagerDevice<I2CDevice> pcfAlim = I2CManagerDevice.<I2CDevice>builder()
                .deviceName(OdinConstantesI2C.PCF_ALIM_DEVICE_NAME)
                .device(i2cBus.getDevice(OdinConstantesI2C.PCF_ALIM_ADDRESS))
                .build();
        final I2CManagerDevice<I2CDevice> pcf1 = I2CManagerDevice.<I2CDevice>builder()
                .deviceName(OdinConstantesI2C.PCF1_DEVICE_NAME)
                .device(i2cBus.getDevice(OdinConstantesI2C.PCF1_ADDRESS))
                .build();
        final I2CManagerDevice<I2CDevice> pcf2 = I2CManagerDevice.<I2CDevice>builder()
                .deviceName(OdinConstantesI2C.PCF2_DEVICE_NAME)
                .device(i2cBus.getDevice(OdinConstantesI2C.PCF2_ADDRESS))
                .build();
        final I2CManagerDevice<I2CDevice> sd21 = I2CManagerDevice.<I2CDevice>builder()
                .deviceName(OdinConstantesI2C.SERVO_DEVICE_NAME)
                .device(i2cBus.getDevice(OdinConstantesI2C.SD21_ADDRESS))
                .build();
        final I2CManagerDevice<I2CDevice> codeurMoteurDroit = I2CManagerDevice.<I2CDevice>builder()
                .deviceName(OdinConstantesI2C.CODEUR_MOTEUR_DROIT)
                .device(i2cBus.getDevice(OdinConstantesI2C.CODEUR_DROIT_ADDRESS))
                .build();
        final I2CManagerDevice<I2CDevice> codeurMoteurGauche = I2CManagerDevice.<I2CDevice>builder()
                .deviceName(OdinConstantesI2C.CODEUR_MOTEUR_GAUCHE)
                .device(i2cBus.getDevice(OdinConstantesI2C.CODEUR_GAUCHE_ADDRESS))
                .build();
        /*final I2CManagerDevice<I2CDevice> alimMesure = I2CManagerDevice.<I2CDevice>builder()
                .deviceName(IOdinConstantesI2C.ALIM_MESURE_DEVICE_NAME)
                .device(i2cBus.getDevice(IOdinConstantesI2C.ALIM_MESURE_ADDRESS))
                .scanCmd(new byte[]{0x00})
                .build();*/
        final I2CManagerDevice<I2CDevice> controlleurPompes = I2CManagerDevice.<I2CDevice>builder()
                .deviceName(OdinConstantesI2C.VACUUM_CONTROLLER_DEVICE_NAME)
                .device(i2cBus.getDevice(OdinConstantesI2C.VACUUM_CONTROLLER_ADDRESS))
                .scanCmd(new byte[]{0x00, 0x00})
                .build();
        final I2CManagerDevice<I2CDevice> mux = I2CManagerDevice.<I2CDevice>builder()
                .deviceName(OdinConstantesI2C.MULTIPLEXEUR_I2C_NAME)
                .device(i2cBus.getDevice(OdinConstantesI2C.MULTIPLEXEUR_I2C_ADDRESS))
                .build();
        final I2CManagerDevice<I2CDevice> couleurVentouseBas = I2CManagerDevice.<I2CDevice>builder()
                .deviceName(OdinConstantesI2C.COULEUR_VENTOUSE_BAS_NAME)
                .device(i2cBus.getDevice(TCS34725ColorSensor.TCS34725_ADDRESS))
                .multiplexerDeviceName(OdinConstantesI2C.MULTIPLEXEUR_I2C_NAME)
                .multiplexerChannel(OdinConstantesI2C.COULEUR_VENTOUSE_BAS_MUX_CHANNEL)
                .build();
        final I2CManagerDevice<I2CDevice> couleurVentouseHaut = I2CManagerDevice.<I2CDevice>builder()
                .deviceName(OdinConstantesI2C.COULEUR_VENTOUSE_HAUT_NAME)
                .device(i2cBus.getDevice(TCS34725ColorSensor.TCS34725_ADDRESS))
                .multiplexerDeviceName(OdinConstantesI2C.MULTIPLEXEUR_I2C_NAME)
                .multiplexerChannel(OdinConstantesI2C.COULEUR_VENTOUSE_HAUT_MUX_CHANNEL)
                .build();

        manager.registerDevice(sd21);
        manager.registerDevice(codeurMoteurDroit);
        manager.registerDevice(codeurMoteurGauche);
        manager.registerDevice(pcfAlim);
        manager.registerDevice(pcf1);
        manager.registerDevice(pcf2);
        manager.registerDevice(pca9685);
        //manager.registerDevice(alimMesure);
        manager.registerDevice(controlleurPompes);
        manager.registerDevice(mux);
        manager.registerDevice(couleurVentouseBas);
        manager.registerDevice(couleurVentouseHaut);

        return manager;
    }

    @Bean
    public SD21Servos servos() {
        return new SD21Servos(OdinConstantesI2C.SERVO_DEVICE_NAME);
    }

    @Bean
    public ARIGI2C2WheelsEncoders encoders() {
        final ARIGI2C2WheelsEncoders encoders = new ARIGI2C2WheelsEncoders(OdinConstantesI2C.CODEUR_MOTEUR_GAUCHE, OdinConstantesI2C.CODEUR_MOTEUR_DROIT);
        encoders.setCoefs(OdinConstantesConfig.coefCodeurGauche, OdinConstantesConfig.coefCodeurDroit);
        return encoders;
    }

    @Bean
    public ARIGVacuumController vacuumController() {
        return new ARIGVacuumController(OdinConstantesI2C.VACUUM_CONTROLLER_DEVICE_NAME);
    }

    /*
    @Bean
    public ARIG2ChannelsAlimentationSensor alimentationSensor() {
        return new ARIG2ChannelsAlimentationSensor(IOdinConstantesI2C.ALIM_MESURE_DEVICE_NAME);
    }
    */

    @Bean
    public TCS34725ColorSensor couleurVentouseBas() {
        return new TCS34725ColorSensor(OdinConstantesI2C.COULEUR_VENTOUSE_BAS_NAME, IntegrationTime.TCS34725_INTEGRATIONTIME_154MS, Gain.TCS34725_GAIN_1X);
    }

    @Bean
    public TCS34725ColorSensor couleurVentouseHaut() {
        return new TCS34725ColorSensor(OdinConstantesI2C.COULEUR_VENTOUSE_HAUT_NAME, IntegrationTime.TCS34725_INTEGRATIONTIME_154MS, Gain.TCS34725_GAIN_1X);
    }

    @Bean
    public TCA9548MultiplexerI2C mux(I2CManager i2CManager) {
        final TCA9548MultiplexerI2C mux = new TCA9548MultiplexerI2C(OdinConstantesI2C.MULTIPLEXEUR_I2C_NAME);
        i2CManager.registerMultiplexerDevice(OdinConstantesI2C.MULTIPLEXEUR_I2C_NAME, mux);
        return mux;
    }

    @Bean
    @SneakyThrows
    public PCA9685GpioProvider pca9685GpioControler(I2CBus bus) {
        return new PCA9685GpioProvider(bus, OdinConstantesI2C.PCA9685_ADDRESS, new BigDecimal(200));
    }

    @Bean
    public AbstractPropulsionsMotors motors() {
        // Configuration de la carte moteur propulsion.
        final PropulsionsPCA9685SimpleMotors motors = new PropulsionsPCA9685SimpleMotors(PCA9685Pin.PWM_02, PCA9685Pin.PWM_03, PCA9685Pin.PWM_00, PCA9685Pin.PWM_01);
        motors.assignMotors(OdinConstantesConfig.numeroMoteurGauche, OdinConstantesConfig.numeroMoteurDroit);
        return motors;
    }

    @Bean
    public RPLidarBridgeProcess rplidarBridgeProcess() {
        return new RPLidarBridgeProcess("/home/pi/rplidar_bridge");
    }

    @Bean
    @DependsOn("rplidarBridgeProcess")
    public ILidarTelemeter rplidar() throws Exception {
        final File socketFile = new File(RPLidarBridgeProcess.socketPath);
        return new RPLidarA2TelemeterOverSocket(socketFile);
    }

    @Bean
    public IVisionBalise<StatutBalise> visionBalise(Environment env) {
        final String host = env.getRequiredProperty("balise.socket.host");
        final Integer port = env.getRequiredProperty("balise.socket.port", Integer.class);
        return new VisionBaliseOverSocket(host, port);
    }

    @Bean
    public AvoidingService avoidingService(Environment env) {
        AvoidingService.Mode mode = env.getProperty("robot.avoidance.implementation", AvoidingService.Mode.class, AvoidingService.Mode.BASIC);

        switch (mode) {
            case BASIC:
                return new BasicAvoidingService();
            case BASIC_RETRY:
                return new BasicRetryAvoidingService();
            case SEMI_COMPLETE:
                return new SemiCompleteAvoidingService();
            case FULL:
            default:
                return new CompleteAvoidingService();
        }
    }
}

package org.arig.robot.config.spring;

import com.pi4j.gpio.extension.pca.PCA9685GpioProvider;
import com.pi4j.gpio.extension.pca.PCA9685Pin;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import com.pi4j.io.i2c.I2CFactory.UnsupportedBusNumberException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.communication.I2CManagerDevice;
import org.arig.robot.communication.II2CManager;
import org.arig.robot.communication.raspi.RaspiI2CManager;
import org.arig.robot.constants.IConstantesI2CNerell;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.model.RobotName;
import org.arig.robot.model.balise.StatutBalise;
import org.arig.robot.system.RobotGroupOverSocket;
import org.arig.robot.system.avoiding.IAvoidingService;
import org.arig.robot.system.avoiding.impl.BasicAvoidingService;
import org.arig.robot.system.avoiding.impl.BasicRetryAvoidingService;
import org.arig.robot.system.avoiding.impl.CompleteAvoidingService;
import org.arig.robot.system.avoiding.impl.SemiCompleteAvoidingService;
import org.arig.robot.system.capteurs.EcranOverSocket;
import org.arig.robot.system.capteurs.IEcran;
import org.arig.robot.system.capteurs.ILidarTelemeter;
import org.arig.robot.system.capteurs.IVisionBalise;
import org.arig.robot.system.capteurs.RPLidarA2TelemeterOverSocket;
import org.arig.robot.system.capteurs.TCA9548MultiplexerI2C;
import org.arig.robot.system.capteurs.TCS34725ColorSensor;
import org.arig.robot.system.capteurs.TCS34725ColorSensor.Gain;
import org.arig.robot.system.capteurs.TCS34725ColorSensor.IntegrationTime;
import org.arig.robot.system.capteurs.VisionBaliseOverSocket;
import org.arig.robot.system.encoders.ARIG2WheelsEncoders;
import org.arig.robot.system.group.IRobotGroup;
import org.arig.robot.system.motors.AbstractPropulsionsMotors;
import org.arig.robot.system.motors.PropulsionsPCA9685Motors;
import org.arig.robot.system.process.EcranProcess;
import org.arig.robot.system.process.RPLidarBridgeProcess;
import org.arig.robot.system.servos.SD21Servos;
import org.arig.robot.system.vacuum.ARIGVacuumController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.concurrent.ExecutorService;

@Slf4j
@Configuration
public class NerellRobotContext {

    @Bean
    public RobotName robotName() {
        return new RobotName()
                .name("Nerell (The Big One)")
                .version("2021 (Sail the World)");
    }

    @Bean(destroyMethod = "close")
    public I2CBus i2cBus() throws IOException, UnsupportedBusNumberException {
        return I2CFactory.getInstance(I2CBus.BUS_1);
    }

    @Bean
    public II2CManager i2cManager(I2CBus i2cBus) throws IOException {
        final RaspiI2CManager manager = new RaspiI2CManager();

        final I2CManagerDevice<I2CDevice> pca9685 = I2CManagerDevice.<I2CDevice>builder()
                .deviceName(IConstantesI2CNerell.PCA9685_DEVICE_NAME)
                .device(i2cBus.getDevice(IConstantesI2CNerell.PCA9685_ADDRESS))
                .build();
        final I2CManagerDevice<I2CDevice> pcfAlim = I2CManagerDevice.<I2CDevice>builder()
                .deviceName(IConstantesI2CNerell.PCF_ALIM_DEVICE_NAME)
                .device(i2cBus.getDevice(IConstantesI2CNerell.PCF_ALIM_ADDRESS))
                .build();
        final I2CManagerDevice<I2CDevice> pcf2 = I2CManagerDevice.<I2CDevice>builder()
                .deviceName(IConstantesI2CNerell.PCF2_DEVICE_NAME)
                .device(i2cBus.getDevice(IConstantesI2CNerell.PCF2_ADDRESS))
                .build();
        final I2CManagerDevice<I2CDevice> sd21 = I2CManagerDevice.<I2CDevice>builder()
                .deviceName(IConstantesI2CNerell.SERVO_DEVICE_NAME)
                .device(i2cBus.getDevice(IConstantesI2CNerell.SD21_ADDRESS))
                .build();
        final I2CManagerDevice<I2CDevice> codeurMoteurDroit = I2CManagerDevice.<I2CDevice>builder()
                .deviceName(IConstantesI2CNerell.CODEUR_MOTEUR_DROIT)
                .device(i2cBus.getDevice(IConstantesI2CNerell.CODEUR_DROIT_ADDRESS))
                .build();
        final I2CManagerDevice<I2CDevice> codeurMoteurGauche = I2CManagerDevice.<I2CDevice>builder()
                .deviceName(IConstantesI2CNerell.CODEUR_MOTEUR_GAUCHE)
                .device(i2cBus.getDevice(IConstantesI2CNerell.CODEUR_GAUCHE_ADDRESS))
                .build();
        final I2CManagerDevice<I2CDevice> controlleurPompes = I2CManagerDevice.<I2CDevice>builder()
                .deviceName(IConstantesI2CNerell.VACUUM_CONTROLLER_DEVICE_NAME)
                .device(i2cBus.getDevice(IConstantesI2CNerell.VACUUM_CONTROLLER_ADDRESS))
                .scanCmd(new byte[]{0x00})
                .build();
        final I2CManagerDevice<I2CDevice> mux = I2CManagerDevice.<I2CDevice>builder()
                .deviceName(IConstantesI2CNerell.MULTIPLEXEUR_I2C_NAME)
                .device(i2cBus.getDevice(IConstantesI2CNerell.MULTIPLEXEUR_I2C_ADDRESS))
                .build();
        final I2CManagerDevice<I2CDevice> couleur1 = I2CManagerDevice.<I2CDevice>builder()
                .deviceName(IConstantesI2CNerell.COULEUR_1_NAME)
                .device(i2cBus.getDevice(TCS34725ColorSensor.TCS34725_ADDRESS))
                .multiplexerDeviceName(IConstantesI2CNerell.MULTIPLEXEUR_I2C_NAME)
                .multiplexerChannel(IConstantesI2CNerell.COULEUR_1_MUX_CHANNEL)
                .build();
        final I2CManagerDevice<I2CDevice> couleur2 = I2CManagerDevice.<I2CDevice>builder()
                .deviceName(IConstantesI2CNerell.COULEUR_2_NAME)
                .device(i2cBus.getDevice(TCS34725ColorSensor.TCS34725_ADDRESS))
                .multiplexerDeviceName(IConstantesI2CNerell.MULTIPLEXEUR_I2C_NAME)
                .multiplexerChannel(IConstantesI2CNerell.COULEUR_2_MUX_CHANNEL)
                .build();
        final I2CManagerDevice<I2CDevice> couleur3 = I2CManagerDevice.<I2CDevice>builder()
                .deviceName(IConstantesI2CNerell.COULEUR_3_NAME)
                .device(i2cBus.getDevice(TCS34725ColorSensor.TCS34725_ADDRESS))
                .multiplexerDeviceName(IConstantesI2CNerell.MULTIPLEXEUR_I2C_NAME)
                .multiplexerChannel(IConstantesI2CNerell.COULEUR_3_MUX_CHANNEL)
                .build();
        final I2CManagerDevice<I2CDevice> couleur4 = I2CManagerDevice.<I2CDevice>builder()
                .deviceName(IConstantesI2CNerell.COULEUR_4_NAME)
                .device(i2cBus.getDevice(TCS34725ColorSensor.TCS34725_ADDRESS))
                .multiplexerDeviceName(IConstantesI2CNerell.MULTIPLEXEUR_I2C_NAME)
                .multiplexerChannel(IConstantesI2CNerell.COULEUR_4_MUX_CHANNEL)
                .build();

        manager.registerDevice(sd21);
        manager.registerDevice(codeurMoteurDroit);
        manager.registerDevice(codeurMoteurGauche);
        manager.registerDevice(pcfAlim);
        manager.registerDevice(pcf2);
        manager.registerDevice(pca9685);
        manager.registerDevice(controlleurPompes);
        manager.registerDevice(mux);
        manager.registerDevice(couleur1);
        manager.registerDevice(couleur2);
        manager.registerDevice(couleur3);
        manager.registerDevice(couleur4);

        return manager;
    }

    @Bean
    public SD21Servos servos() {
        return new SD21Servos(IConstantesI2CNerell.SERVO_DEVICE_NAME);
    }

    @Bean
    public ARIG2WheelsEncoders encoders() {
        return new ARIG2WheelsEncoders(IConstantesI2CNerell.CODEUR_MOTEUR_GAUCHE, IConstantesI2CNerell.CODEUR_MOTEUR_DROIT);
    }

    @Bean
    @SneakyThrows
    public PCA9685GpioProvider pca9685GpioControler(I2CBus bus) {
        final PCA9685GpioProvider gpioProvider = new PCA9685GpioProvider(bus, IConstantesI2CNerell.PCA9685_ADDRESS, new BigDecimal(200));

        final GpioController gpio = GpioFactory.getInstance();
        // Moteur Gauche
        gpio.provisionPwmOutputPin(gpioProvider, PCA9685Pin.PWM_00); // PWM
        gpio.provisionPwmOutputPin(gpioProvider, PCA9685Pin.PWM_01); // Direction

        // Moteur Droit
        gpio.provisionPwmOutputPin(gpioProvider, PCA9685Pin.PWM_02); // PWM
        gpio.provisionPwmOutputPin(gpioProvider, PCA9685Pin.PWM_03); // Direction

        return gpioProvider;
    }

    @Bean
    public ARIGVacuumController vacuumController() {
        return new ARIGVacuumController(IConstantesI2CNerell.VACUUM_CONTROLLER_DEVICE_NAME);
    }

    @Bean
    public TCS34725ColorSensor couleur1() {
        return new TCS34725ColorSensor(IConstantesI2CNerell.COULEUR_1_NAME, IntegrationTime.TCS34725_INTEGRATIONTIME_24MS, Gain.TCS34725_GAIN_4X);
    }

    @Bean
    public TCS34725ColorSensor couleur2() {
        return new TCS34725ColorSensor(IConstantesI2CNerell.COULEUR_2_NAME, IntegrationTime.TCS34725_INTEGRATIONTIME_24MS, Gain.TCS34725_GAIN_4X);
    }

    @Bean
    public TCS34725ColorSensor couleur3() {
        return new TCS34725ColorSensor(IConstantesI2CNerell.COULEUR_3_NAME, IntegrationTime.TCS34725_INTEGRATIONTIME_24MS, Gain.TCS34725_GAIN_4X);
    }

    @Bean
    public TCS34725ColorSensor couleur4() {
        return new TCS34725ColorSensor(IConstantesI2CNerell.COULEUR_4_NAME, IntegrationTime.TCS34725_INTEGRATIONTIME_24MS, Gain.TCS34725_GAIN_4X);
    }

    @Bean
    public TCA9548MultiplexerI2C mux(II2CManager i2CManager) {
        final TCA9548MultiplexerI2C mux = new TCA9548MultiplexerI2C(IConstantesI2CNerell.MULTIPLEXEUR_I2C_NAME);
        i2CManager.registerMultiplexerDevice(IConstantesI2CNerell.MULTIPLEXEUR_I2C_NAME, mux);
        return mux;
    }

    @Bean
    public AbstractPropulsionsMotors motors() {
        // Configuration de la carte moteur propulsion.
        final PropulsionsPCA9685Motors motors = new PropulsionsPCA9685Motors(PCA9685Pin.PWM_02, PCA9685Pin.PWM_03, PCA9685Pin.PWM_00, PCA9685Pin.PWM_01);
        motors.assignMotors(IConstantesNerellConfig.numeroMoteurGauche, IConstantesNerellConfig.numeroMoteurDroit);
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
    public EcranProcess ecranProcess() {
        return new EcranProcess("/home/pi/nerell-gui");
    }

    @Bean
    @DependsOn("ecranProcess")
    public IEcran ecran() throws Exception {
        final File socketFile = new File(EcranProcess.socketPath);
        return new EcranOverSocket(socketFile);
    }

    @Bean
    public IVisionBalise<StatutBalise> visionBalise(Environment env) {
        final String host = env.getRequiredProperty("balise.socket.host");
        final Integer port = env.getRequiredProperty("balise.socket.port", Integer.class);
        return new VisionBaliseOverSocket(host, port);
    }

    @Bean
    public IRobotGroup robotGroup(Environment env, ExecutorService taskExecutor) throws IOException {
        final Integer serverPort = env.getRequiredProperty("robot.server.port", Integer.class);
        final String odinHost = env.getRequiredProperty("odin.socket.host");
        final Integer odinPort = env.getRequiredProperty("odin.socket.port", Integer.class);
        RobotGroupOverSocket robotGroupOverSocket = new RobotGroupOverSocket(serverPort, odinHost, odinPort, taskExecutor);
        robotGroupOverSocket.openSocket();
        return robotGroupOverSocket;
    }

    @Bean
    public IAvoidingService avoidingService(Environment env) {
        IAvoidingService.Mode mode = env.getProperty("robot.avoidance.implementation", IAvoidingService.Mode.class, IAvoidingService.Mode.BASIC);

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

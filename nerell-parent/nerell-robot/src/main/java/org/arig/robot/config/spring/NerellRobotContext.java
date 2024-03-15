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
import org.arig.robot.communication.I2CManager;
import org.arig.robot.communication.I2CManagerDevice;
import org.arig.robot.communication.raspi.RaspiI2CManager;
import org.arig.robot.constants.NerellConstantesConfig;
import org.arig.robot.constants.NerellConstantesI2C;
import org.arig.robot.model.RobotName;
import org.arig.robot.model.RobotName.RobotIdentification;
import org.arig.robot.model.balise.StatutBalise;
import org.arig.robot.system.avoiding.AvoidingService;
import org.arig.robot.system.avoiding.BasicAvoidingService;
import org.arig.robot.system.avoiding.BasicRetryAvoidingService;
import org.arig.robot.system.avoiding.CompleteAvoidingService;
import org.arig.robot.system.avoiding.SemiCompleteAvoidingService;
import org.arig.robot.system.capteurs.CarreFouilleReader;
import org.arig.robot.system.capteurs.ILidarTelemeter;
import org.arig.robot.system.capteurs.IVisionBalise;
import org.arig.robot.system.capteurs.RPLidarA2TelemeterOverSocket;
import org.arig.robot.system.capteurs.TCA9548MultiplexerI2C;
import org.arig.robot.system.capteurs.TCS34725ColorSensor;
import org.arig.robot.system.capteurs.TCS34725ColorSensor.Gain;
import org.arig.robot.system.capteurs.TCS34725ColorSensor.IntegrationTime;
import org.arig.robot.system.capteurs.VisionBaliseOverSocket;
import org.arig.robot.system.encoders.ARIGI2C2WheelsEncoders;
import org.arig.robot.system.motors.AbstractPropulsionsMotors;
import org.arig.robot.system.motors.PropulsionsPCA9685Motors;
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

@Slf4j
@Configuration
public class NerellRobotContext {

    @Bean
    public RobotName robotName() {
        return RobotName.builder()
                .id(RobotIdentification.NERELL)
                .name("Nerell (The less I do the better I am)")
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
                .deviceName(NerellConstantesI2C.PCA9685_DEVICE_NAME)
                .device(i2cBus.getDevice(NerellConstantesI2C.PCA9685_ADDRESS))
                .build();
        final I2CManagerDevice<I2CDevice> pcfAlim = I2CManagerDevice.<I2CDevice>builder()
                .deviceName(NerellConstantesI2C.PCF_ALIM_DEVICE_NAME)
                .device(i2cBus.getDevice(NerellConstantesI2C.PCF_ALIM_ADDRESS))
                .build();
        final I2CManagerDevice<I2CDevice> pcf1 = I2CManagerDevice.<I2CDevice>builder()
                .deviceName(NerellConstantesI2C.PCF1_DEVICE_NAME)
                .device(i2cBus.getDevice(NerellConstantesI2C.PCF1_ADDRESS))
                .build();
        final I2CManagerDevice<I2CDevice> pcf2 = I2CManagerDevice.<I2CDevice>builder()
                .deviceName(NerellConstantesI2C.PCF2_DEVICE_NAME)
                .device(i2cBus.getDevice(NerellConstantesI2C.PCF2_ADDRESS))
                .build();
        final I2CManagerDevice<I2CDevice> sd21 = I2CManagerDevice.<I2CDevice>builder()
                .deviceName(NerellConstantesI2C.SERVO_DEVICE_NAME)
                .device(i2cBus.getDevice(NerellConstantesI2C.SD21_ADDRESS))
                .build();
        final I2CManagerDevice<I2CDevice> codeurMoteurDroit = I2CManagerDevice.<I2CDevice>builder()
                .deviceName(NerellConstantesI2C.CODEUR_MOTEUR_DROIT)
                .device(i2cBus.getDevice(NerellConstantesI2C.CODEUR_DROIT_ADDRESS))
                .build();
        final I2CManagerDevice<I2CDevice> codeurMoteurGauche = I2CManagerDevice.<I2CDevice>builder()
                .deviceName(NerellConstantesI2C.CODEUR_MOTEUR_GAUCHE)
                .device(i2cBus.getDevice(NerellConstantesI2C.CODEUR_GAUCHE_ADDRESS))
                .build();
        /*final I2CManagerDevice<I2CDevice> alimMesure = I2CManagerDevice.<I2CDevice>builder()
                .deviceName(NerellConstantesI2C.ALIM_MESURE_DEVICE_NAME)
                .device(i2cBus.getDevice(NerellConstantesI2C.ALIM_MESURE_ADDRESS))
                .scanCmd(new byte[]{0x00})
                .build();*/
        final I2CManagerDevice<I2CDevice> controlleurPompes = I2CManagerDevice.<I2CDevice>builder()
                .deviceName(NerellConstantesI2C.VACUUM_CONTROLLER_DEVICE_NAME)
                .device(i2cBus.getDevice(NerellConstantesI2C.VACUUM_CONTROLLER_ADDRESS))
                .scanCmd(new byte[]{0x00, 0x00})
                .build();
        final I2CManagerDevice<I2CDevice> mux = I2CManagerDevice.<I2CDevice>builder()
                .deviceName(NerellConstantesI2C.MULTIPLEXEUR_I2C_NAME)
                .device(i2cBus.getDevice(NerellConstantesI2C.MULTIPLEXEUR_I2C_ADDRESS))
                .build();
        final I2CManagerDevice<I2CDevice> couleurVentouseBas = I2CManagerDevice.<I2CDevice>builder()
                .deviceName(NerellConstantesI2C.COULEUR_VENTOUSE_BAS_NAME)
                .device(i2cBus.getDevice(TCS34725ColorSensor.TCS34725_ADDRESS))
                .multiplexerDeviceName(NerellConstantesI2C.MULTIPLEXEUR_I2C_NAME)
                .multiplexerChannel(NerellConstantesI2C.COULEUR_VENTOUSE_BAS_MUX_CHANNEL)
                .build();
        final I2CManagerDevice<I2CDevice> couleurVentouseHaut = I2CManagerDevice.<I2CDevice>builder()
                .deviceName(NerellConstantesI2C.COULEUR_VENTOUSE_HAUT_NAME)
                .device(i2cBus.getDevice(TCS34725ColorSensor.TCS34725_ADDRESS))
                .multiplexerDeviceName(NerellConstantesI2C.MULTIPLEXEUR_I2C_NAME)
                .multiplexerChannel(NerellConstantesI2C.COULEUR_VENTOUSE_HAUT_MUX_CHANNEL)
                .build();
        final I2CManagerDevice<I2CDevice> carreFouilleReader = I2CManagerDevice.<I2CDevice>builder()
                .deviceName(NerellConstantesI2C.CARRE_FOUILLE_READER_NAME)
                .device(i2cBus.getDevice(NerellConstantesI2C.CARRE_FOUILLE_READER_ADDRESS))
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
        manager.registerDevice(carreFouilleReader);

        return manager;
    }

    @Bean
    public SD21Servos servos() {
        return new SD21Servos(NerellConstantesI2C.SERVO_DEVICE_NAME);
    }

    @Bean
    public ARIGI2C2WheelsEncoders encoders() {
        final ARIGI2C2WheelsEncoders encoders = new ARIGI2C2WheelsEncoders(NerellConstantesI2C.CODEUR_MOTEUR_GAUCHE, NerellConstantesI2C.CODEUR_MOTEUR_DROIT);
        encoders.setCoefs(NerellConstantesConfig.coefCodeurGauche, NerellConstantesConfig.coefCodeurDroit);
        return encoders;
    }


    @Bean
    public ARIGVacuumController vacuumController() {
        return new ARIGVacuumController(NerellConstantesI2C.VACUUM_CONTROLLER_DEVICE_NAME);
    }

    /*
    @Bean
    public ARIG2ChannelsAlimentationSensor alimentationSensor() {
        return new ARIG2ChannelsAlimentationSensor(NerellConstantesI2C.ALIM_MESURE_DEVICE_NAME);
    }
    */

    @Bean
    public TCS34725ColorSensor couleurVentouseBas() {
        return new TCS34725ColorSensor(NerellConstantesI2C.COULEUR_VENTOUSE_BAS_NAME, IntegrationTime.TCS34725_INTEGRATIONTIME_154MS, Gain.TCS34725_GAIN_1X);
    }

    @Bean
    public TCS34725ColorSensor couleurVentouseHaut() {
        return new TCS34725ColorSensor(NerellConstantesI2C.COULEUR_VENTOUSE_HAUT_NAME, IntegrationTime.TCS34725_INTEGRATIONTIME_154MS, Gain.TCS34725_GAIN_1X);
    }

    @Bean
    public TCA9548MultiplexerI2C mux(I2CManager i2CManager) {
        final TCA9548MultiplexerI2C mux = new TCA9548MultiplexerI2C(NerellConstantesI2C.MULTIPLEXEUR_I2C_NAME);
        i2CManager.registerMultiplexerDevice(NerellConstantesI2C.MULTIPLEXEUR_I2C_NAME, mux);
        return mux;
    }

    @Bean
    public CarreFouilleReader carreFouilleReader(I2CManager i2CManager) {
        return new CarreFouilleReader(i2CManager, NerellConstantesI2C.CARRE_FOUILLE_READER_NAME);
    }

    @Bean
    @SneakyThrows
    public PCA9685GpioProvider pca9685GpioControler(I2CBus bus) {
        final PCA9685GpioProvider gpioProvider = new PCA9685GpioProvider(bus, NerellConstantesI2C.PCA9685_ADDRESS, new BigDecimal(200));

        final GpioController gpio = GpioFactory.getInstance();
        // Moteur Gauche
        gpio.provisionPwmOutputPin(gpioProvider, PCA9685Pin.PWM_04); // PWM
        gpio.provisionPwmOutputPin(gpioProvider, PCA9685Pin.PWM_05); // Direction

        // Moteur Droit
        gpio.provisionPwmOutputPin(gpioProvider, PCA9685Pin.PWM_06); // PWM
        gpio.provisionPwmOutputPin(gpioProvider, PCA9685Pin.PWM_07); // Direction

        return gpioProvider;
    }

    @Bean
    public AbstractPropulsionsMotors motors() {
        // Configuration de la carte moteur propulsion.
        final PropulsionsPCA9685Motors motors = new PropulsionsPCA9685Motors(PCA9685Pin.PWM_06, PCA9685Pin.PWM_07, PCA9685Pin.PWM_04, PCA9685Pin.PWM_05);
        motors.assignMotors(NerellConstantesConfig.numeroMoteurGauche, NerellConstantesConfig.numeroMoteurDroit);
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

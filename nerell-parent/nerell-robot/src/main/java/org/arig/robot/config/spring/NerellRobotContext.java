package org.arig.robot.config.spring;

import com.pi4j.gpio.extension.pca.PCA9685GpioProvider;
import com.pi4j.gpio.extension.pca.PCA9685Pin;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import com.pi4j.io.i2c.I2CFactory.UnsupportedBusNumberException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.communication.i2c.I2CManager;
import org.arig.robot.communication.i2c.I2CManagerDevice;
import org.arig.robot.communication.raspi.RaspiI2CManager;
import org.arig.robot.constants.NerellConstantesConfig;
import org.arig.robot.constants.NerellConstantesI2C;
import org.arig.robot.model.RobotName;
import org.arig.robot.model.RobotName.RobotIdentification;
import org.arig.robot.model.balise.BaliseData;
import org.arig.robot.services.NerellRobotServosService;
import org.arig.robot.system.avoiding.AvoidingService;
import org.arig.robot.system.avoiding.BasicAvoidingService;
import org.arig.robot.system.avoiding.BasicRetryAvoidingService;
import org.arig.robot.system.avoiding.CompleteAvoidingService;
import org.arig.robot.system.avoiding.SemiCompleteAvoidingService;
import org.arig.robot.system.capteurs.VisionBaliseOverSocket;
import org.arig.robot.system.capteurs.i2c.ARIG2ChannelsAlimentationSensor;
import org.arig.robot.system.capteurs.i2c.GP2D12Telemeter;
import org.arig.robot.system.capteurs.i2c.I2CAdcAnalogInput;
import org.arig.robot.system.capteurs.i2c.TCA9548MultiplexerI2C;
import org.arig.robot.system.capteurs.socket.ILidarTelemeter;
import org.arig.robot.system.capteurs.socket.IVisionBalise;
import org.arig.robot.system.capteurs.socket.RPLidarA2TelemeterOverSocket;
import org.arig.robot.system.encoders.i2c.ARIGI2C2WheelsEncoders;
import org.arig.robot.system.motors.AbstractPropulsionsMotors;
import org.arig.robot.system.motors.PCA9685ToTB6612Motor;
import org.arig.robot.system.motors.PropulsionsPCA9685SimpleMotors;
import org.arig.robot.system.process.RPLidarBridgeProcess;
import org.arig.robot.system.servos.i2c.SD21Servos;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Configuration
public class NerellRobotContext {

    @Bean
    public RobotName robotName() {
        return RobotName.builder()
                .id(RobotIdentification.NERELL)
                .name("Nerell (The less I do the better I am)")
                .version("2024 (Terraforming Mars)")
                .build();
    }

    @Bean(destroyMethod = "close")
    public I2CBus i2cBus() throws IOException, UnsupportedBusNumberException {
        return I2CFactory.getInstance(I2CBus.BUS_1);
    }

    @Bean
    public I2CManager i2cManager(I2CBus i2cBus) throws IOException {
        final RaspiI2CManager manager = new RaspiI2CManager();

        final I2CManagerDevice<I2CDevice> mux = I2CManagerDevice.<I2CDevice>builder()
                .deviceName(NerellConstantesI2C.MULTIPLEXEUR_I2C_NAME)
                .device(i2cBus.getDevice(NerellConstantesI2C.MULTIPLEXEUR_I2C_ADDRESS))
                .build();
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
        final I2CManagerDevice<I2CDevice> pcf3 = I2CManagerDevice.<I2CDevice>builder()
                .deviceName(NerellConstantesI2C.PCF3_DEVICE_NAME)
                .device(i2cBus.getDevice(NerellConstantesI2C.PCF3_ADDRESS))
                .build();
        final I2CManagerDevice<I2CDevice> sd21Avant = I2CManagerDevice.<I2CDevice>builder()
                .deviceName(NerellConstantesI2C.SERVO_AVANT_DEVICE_NAME)
                .device(i2cBus.getDevice(NerellConstantesI2C.SD21_ADDRESS))
                .multiplexerDeviceName(NerellConstantesI2C.MULTIPLEXEUR_I2C_NAME)
                .multiplexerChannel(NerellConstantesI2C.SERVO_AVANT_MUX_CHANNEL)
                .build();
        final I2CManagerDevice<I2CDevice> sd21Arriere = I2CManagerDevice.<I2CDevice>builder()
                .deviceName(NerellConstantesI2C.SERVO_ARRIERE_DEVICE_NAME)
                .device(i2cBus.getDevice(NerellConstantesI2C.SD21_ADDRESS))
                .multiplexerDeviceName(NerellConstantesI2C.MULTIPLEXEUR_I2C_NAME)
                .multiplexerChannel(NerellConstantesI2C.SERVO_ARRIERE_MUX_CHANNEL)
                .build();
        final I2CManagerDevice<I2CDevice> codeurMoteurDroit = I2CManagerDevice.<I2CDevice>builder()
                .deviceName(NerellConstantesI2C.CODEUR_MOTEUR_DROIT)
                .device(i2cBus.getDevice(NerellConstantesI2C.CODEUR_DROIT_ADDRESS))
                .build();
        final I2CManagerDevice<I2CDevice> codeurMoteurGauche = I2CManagerDevice.<I2CDevice>builder()
                .deviceName(NerellConstantesI2C.CODEUR_MOTEUR_GAUCHE)
                .device(i2cBus.getDevice(NerellConstantesI2C.CODEUR_GAUCHE_ADDRESS))
                .build();
        final I2CManagerDevice<I2CDevice> alimMesure = I2CManagerDevice.<I2CDevice>builder()
                .deviceName(NerellConstantesI2C.ALIM_MESURE_DEVICE_NAME)
                .device(i2cBus.getDevice(NerellConstantesI2C.ALIM_MESURE_ADDRESS))
                .scanCmd(new byte[]{0x00})
                .build();
        final I2CManagerDevice<I2CDevice> adc = I2CManagerDevice.<I2CDevice>builder()
                .deviceName(NerellConstantesI2C.I2C_ADC_DEVICE_NAME)
                .device(i2cBus.getDevice(NerellConstantesI2C.I2C_ADC_ADDRESS))
                .build();

        manager.registerDevice(mux);
        manager.registerDevice(sd21Avant);
        manager.registerDevice(sd21Arriere);
        manager.registerDevice(codeurMoteurDroit);
        manager.registerDevice(codeurMoteurGauche);
        manager.registerDevice(pcfAlim);
        manager.registerDevice(pcf1);
        manager.registerDevice(pcf2);
        manager.registerDevice(pcf3);
        manager.registerDevice(pca9685);
        manager.registerDevice(alimMesure);
        manager.registerDevice(adc);

        return manager;
    }

    @Bean
    public SD21Servos servosAvant() {
        return new SD21Servos(NerellConstantesI2C.SERVO_AVANT_DEVICE_NAME);
    }

    @Bean
    public SD21Servos servosArriere() {
        return new SD21Servos(NerellConstantesI2C.SERVO_ARRIERE_DEVICE_NAME);
    }

    @Bean
    public NerellRobotServosService servosService(SD21Servos servosAvant, SD21Servos servosArriere) {
        return new NerellRobotServosService(servosAvant, servosArriere);
    }

    @Bean
    public ARIGI2C2WheelsEncoders encoders() {
        final ARIGI2C2WheelsEncoders encoders = new ARIGI2C2WheelsEncoders(NerellConstantesI2C.CODEUR_MOTEUR_GAUCHE, NerellConstantesI2C.CODEUR_MOTEUR_DROIT);
        encoders.setCoefs(NerellConstantesConfig.coefCodeurGauche, NerellConstantesConfig.coefCodeurDroit);
        return encoders;
    }

    @Bean
    public ARIG2ChannelsAlimentationSensor alimentationSensor() {
        return new ARIG2ChannelsAlimentationSensor(NerellConstantesI2C.ALIM_MESURE_DEVICE_NAME);
    }

    @Bean
    public TCA9548MultiplexerI2C mux(I2CManager i2CManager) {
        final TCA9548MultiplexerI2C mux = new TCA9548MultiplexerI2C(NerellConstantesI2C.MULTIPLEXEUR_I2C_NAME);
        i2CManager.registerMultiplexerDevice(NerellConstantesI2C.MULTIPLEXEUR_I2C_NAME, mux);
        return mux;
    }

    @Bean
    @SneakyThrows
    public PCA9685GpioProvider pca9685GpioControler(I2CBus bus) {
        PCA9685GpioProvider pca9685 = new PCA9685GpioProvider(bus, NerellConstantesI2C.PCA9685_ADDRESS, new BigDecimal(200));

        final GpioController gpio = GpioFactory.getInstance();
        for (Pin pin : PCA9685Pin.ALL) {
            gpio.provisionPwmOutputPin(pca9685, pin);
        }
        return pca9685;
    }

    @Bean
    public AbstractPropulsionsMotors motors() {
        // Configuration de la carte moteur propulsion.
        final PropulsionsPCA9685SimpleMotors motors = new PropulsionsPCA9685SimpleMotors(PCA9685Pin.PWM_02, PCA9685Pin.PWM_03, PCA9685Pin.PWM_06, PCA9685Pin.PWM_07);
        motors.assignMotors(NerellConstantesConfig.numeroMoteurGauche, NerellConstantesConfig.numeroMoteurDroit);
        return motors;
    }

    @Bean
    public PCA9685ToTB6612Motor solarWheelMotor() {
        return new PCA9685ToTB6612Motor(PCA9685Pin.PWM_08, PCA9685Pin.PWM_09, PCA9685Pin.PWM_10);
    }

    @Bean
    public RPLidarBridgeProcess rplidarBridgeProcess() {
        return new RPLidarBridgeProcess("/home/pi/rplidar_bridge");
    }

    @Bean("rplidar")
    @DependsOn("rplidarBridgeProcess")
    public ILidarTelemeter rplidar() throws Exception {
        final File socketFile = new File(RPLidarBridgeProcess.socketPath);
        return new RPLidarA2TelemeterOverSocket(socketFile);
    }

    @Bean
    public I2CAdcAnalogInput analogReader() {
        return new I2CAdcAnalogInput(NerellConstantesI2C.I2C_ADC_DEVICE_NAME);
    }

    @Bean("gp2d")
    public ILidarTelemeter gp2d12Telemeter() {
        List<GP2D12Telemeter.Device> devices = new ArrayList<>();
        devices.add(new GP2D12Telemeter.Device((byte) 1, -80, 70, 160));
        devices.add(new GP2D12Telemeter.Device((byte) 5, -90, 39, 177));
        devices.add(new GP2D12Telemeter.Device((byte) 4, -90, -39, -177));
        devices.add(new GP2D12Telemeter.Device((byte) 0, -80, -70, -160));
        return new GP2D12Telemeter(devices, NerellConstantesConfig.pathFindingTailleObstaclePami);
    }

    @Bean
    public IVisionBalise<BaliseData> visionBalise(Environment env) {
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

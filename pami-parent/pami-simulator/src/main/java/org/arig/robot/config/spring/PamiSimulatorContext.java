package org.arig.robot.config.spring;

import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.arig.robot.communication.bouchon.BouchonI2CManager;
import org.arig.robot.communication.i2c.I2CManager;
import org.arig.robot.communication.i2c.I2CManagerDevice;
import org.arig.robot.exception.I2CException;
import org.arig.robot.model.RobotName;
import org.arig.robot.model.RobotName.RobotIdentification;
import org.arig.robot.model.balise.BaliseData;
import org.arig.robot.model.bouchon.BouchonEncoderValues;
import org.arig.robot.model.bouchon.BouchonI2CDevice;
import org.arig.robot.system.avoiding.AvoidingService;
import org.arig.robot.system.avoiding.AvoidingServiceBouchon;
import org.arig.robot.system.capteurs.ARIG2ChannelsAlimentationSensorBouchon;
import org.arig.robot.system.capteurs.LidarTelemeterBouchon;
import org.arig.robot.system.capteurs.VisionBaliseBouchon;
import org.arig.robot.system.capteurs.i2c.IAlimentationSensor;
import org.arig.robot.system.capteurs.socket.ILidarTelemeter;
import org.arig.robot.system.capteurs.socket.IVisionBalise;
import org.arig.robot.system.capteurs.socket.LD19LidarTelemeterOverSocket;
import org.arig.robot.system.encoders.BouchonARIG2WheelsEncoders;
import org.arig.robot.system.encoders.i2c.ARIGI2C2WheelsEncoders;
import org.arig.robot.system.leds.ARIG2025IoPamiLeds;
import org.arig.robot.system.motors.AbstractPropulsionsMotors;
import org.arig.robot.system.motors.BouchonPropulsionsMotors;
import org.arig.robot.system.process.EcranProcess;
import org.arig.robot.system.servos.AbstractServos;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class PamiSimulatorContext {

    private static final int PCA9685_OFFSET = 0;
    private static final int PCA9685_MAX = 4095;
    private static final int PCA9685_MIN = -PCA9685_MAX;

    @Bean
    public RobotName robotName() {
        return RobotName.builder()
                .id(RobotName.fromPamiID())
                .name("Pami (simulator)")
                .version("latest")
                .build();
    }

    @Bean
    public I2CManager i2cManager() throws I2CException {
        final BouchonI2CManager manager = new BouchonI2CManager();
        BouchonI2CDevice simpleDevice = new BouchonI2CDevice().address(0x12);
        manager.registerDevice(I2CManagerDevice.<BouchonI2CDevice>builder().deviceName("Simple device").device(simpleDevice).build());
        return manager;
    }

    @Bean
    public AbstractServos servos() {
        return new AbstractServos(2) {
            @Override
            public String deviceName() {
                return "";
            }

            @Override
            public void printVersion() throws I2CException {

            }

            @Override
            protected void setPositionImpl(byte servoNb, int position) {

            }

            @Override
            protected void setSpeedImpl(byte servoNb, byte speed) {

            }

            @Override
            protected void setPositionAndSpeedImpl(byte servoNb, int position, byte speed) {

            }
        };
    }

    @Bean
    public ARIG2025IoPamiLeds leds(I2CManager i2cManager) {
        return new ARIG2025IoPamiLeds(i2cManager, "PAMI Leds");
    }

    @Bean
    public AbstractPropulsionsMotors motors() {
        final AbstractPropulsionsMotors motors = new BouchonPropulsionsMotors(PCA9685_OFFSET, PCA9685_MIN, PCA9685_MAX);
        motors.assignMotors(1, 2);
        return motors;
    }

    @Bean
    @SneakyThrows
    public ARIGI2C2WheelsEncoders encoders(ResourcePatternResolver patternResolver) {
        InputStream is = patternResolver.getResource("classpath:pami-propulsions.csv").getInputStream();
        List<String> lines = IOUtils.readLines(is, Charset.defaultCharset());
        List<BouchonEncoderValues> values = lines.parallelStream()
                .filter(l -> !l.startsWith("#"))
                .map(l -> {
                    String[] v = l.split(";");
                    return new BouchonEncoderValues()
                            .vitesseMoteur(Integer.parseInt(v[0]))
                            .gauche(Double.parseDouble(v[1]))
                            .droit(Double.parseDouble(v[2]));
                })
                .collect(Collectors.toList());

        return new BouchonARIG2WheelsEncoders(values);
    }

    @Bean
    public IAlimentationSensor alimentationSensor() {
        return new ARIG2ChannelsAlimentationSensorBouchon("alim sensor");
    }

    @Bean("rplidar")
    public ILidarTelemeter rplidar() {
        return new LidarTelemeterBouchon();
    }

    @Bean
    public IVisionBalise<BaliseData> visionBalise(Environment env) {
        return new VisionBaliseBouchon();
    }

    @Bean
    public AvoidingService avoidingService() {
        return new AvoidingServiceBouchon();
    }

    @Bean
    public EcranProcess ecranProcess(Environment env) {
        final Integer ecranPort = env.getRequiredProperty("ecran.socket.port", Integer.class);
        final String ecranBinary = env.getRequiredProperty("ecran.binary");
        return new EcranProcess(ecranBinary, ecranPort);
    }
}

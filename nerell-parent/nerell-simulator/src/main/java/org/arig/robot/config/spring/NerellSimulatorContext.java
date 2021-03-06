package org.arig.robot.config.spring;

import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.arig.robot.communication.II2CManager;
import org.arig.robot.communication.bouchon.BouchonI2CManager;
import org.arig.robot.constants.IConstantesI2CSimulator;
import org.arig.robot.exception.I2CException;
import org.arig.robot.model.RobotName;
import org.arig.robot.model.bouchon.BouchonEncoderValues;
import org.arig.robot.monitoring.IMonitoringWrapper;
import org.arig.robot.monitoring.MonitoringJsonWrapper;
import org.arig.robot.services.VisionBaliseBouchon;
import org.arig.robot.system.avoiding.AvoidingServiceBouchon;
import org.arig.robot.system.avoiding.IAvoidingService;
import org.arig.robot.system.capteurs.EcranOverSocket;
import org.arig.robot.system.capteurs.IEcran;
import org.arig.robot.system.capteurs.ILidarTelemeter;
import org.arig.robot.system.capteurs.IVisionBalise;
import org.arig.robot.system.capteurs.LidarTelemeterBouchon;
import org.arig.robot.system.capteurs.VisionBaliseOverSocket;
import org.arig.robot.system.encoders.ARIG2WheelsEncoders;
import org.arig.robot.system.encoders.BouchonARIG2WheelsEncoders;
import org.arig.robot.system.motors.AbstractMotor;
import org.arig.robot.system.motors.AbstractPropulsionsMotors;
import org.arig.robot.system.motors.BouchonMotor;
import org.arig.robot.system.motors.BouchonPropulsionsMotors;
import org.arig.robot.system.process.EcranProcess;
import org.arig.robot.system.servos.SD21Servos;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class NerellSimulatorContext {

    private static final int PCA9685_OFFSET = 0;
    private static final int PCA9685_MAX = 4095;
    private static final int PCA9685_MIN = -PCA9685_MAX;

    @Bean
    public RobotName robotName() {
        return new RobotName().name("Nerell (simulator)").version("latest");
    }

    @Bean
    public IMonitoringWrapper monitoringWrapper(Environment env) {
        MonitoringJsonWrapper mjw = new MonitoringJsonWrapper();
        mjw.setEnabled(env.getProperty("monitoring.points.enable", Boolean.class, true));
        return mjw;
    }

    @Bean
    public II2CManager i2cManager() throws I2CException {
        final BouchonI2CManager manager = new BouchonI2CManager();
        manager.registerDevice("DUMMY I2C", 0x00);
        return manager;
    }

    @Bean
    public SD21Servos servos() {
        return new SD21Servos(IConstantesI2CSimulator.SERVO_DEVICE_NAME);
    }

    @Bean
    public AbstractPropulsionsMotors motors() {
        final AbstractPropulsionsMotors motors = new BouchonPropulsionsMotors(PCA9685_OFFSET, PCA9685_MIN, PCA9685_MAX);
        motors.assignMotors(1, 2);
        return motors;
    }

    @Bean
    public AbstractMotor motorPavillon() {
        return new BouchonMotor(PCA9685_OFFSET, PCA9685_MIN, PCA9685_MAX);
    }

    @Bean
    @SneakyThrows
    public ARIG2WheelsEncoders encoders(ResourcePatternResolver patternResolver) {
        InputStream is = patternResolver.getResource("classpath:nerell-propulsions.csv").getInputStream();
        List<String> lines = IOUtils.readLines(is, Charset.defaultCharset());
        List<BouchonEncoderValues> values = lines.parallelStream()
                .filter(l -> !l.startsWith("#"))
                .map(l -> {
                    String [] v = l.split(";");
                    return new BouchonEncoderValues()
                            .vitesseMoteur(Integer.parseInt(v[0]))
                            .gauche(Double.parseDouble(v[1]))
                            .droit(Double.parseDouble(v[2]));
                })
                .collect(Collectors.toList());

        return new BouchonARIG2WheelsEncoders(IConstantesI2CSimulator.CODEUR_MOTEUR_GAUCHE, IConstantesI2CSimulator.CODEUR_MOTEUR_DROIT, values);
    }

    @Bean
    public EcranProcess ecranProcess() {
        return new EcranProcess("/opt/nerell-gui");
    }

    @Bean
    @DependsOn("ecranProcess")
    public IEcran ecran() throws Exception {
        final File socketFile = new File(EcranProcess.socketPath);
        return new EcranOverSocket(socketFile);
    }

    @Bean
    public ILidarTelemeter rplidar() {
        return new LidarTelemeterBouchon();
    }

    @Bean
    public IVisionBalise visionBalise(Environment env) {
        if (StringUtils.equalsIgnoreCase(env.getProperty("balise.impl", String.class, "bouchon"), "bouchon")) {
            return new VisionBaliseBouchon();
        } else {
            final String host = env.getRequiredProperty("balise.socket.host");
            final Integer port = env.getRequiredProperty("balise.socket.port", Integer.class);
            return new VisionBaliseOverSocket(host, port);
        }
    }

    @Bean
    public IAvoidingService avoidingService() {
        return new AvoidingServiceBouchon();
    }
}

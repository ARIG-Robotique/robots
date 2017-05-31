package org.arig.robot.config.spring;

import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.arig.robot.communication.II2CManager;
import org.arig.robot.communication.bouchon.BouchonI2CManager;
import org.arig.robot.constants.IConstantesI2C;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.constants.IConstantesServos;
import org.arig.robot.exception.I2CException;
import org.arig.robot.model.bouchon.BouchonEncoderValues;
import org.arig.robot.system.capteurs.I2CAdcAnalogInput;
import org.arig.robot.system.capteurs.TCS34725ColorSensor;
import org.arig.robot.system.encoders.ARIG2WheelsEncoders;
import org.arig.robot.system.encoders.BouchonARIG2WheelsEncoders;
import org.arig.robot.system.motors.AbstractPropulsionsMotors;
import org.arig.robot.system.motors.SD21Motors;
import org.arig.robot.system.servos.SD21Servos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author gdepuille on 21/12/13.
 */
@Configuration
public class NerellSimulatorI2CContext {

    @Autowired
    private ResourcePatternResolver patternResolver;

    @Bean
    public II2CManager i2cManager() throws I2CException, IOException {
        final BouchonI2CManager manager = new BouchonI2CManager();
        manager.registerDevice("DUMMY I2C", 0x00);
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
    @SneakyThrows
    public ARIG2WheelsEncoders encoders() {
        InputStream is = patternResolver.getResource("classpath:nerell-capture.csv").getInputStream();
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

        return new BouchonARIG2WheelsEncoders(IConstantesI2C.CODEUR_MOTEUR_GAUCHE, IConstantesI2C.CODEUR_MOTEUR_DROIT, values);
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

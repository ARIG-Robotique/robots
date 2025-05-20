package org.arig.robot.config.spring;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import com.pi4j.io.i2c.I2CFactory.UnsupportedBusNumberException;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.communication.i2c.I2CManager;
import org.arig.robot.communication.i2c.I2CManagerDevice;
import org.arig.robot.communication.raspi.RaspiI2CManager;
import org.arig.robot.constants.PamiConstantesConfig;
import org.arig.robot.constants.PamiConstantesI2C;
import org.arig.robot.model.RobotName;
import org.arig.robot.model.RobotName.RobotIdentification;
import org.arig.robot.model.balise.BaliseData;
import org.arig.robot.system.avoiding.AvoidingService;
import org.arig.robot.system.avoiding.BasicAvoidingService;
import org.arig.robot.system.avoiding.BasicRetryAvoidingService;
import org.arig.robot.system.avoiding.CompleteAvoidingService;
import org.arig.robot.system.avoiding.SemiCompleteAvoidingService;
import org.arig.robot.system.capteurs.VisionBaliseOverSocket;
import org.arig.robot.system.capteurs.can.ARIG2024AlimentationController;
import org.arig.robot.system.capteurs.i2c.ARIG2025IoPamiSensors;
import org.arig.robot.system.capteurs.socket.ILidarTelemeter;
import org.arig.robot.system.capteurs.socket.IVisionBalise;
import org.arig.robot.system.capteurs.socket.LD19LidarTelemeterOverSocket;
import org.arig.robot.system.encoders.Abstract2WheelsEncoders;
import org.arig.robot.system.encoders.can.ARIG2024Can2WheelsEncoders;
import org.arig.robot.system.leds.ARIG2025IoPamiLeds;
import org.arig.robot.system.motors.AbstractPropulsionsMotors;
import org.arig.robot.system.motors.can.ARIG2024CanPropulsionsMotors;
import org.arig.robot.system.process.LidarBridgeProcess;
import org.arig.robot.system.servos.i2c.ARIG2025IoPamiServos;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;
import tel.schich.javacan.NetworkDevice;

import java.io.File;
import java.io.IOException;

@Slf4j
@Configuration
public class PamiRobotContext {

  @Bean
  public RobotName robotName() {
    return RobotName.builder()
      .id(RobotName.fromPamiID())
      .name(RobotName.fromPamiID() + " (A huge fan, but it's not a fan)")
      .version("expectedSimulator")
      .build();
  }

  @Bean(destroyMethod = "close")
  public I2CBus i2cBus() throws IOException, UnsupportedBusNumberException {
    return I2CFactory.getInstance(I2CBus.BUS_1);
  }

  @Bean
  public I2CManager i2cManager(I2CBus i2cBus) throws IOException {
    final RaspiI2CManager manager = new RaspiI2CManager();

    final I2CManagerDevice<I2CDevice> pamiIO = I2CManagerDevice.<I2CDevice>builder()
      .deviceName(PamiConstantesI2C.ARIG_2025_PAMI_IO_NAME)
      .device(i2cBus.getDevice(PamiConstantesI2C.ARIG_2025_PAMI_IO_ADDRESS))
      .build();

    manager.registerDevice(pamiIO);

    return manager;
  }

  @Bean
  public ARIG2025IoPamiServos servos() {
    return new ARIG2025IoPamiServos(PamiConstantesI2C.ARIG_2025_PAMI_IO_NAME);
  }

  @Bean
  public ARIG2025IoPamiSensors sensors(I2CManager i2cManager) {
    return new ARIG2025IoPamiSensors(i2cManager, PamiConstantesI2C.ARIG_2025_PAMI_IO_NAME);
  }

  @Bean
  public ARIG2025IoPamiLeds leds(I2CManager i2cManager) {
    return new ARIG2025IoPamiLeds(i2cManager, PamiConstantesI2C.ARIG_2025_PAMI_IO_NAME);
  }

  @Bean
  public Abstract2WheelsEncoders encoders(NetworkDevice canBus, RobotName robotName) throws IOException {
    final ARIG2024Can2WheelsEncoders encoders;

    if (robotName.id() == RobotIdentification.PAMI_TRIANGLE) {
      encoders = new ARIG2024Can2WheelsEncoders(canBus, 1, 2);
      encoders.setEncoderConfiguration(false, true);

    } else if (robotName.id() == RobotIdentification.PAMI_CARRE) {
      encoders = new ARIG2024Can2WheelsEncoders(canBus, 2, 1);
      encoders.setEncoderConfiguration(false, true);

    } else if (robotName.id() == RobotIdentification.PAMI_ROND) {
      encoders = new ARIG2024Can2WheelsEncoders(canBus, 2, 1);
      encoders.setEncoderConfiguration(false, true);

    } else {
      encoders = new ARIG2024Can2WheelsEncoders(canBus, 1, 2);
      encoders.setEncoderConfiguration(false, false);
    }

    encoders.setCoefs(PamiConstantesConfig.coefCodeurGauche, PamiConstantesConfig.coefCodeurDroit);
    return encoders;
  }

  @Bean
  public NetworkDevice canBus() throws IOException {
    return NetworkDevice.lookup("can0");
  }

  @Bean
  public ARIG2024AlimentationController alimentationController(NetworkDevice canBus) throws IOException {
    ARIG2024AlimentationController controller = new ARIG2024AlimentationController(canBus);
    controller.configMonitoring(true, false, false);
    controller.scan();
    return controller;
  }

  @Bean
  public AbstractPropulsionsMotors motors(NetworkDevice canBus, RobotName robotName) throws IOException {
    final ARIG2024CanPropulsionsMotors motors;
    if (robotName.id() == RobotIdentification.PAMI_TRIANGLE) {
      motors = new ARIG2024CanPropulsionsMotors(canBus);
      motors.assignMotors(1, 2);
      motors.setMotorConfiguration(true, false);

    } else if (robotName.id() == RobotIdentification.PAMI_CARRE) {
      motors = new ARIG2024CanPropulsionsMotors(canBus);
      motors.assignMotors(2, 1);
      motors.setMotorConfiguration(true, false);

    } else if (robotName.id() == RobotIdentification.PAMI_ROND) {
      motors = new ARIG2024CanPropulsionsMotors(canBus);
      motors.assignMotors(2, 1);
      motors.setMotorConfiguration(true, false);

    } else {
      motors = new ARIG2024CanPropulsionsMotors(canBus);
      motors.assignMotors(1, 2);
      motors.setMotorConfiguration(false, false);
    }
    return motors;
  }

  @Bean
  public LidarBridgeProcess lidarBridgeProcess() {
    return new LidarBridgeProcess("/home/pi/lidar_bridge", "ldlidar");
  }

  @Bean("lidar")
  @DependsOn("lidarBridgeProcess")
  public ILidarTelemeter lidar() throws Exception {
    final File socketFile = new File(LidarBridgeProcess.socketPath);
    return new LD19LidarTelemeterOverSocket(socketFile);
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

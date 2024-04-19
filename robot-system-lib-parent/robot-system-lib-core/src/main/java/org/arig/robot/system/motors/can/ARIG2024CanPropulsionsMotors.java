package org.arig.robot.system.motors.can;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.arig.robot.communication.can.CANDevice;
import org.arig.robot.system.motors.AbstractPropulsionsMotors;
import tel.schich.javacan.CanChannels;
import tel.schich.javacan.CanFilter;
import tel.schich.javacan.CanFrame;
import tel.schich.javacan.CanSocketOptions;
import tel.schich.javacan.NetworkDevice;
import tel.schich.javacan.RawCanChannel;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Accessors(fluent = true)
public class ARIG2024CanPropulsionsMotors extends AbstractPropulsionsMotors implements CANDevice, AutoCloseable {

  private final RawCanChannel manualChannel;

  @Getter
  private final String deviceName = "ARIG AsservController :: 2 Motors 2024";

  private String version = StringUtils.EMPTY;

  private int speedMoteur1 = 0;
  private int speedMoteur2 = 0;

  @Getter
  @RequiredArgsConstructor
  enum ARIG2024CanPropulsionsMotorsManual {
    SET_MOTOR_CONFIGURATION(11),
    SET_MOTOR_SPEED(14),

    GET_VERSION(16);

    private final int id;
    private static CanFilter[] filters() {
      CanFilter[] filters = new CanFilter[ARIG2024CanPropulsionsMotorsManual.values().length];
      for (ARIG2024CanPropulsionsMotorsManual message : ARIG2024CanPropulsionsMotorsManual.values()) {
        filters[message.ordinal()] = new CanFilter(message.id);
      }
      return filters;
    }
  }

  public ARIG2024CanPropulsionsMotors(final NetworkDevice canDevice) throws IOException {
    super(0);

    this.manualChannel = CanChannels.newRawChannel(canDevice);
    this.manualChannel.setOption(CanSocketOptions.FILTER, ARIG2024CanPropulsionsMotorsManual.filters());
  }

  @Override
  public void close() throws Exception {
    log.info("Close {}", deviceName);

    if (this.manualChannel != null) {
      this.manualChannel.close();
    }
  }

  public void setMotorConfiguration(boolean invertGauche, boolean invertDroit) {
    exceptionAssignationMoteurGauche();
    exceptionAssignationMoteurDroit();

    log.info("Set motor configuration");
    log.info(" * Invert gauche: {}", invertGauche);
    log.info(" * Invert droit: {}", invertDroit);

    byte config = 0;
    if (invertGauche) {
      config += (byte) (numMoteurGauche() == AbstractPropulsionsMotors.MOTOR_1 ? 1 : 2);
    }
    if (invertDroit) {
      config += (byte) (numMoteurDroit() == AbstractPropulsionsMotors.MOTOR_1 ? 1 : 2);
    }

    final CanFrame encoderConfigurationFrame = CanFrame.create(ARIG2024CanPropulsionsMotorsManual.SET_MOTOR_CONFIGURATION.id,
        CanFrame.FD_NO_FLAGS, new byte[]{config});
    try {
      manualChannel.write(encoderConfigurationFrame);
    } catch (IOException e) {
      log.error("Error while sending set encoder configuration request", e);
      throw new RuntimeException(e);
    }
  }

  @Override
  public String version() {
    if (StringUtils.isBlank(version)) {
      CanFrame versionFrame = CanFrame.create(ARIG2024CanPropulsionsMotorsManual.GET_VERSION.id, CanFrame.FD_NO_FLAGS, new byte[]{});
      try {
        manualChannel.write(versionFrame);
        CanFrame response = manualChannel.read();

        byte[] data = new byte[response.getDataLength()];
        response.getData(data, 0, data.length);
        version = new String(data, StandardCharsets.UTF_8);;
      } catch (IOException e) {
        log.error("Error while sending version request", e);
        version = StringUtils.EMPTY;
      }
    }

    return version;
  }

  @Override
  public boolean scan() throws IOException {
    version();
    return StringUtils.isNotBlank(version);
  }

  @Override
  public void init() {
    stopAll();
  }

  @Override
  public void stopAll() {
    generateMouvement(getStopSpeed(), getStopSpeed());
  }

  @Override
  public void generateMouvement(int gauche, int droit) {
    super.generateMouvement(gauche, droit);

    try {
      int absMotor1 = Math.abs(speedMoteur1);
      int absMotor2 = Math.abs(speedMoteur2);

      byte [] data = new byte[5];
      data[0] = (byte) (absMotor1 >> 8);
      data[1] = (byte) (absMotor1 & 0xFF);
      data[2] = (byte) (absMotor2 >> 8);
      data[3] = (byte) (absMotor2 & 0xFF);
      data[4] = (byte) ((speedMoteur1 < 0 ? 1 : 0) | (speedMoteur2 < 0 ? 2 : 0));

      final CanFrame frame = CanFrame.create(ARIG2024CanPropulsionsMotorsManual.SET_MOTOR_SPEED.id,
          CanFrame.FD_NO_FLAGS, data);
      manualChannel.write(frame);
    } catch (IOException e) {
      log.error("Error while sending set motor speed request", e);
    }
  }

  @Override
  public void printVersion() {
    log.info("{} version {}", deviceName(), version());
  }

  @Override
  public void speedMoteur1(int val) {
    speedMoteur1 = val;
  }

  @Override
  public void speedMoteur2(int val) {
    speedMoteur2 = val;
  }
}

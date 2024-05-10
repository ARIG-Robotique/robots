package org.arig.robot.system.encoders.can;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.arig.robot.communication.can.CANDevice;
import org.arig.robot.system.encoders.Abstract2WheelsEncoders;
import org.springframework.util.Assert;
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
public class ARIG2024Can2WheelsEncoders extends Abstract2WheelsEncoders implements CANDevice, AutoCloseable {

  private final RawCanChannel manualChannel;
  private final byte encoderGaucheId;
  private final byte encoderDroitId;

  @Getter
  private final String deviceName = "ARIG AsservController :: 2 Wheels Encoders 2024";

  private String version = StringUtils.EMPTY;
  private double rawEncoder1 = 0;
  private double rawEncoder2 = 0;

  @Getter
  @RequiredArgsConstructor
  enum ARIG2024Can2WheelsEncodersManual {
    SET_ENCODER_CONFIGURATION(12),
    GET_ENCODER(15),
    GET_VERSION(16);

    private final int id;
    private static CanFilter[] filters() {
      CanFilter[] filters = new CanFilter[ARIG2024Can2WheelsEncodersManual.values().length];
      for (ARIG2024Can2WheelsEncodersManual message : ARIG2024Can2WheelsEncodersManual.values()) {
        filters[message.ordinal()] = new CanFilter(message.id);
      }
      return filters;
    }
  }

  public ARIG2024Can2WheelsEncoders(final NetworkDevice canDevice) throws IOException {
    this(canDevice, 1, 2);
  }

  public ARIG2024Can2WheelsEncoders(final NetworkDevice canDevice, int encoderGaucheId, int encoderDroitId) throws IOException {
    super("two_wheels_encoders");

    Assert.isTrue(encoderGaucheId != encoderDroitId, "Encoder id must be different");
    Assert.isTrue(encoderGaucheId == 1 || encoderGaucheId == 2, "Encoder gauche id must be 1 or 2");
    Assert.isTrue(encoderDroitId == 1 || encoderDroitId == 2, "Encoder droit id must be 1 or 2");
    this.encoderGaucheId = (byte) encoderGaucheId;
    this.encoderDroitId = (byte) encoderDroitId;

    this.manualChannel = CanChannels.newRawChannel(canDevice);
    this.manualChannel.setOption(CanSocketOptions.FILTER, ARIG2024Can2WheelsEncodersManual.filters());
  }

  @Override
  public void close() throws Exception {
    log.info("Close {}", deviceName);

    if (this.manualChannel != null) {
      this.manualChannel.close();
    }
  }

  public void setEncoderConfiguration(boolean invertGauche, boolean invertDroit) {
    log.info("Set encoder configuration");
    log.info(" * Invert gauche: {}", invertGauche);
    log.info(" * Invert droit: {}", invertDroit);

    byte config = 0;
    if (invertGauche) {
      config += (byte) (encoderGaucheId == 1 ? 1 : 2);
    }
    if (invertDroit) {
      config += (byte) (encoderDroitId == 1 ? 1 : 2);
    }

    final CanFrame encoderConfigurationFrame = CanFrame.create(ARIG2024Can2WheelsEncodersManual.SET_ENCODER_CONFIGURATION.id,
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
      CanFrame versionFrame = CanFrame.create(ARIG2024Can2WheelsEncodersManual.GET_VERSION.id, CanFrame.FD_NO_FLAGS, new byte[]{});
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
  public void reset() {
    log.info("Reset encoders");
    lectureValeurs();
  }

  @Override
  public void lectureValeurs() {
    log.debug("Read encoders values");

    final CanFrame encoderReadFrame = CanFrame.create(ARIG2024Can2WheelsEncodersManual.GET_ENCODER.id, CanFrame.FD_NO_FLAGS, new byte[]{});
    try {
      manualChannel.write(encoderReadFrame);
      CanFrame response = manualChannel.read();

      byte[] data = new byte[response.getDataLength()];
      response.getData(data, 0, data.length);
      if (response.getDataLength() == 0) {
        log.error("Encoder invalid response length: {}", response.getDataLength());
        rawEncoder1 = rawEncoder2 = 0;
      } else {
        rawEncoder1 = ((short) ((data[0] << 8) + (data[1] & 0xFF)));
        rawEncoder2 = ((short) ((data[2] << 8) + (data[3] & 0xFF)));
      }
      super.lectureValeurs();
    } catch (IOException e) {
      log.error("Error while sending read encoders request", e);
      throw new RuntimeException(e);
    }
  }

  @Override
  protected double lectureGauche() {
    return encoderGaucheId == 1 ? rawEncoder1 : rawEncoder2;
  }

  @Override
  protected double lectureDroit() {
    return encoderDroitId == 1 ? rawEncoder1 : rawEncoder2;
  }
}

package org.arig.robot.system.capteurs.can;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.arig.robot.communication.can.CANDevice;
import org.arig.robot.model.capteurs.AlimentationSensorValue;
import org.arig.robot.model.capteurs.BatterySensorValue;
import org.arig.robot.system.capteurs.i2c.IAlimentationSensor;
import org.arig.robot.system.communication.CANRefreshThread;
import tel.schich.javacan.CanChannels;
import tel.schich.javacan.CanFilter;
import tel.schich.javacan.CanFrame;
import tel.schich.javacan.CanSocketOptions;
import tel.schich.javacan.NetworkDevice;
import tel.schich.javacan.RawCanChannel;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

@Slf4j
@Accessors(fluent = true)
public class ARIG2024AlimentationController implements IAlimentationSensor, CANDevice, AutoCloseable {

  private final RawCanChannel manualChannel;
  private final RawCanChannel fluxChannel;
  private final AlimentationSensorValue[] alimentations = new AlimentationSensorValue[2];
  private final BatterySensorValue battery = new BatterySensorValue();
  private final CANRefreshThread refreshThread;

  @Getter
  private final String deviceName = "ARIG AlimController 2024";

  private Boolean auOK = null;
  private String version = StringUtils.EMPTY;

  @Getter
  @RequiredArgsConstructor
  enum AlimControlerManual {
    SET_CONFIG(1),
    SET_INTERNAL_ALIM(2),
    SET_EXTERNAL_ALIM(3),

    GET_VERSION(5),
    GET_SOUND(10);

    private final int id;
    private static CanFilter[] filters() {
      CanFilter[] filters = new CanFilter[AlimControlerManual.values().length];
      for (AlimControlerManual message : AlimControlerManual.values()) {
        filters[message.ordinal()] = new CanFilter(message.id);
      }
      return filters;
    }
  }

  @Getter
  @RequiredArgsConstructor
  enum AlimControlerFlux {
    GET_AU_STATE(6),
    GET_INTERNAL_ALIM_STATE(7),
    GET_EXTERNAL_ALIM_STATE(8),
    GET_BATTERY_STATE(9);


    private final int id;
    private static CanFilter[] filters() {
      CanFilter[] filters = new CanFilter[AlimControlerFlux.values().length];
      for (AlimControlerFlux message : AlimControlerFlux.values()) {
        filters[message.ordinal()] = new CanFilter(message.id);
      }
      return filters;
    }
  }

  public ARIG2024AlimentationController(final NetworkDevice canDevice) throws IOException {
    this.manualChannel = CanChannels.newRawChannel(canDevice);
    this.manualChannel.setOption(CanSocketOptions.FILTER, AlimControlerManual.filters());

    this.fluxChannel = CanChannels.newRawChannel(canDevice);
    this.fluxChannel.setOption(CanSocketOptions.FILTER, AlimControlerFlux.filters());

    for (int i = 0 ; i < alimentations.length ; i++) {
      alimentations[i] = new AlimentationSensorValue();
    }

    this.refreshThread = new CANRefreshThread(deviceName() + " :: Flux", fluxChannel, fluxDispatcher);
    this.refreshThread.start();
  }

  @Override
  public void close() throws IOException {
    log.info("Close {}", deviceName);
    this.refreshThread.stopThread();

    configMonitoring(false, false, false);

    if (this.manualChannel != null) {
      this.manualChannel.close();
    }
    if (this.fluxChannel != null) {
      this.fluxChannel.close();
    }
  }

  @Override
  public boolean scan() throws IOException {
    version();
    internalRefresh();
    return StringUtils.isNotBlank(version);
  }

  public boolean auOk() {
    return auOK != null && auOK;
  }

  @Override
  public void printVersion() {
    log.info("Version : {}", version());
  }

  public void sound() {
    try {
      CanFrame frame = CanFrame.create(AlimControlerManual.GET_SOUND.id, CanFrame.FD_NO_FLAGS, new byte[]{});
      manualChannel.write(frame);
    } catch (IOException e) {
      log.error("Error while sound request", e);
    }
  }

  public void configMonitoring(boolean monitoredInternalAlim, boolean monitoredExternalAlim, boolean monitoredBattery) {
    try {
      byte config = (byte) (monitoredInternalAlim ? 1 : 0);
      config += (byte) (monitoredExternalAlim ? 2 : 0);
      config += (byte) (monitoredBattery ? 4 : 0);
      CanFrame frame = CanFrame.create(AlimControlerManual.SET_CONFIG.id, CanFrame.FD_NO_FLAGS, new byte[]{config});
      manualChannel.write(frame);
    } catch (IOException e) {
      log.error("Error while sending config request", e);
    }
  }

  public void setInternalAlimentation(boolean enable) {
    try {
      CanFrame frame = CanFrame.create(AlimControlerManual.SET_INTERNAL_ALIM.id, CanFrame.FD_NO_FLAGS, new byte[]{(byte) (enable ? 1 : 0)});
      manualChannel.write(frame);
    } catch (IOException e) {
      log.error("Error while sending set alimentation 2 request", e);
    }
  }

  public void setExternalAlimentation(boolean enable) {
    try {
      CanFrame frame = CanFrame.create(AlimControlerManual.SET_EXTERNAL_ALIM.id, CanFrame.FD_NO_FLAGS, new byte[]{(byte) (enable ? 1 : 0)});
      manualChannel.write(frame);
    } catch (IOException e) {
      log.error("Error while sending set alimentation 3 request", e);
    }
  }

  @Override
  public String version() {
    if (StringUtils.isBlank(version)) {
      CanFrame versionFrame = CanFrame.create(AlimControlerManual.GET_VERSION.id, CanFrame.FD_NO_FLAGS, new byte[]{});
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
  public AlimentationSensorValue get(byte channel) {
    if (channel < 1 || channel > alimentations.length + 1) {
      throw new IllegalArgumentException("Le canal doit être compris entre 1 et " + (alimentations.length));
    }
    return alimentations[channel - 1];
  }

  public BatterySensorValue battery() {
    return battery;
  }

  private void internalRefresh() {
    for (AlimControlerFlux flux : AlimControlerFlux.values()) {
      try {
        CanFrame refreshFrame = CanFrame.create(flux.id, CanFrame.FD_NO_FLAGS, new byte[]{});
        fluxChannel.write(refreshFrame);
      } catch (IOException e) {
        log.error("Error while sending {} request", flux.name(), e);
      }
    }
  }

  private void updateAlimentation(int index, byte[] data) {
    int faultByte = 4;
    // 0-1           : Alim tension
    // 2-3           : Alim current
    // 4 : Alim fault
    double rawTension = ((double) (data[0] << 8)) + (data[1] & 0xff);
    double rawCurrent = ((double) (data[2] << 8)) + (data[3] & 0xff);
    boolean fault = (data[4] & 0x01) == 1;
    alimentations[index].tension(rawTension / 100);
    alimentations[index].current(rawCurrent / 100);
    alimentations[index].fault(fault);
  }

  private final Consumer<CanFrame> fluxDispatcher = frame -> {
    if (frame.getId() == AlimControlerFlux.GET_AU_STATE.id) {
      byte[] data = new byte[frame.getDataLength()];
      frame.getData(data, 0, data.length);
      auOK = data[0] == 1;

    } else if (frame.getId() == AlimControlerFlux.GET_INTERNAL_ALIM_STATE.id) {
      byte[] data = new byte[frame.getDataLength()];
      frame.getData(data, 0, data.length);
      updateAlimentation(0, data);

    } else if (frame.getId() == AlimControlerFlux.GET_EXTERNAL_ALIM_STATE.id) {
      byte[] data = new byte[frame.getDataLength()];
      frame.getData(data, 0, data.length);
      updateAlimentation(1, data);

    } else if (frame.getId() == AlimControlerFlux.GET_BATTERY_STATE.id) {
      byte[] data = new byte[frame.getDataLength()];
      frame.getData(data, 0, data.length);
      log.warn("Battery state not yet implemented");
      battery.percentage(50).voltage(15.32)
          .cell1Percentage(50).cell1Voltage(3.83)
          .cell2Percentage(50).cell2Voltage(3.83)
          .cell3Percentage(50).cell3Voltage(3.83)
          .cell4Percentage(50).cell4Voltage(3.83);
      log.info("Battery : {}", battery.infos());
    }
  };
}

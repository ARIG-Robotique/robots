package org.arig.robot.model.system;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.pi4j.system.SystemInfo;

import java.io.IOException;

/**
 * @author gdepuille on 29/04/15.
 */
public class ClockInfo {

  @JsonGetter
  public long getArmFrequency() throws IOException, InterruptedException {
    return SystemInfo.getClockFrequencyArm();
  }

  @JsonGetter
  public long getCoreFrequency() throws IOException, InterruptedException {
    return SystemInfo.getClockFrequencyCore();
  }

  @JsonGetter
  public long getH264Frequency() throws IOException, InterruptedException {
    return SystemInfo.getClockFrequencyH264();
  }

  @JsonGetter
  public long getISPFrequency() throws IOException, InterruptedException {
    return SystemInfo.getClockFrequencyISP();
  }

  @JsonGetter
  public long getV3DFrequency() throws IOException, InterruptedException {
    return SystemInfo.getClockFrequencyV3D();
  }

  @JsonGetter
  public long getUARTFrequency() throws IOException, InterruptedException {
    return SystemInfo.getClockFrequencyUART();
  }

  @JsonGetter
  public long getPWMFrequency() throws IOException, InterruptedException {
    return SystemInfo.getClockFrequencyPWM();
  }

  @JsonGetter
  public long getEMMCFrequency() throws IOException, InterruptedException {
    return SystemInfo.getClockFrequencyEMMC();
  }

  @JsonGetter
  public long getPixelFrequency() throws IOException, InterruptedException {
    return SystemInfo.getClockFrequencyPixel();
  }

  @JsonGetter
  public long getVECFrequency() throws IOException, InterruptedException {
    return SystemInfo.getClockFrequencyVEC();
  }

  @JsonGetter
  public long getHDMIFrequency() throws IOException, InterruptedException {
    return SystemInfo.getClockFrequencyHDMI();
  }

  @JsonGetter
  public long getDPIFrequency() throws IOException, InterruptedException {
    return SystemInfo.getClockFrequencyDPI();
  }
}

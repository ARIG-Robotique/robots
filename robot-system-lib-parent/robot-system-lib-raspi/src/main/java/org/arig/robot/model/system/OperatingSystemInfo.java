package org.arig.robot.model.system;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.pi4j.system.SystemInfo;

import java.io.IOException;
import java.text.ParseException;

/**
 * @author gdepuille on 29/04/15.
 */
public class OperatingSystemInfo {

  @JsonGetter
  public String getName() {
    return SystemInfo.getOsName();
  }

  @JsonGetter
  public String getVersion() {
    return SystemInfo.getOsVersion();
  }

  @JsonGetter
  public String getArchitecture() {
    return SystemInfo.getOsArch();
  }

  @JsonGetter
  public String getFirmwareBuild() throws IOException, InterruptedException {
    return SystemInfo.getOsFirmwareBuild();
  }

  @JsonGetter
  public String getFirmwareDate() throws IOException, InterruptedException, ParseException {
    return SystemInfo.getOsFirmwareDate();
  }
}

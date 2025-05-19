package org.arig.robot.model.system;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.pi4j.system.SystemInfo;

/**
 * @author gdepuille on 29/04/15.
 */
public class JavaInfo {

  @JsonGetter
  public String getVendor() {
    return SystemInfo.getJavaVendor();
  }

  @JsonGetter
  public String getVendorUrl() {
    return SystemInfo.getJavaVendorUrl();
  }

  @JsonGetter
  public String getVersion() {
    return SystemInfo.getJavaVersion();
  }

  @JsonGetter
  public String getVirtualMachine() {
    return SystemInfo.getJavaVirtualMachine();
  }

  @JsonGetter
  public String getRuntime() {
    return SystemInfo.getJavaRuntime();
  }
}

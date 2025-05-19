package org.arig.robot.model.system;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.pi4j.system.SystemInfo;

import java.io.IOException;

/**
 * @author gdepuille on 29/04/15.
 */
public class CodecInfo {

  @JsonGetter
  public boolean isH264Enabled() throws IOException, InterruptedException {
    return SystemInfo.getCodecH264Enabled();
  }

  @JsonGetter
  public boolean isMPG2Enabled() throws IOException, InterruptedException {
    return SystemInfo.getCodecMPG2Enabled();
  }

  @JsonGetter
  public boolean isWVC1Enabled() throws IOException, InterruptedException {
    return SystemInfo.getCodecWVC1Enabled();
  }
}

package org.arig.robot.communication.socket.balise;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.arig.robot.communication.socket.balise.enums.BaliseMode;
import org.arig.robot.communication.socket.enums.StatusResponse;

import java.io.Serializable;

@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class StatusResponseData implements Serializable {

  private StatusResponse parent;
  private StatusResponse externalRunner;
  private StatusResponse internalRunner;
  private String statusMessage;
  private BaliseMode mode;
  private String team;
  private Boolean idle;

  public boolean isAllOK() {
    return parent == StatusResponse.OK && externalRunner == StatusResponse.OK && internalRunner == StatusResponse.OK;
  }

}

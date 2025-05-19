package org.arig.robot.communication.socket;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.arig.robot.communication.socket.enums.StatusResponse;

/**
 * La meme chose que AbstractResponse mais action n'est pas typé
 */
@Data
public class GenericResponse {

  private String action;
  private StatusResponse status;
  private String errorMessage;
  @JsonInclude(content = JsonInclude.Include.NON_NULL)
  private Object data;

}

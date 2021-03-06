package org.arig.robot.communication.socket;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.arig.robot.communication.socket.enums.StatusResponse;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class AbstractResponse<T extends Enum> {

    private T action;
    private StatusResponse status;
    private String errorMessage;

    public boolean isOk() {
        return StatusResponse.OK.equals(status);
    }

    public boolean isError() {
        return StatusResponse.ERROR.equals(status);
    }

}

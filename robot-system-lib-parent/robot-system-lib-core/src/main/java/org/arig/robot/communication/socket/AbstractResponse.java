package org.arig.robot.communication.socket;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.arig.robot.communication.socket.enums.StatusResponse;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class AbstractResponse<T extends Enum<T>> {

    private T action;
    private StatusResponse status;
    private String errorMessage;

    public AbstractResponse(T action) {
        setAction(action);
    }

    public boolean isOk() {
        return StatusResponse.OK.equals(status);
    }

    public boolean isError() {
        return StatusResponse.ERROR.equals(status);
    }

}

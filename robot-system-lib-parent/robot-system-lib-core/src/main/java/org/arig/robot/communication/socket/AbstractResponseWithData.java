package org.arig.robot.communication.socket;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.arig.robot.communication.socket.enums.StatusResponse;

import java.io.Serializable;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public abstract class AbstractResponseWithData<T extends Enum<T>, D extends Serializable> extends AbstractResponse<T> {

    @JsonInclude(content = JsonInclude.Include.NON_NULL)
    private D data;

    public AbstractResponseWithData(T action, D data) {
        super(action);
        setStatus(StatusResponse.OK);
        setData(data);
    }

}

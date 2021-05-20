package org.arig.robot.communication.socket;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = true)
public abstract class AbstractResponseWithData<T extends Enum, D extends Serializable> extends AbstractResponse<T> {

    @JsonInclude(content = JsonInclude.Include.NON_NULL)
    private D data;

}

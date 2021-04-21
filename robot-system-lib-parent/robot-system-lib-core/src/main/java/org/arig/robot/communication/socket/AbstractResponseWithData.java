package org.arig.robot.communication.socket;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = true)
public abstract class AbstractResponseWithData<T extends Enum, D extends Serializable> extends AbstractResponse<T> {

    private D data;

}

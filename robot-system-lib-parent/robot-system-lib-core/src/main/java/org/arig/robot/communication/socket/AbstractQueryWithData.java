package org.arig.robot.communication.socket;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = true)
public abstract class AbstractQueryWithData<T extends Enum<T>, D extends Serializable> extends AbstractQuery<T> {

    @JsonInclude(content = JsonInclude.Include.NON_NULL)
    private D data;

    protected AbstractQueryWithData(T action) {
        super(action);
    }

    @JsonIgnore
    protected boolean hasData() {
        return data != null;
    }
}

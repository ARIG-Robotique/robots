package org.arig.robot.communication.socket;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = true)
public abstract class AbstractQueryWithData<T extends Enum, D extends Serializable> extends AbstractQuery<T> {

    @JsonInclude(content = JsonInclude.Include.NON_NULL)
    private D data;

    protected AbstractQueryWithData(T action) {
        super(action);
    }

    protected AbstractQueryWithData(T action, D data) {
        super(action);
        setData(data);
    }

    @JsonIgnore
    protected boolean hasData() {
        return data != null;
    }
}

package org.arig.robot.communication.socket;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = true)
public abstract class AbstractQueryWithDatas<T extends Enum, D extends Serializable> extends AbstractQuery<T> {

    @JsonInclude(content = JsonInclude.Include.NON_NULL)
    private D datas;

    protected AbstractQueryWithDatas(T action) {
        super(action);
    }

    @JsonIgnore
    protected boolean hasDatas() {
        return datas != null;
    }
}

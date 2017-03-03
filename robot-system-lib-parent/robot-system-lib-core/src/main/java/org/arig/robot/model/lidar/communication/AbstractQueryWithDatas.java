package org.arig.robot.model.lidar.communication;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.arig.robot.model.lidar.communication.enums.LidarAction;

/**
 * @author gdepuille
 */
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class AbstractQueryWithDatas<D> extends AbstractQuery {

    @JsonInclude(content = JsonInclude.Include.NON_NULL)
    private D datas;

    protected AbstractQueryWithDatas(LidarAction action) {
        super(action);
    }

    @JsonIgnore
    protected boolean hasDatas() {
        return datas != null;
    }
}

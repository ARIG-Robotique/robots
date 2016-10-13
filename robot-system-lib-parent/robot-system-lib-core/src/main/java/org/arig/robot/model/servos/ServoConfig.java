package org.arig.robot.model.servos;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.util.CollectionUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * @author gdepuille on 01/05/15.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class ServoConfig extends ServoInfo {

    private List<ServoPosition> positions;

    public ServoConfig addPosition(ServoPosition pos) {
        if (positions == null) {
            positions = new LinkedList<>();
        }

        positions.add(pos);
        return this;
    }

    public ServoConfig clearPositions() {
        if (!CollectionUtils.isEmpty(positions)) {
            positions.clear();
        }
        return this;
    }

    public ServoInfo toServoInfo() {
        return new ServoInfo().setId(getId()).setName(getName());
    }
}

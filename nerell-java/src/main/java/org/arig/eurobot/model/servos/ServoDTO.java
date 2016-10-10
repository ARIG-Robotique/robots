package org.arig.eurobot.model.servos;

/**
 * Created by gdepuille on 01/05/15.
 */


import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.util.CollectionUtils;

import java.util.LinkedList;
import java.util.List;

@Data
@Accessors(chain = true)
public class ServoDTO {

    private byte id;
    private String name;
    private byte speed;
    private List<ServoPositionDTO> positions;

    public ServoDTO addPosition(ServoPositionDTO pos) {
        if (positions == null) {
            positions = new LinkedList<>();
        }

        positions.add(pos);
        return this;
    }

    public ServoDTO clearPositions() {
        if (!CollectionUtils.isEmpty(positions)) {
            positions.clear();
        }
        return this;
    }
}

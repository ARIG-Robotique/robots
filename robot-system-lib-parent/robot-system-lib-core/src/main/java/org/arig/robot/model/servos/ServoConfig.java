package org.arig.robot.model.servos;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.util.CollectionUtils;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * @author gdepuille on 01/05/15.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class ServoConfig extends ServoInfo {

    private static final int DEFAULT_MIN = 500;
    private static final int DEFAULT_MAX = 2500;
    private static final int OFFSET = 20;

    @Setter(AccessLevel.NONE)
    private int minPosition = DEFAULT_MIN;
    @Setter(AccessLevel.NONE)
    private int maxPosition = DEFAULT_MAX;

    private List<ServoPosition> positions;

    public ServoConfig addPosition(String name, int value) {
        return addPosition(new ServoPosition(name, value));
    }

    public ServoConfig addPosition(ServoPosition pos) {
        if (positions == null) {
            positions = new LinkedList<>();
        }

        positions.add(pos);
        computeMinMax();
        return this;
    }

    public ServoConfig clearPositions() {
        if (!CollectionUtils.isEmpty(positions)) {
            positions.clear();
        }
        computeMinMax();
        return this;
    }

    private void computeMinMax() {
        if (CollectionUtils.isEmpty(positions)) {
            minPosition = DEFAULT_MIN;
            maxPosition = DEFAULT_MAX;
            return;
        }

        Optional<ServoPosition> minPos = positions.stream()
                .min(Comparator.comparingInt(ServoPosition::getValue));
        minPosition = minPos.map(sc -> sc.getValue() - OFFSET).orElse(DEFAULT_MIN);

        Optional<ServoPosition> maxPos = positions.stream()
                .max(Comparator.comparingInt(ServoPosition::getValue));
        maxPosition = maxPos.map(sc -> sc.getValue() + OFFSET).orElse(DEFAULT_MAX);
    }
}

package org.arig.robot.monitoring;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.arig.robot.model.MonitorPoint;

import java.util.ArrayList;
import java.util.List;

/**
 * @author gdepuille on 30/10/16.
 */
@Slf4j
public abstract class AbstractMonitoringWrapper implements IMonitoringWrapper {

    @Getter(AccessLevel.PROTECTED)
    private final List<MonitorPoint> points = new ArrayList<>();

    @Override
    public void clean() {
        log.info("Nettoyage des points de monitoring.");
        points.clear();
    }

    @Override
    public void addPoint(MonitorPoint point) {
        points.add(point);
    }

    protected boolean hasPoints() {
        return CollectionUtils.isNotEmpty(points);
    }
}

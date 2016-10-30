package org.arig.robot.monitoring;

import org.arig.robot.model.MonitorPoint;

/**
 * @author gdepuille on 11/10/16.
 */
public interface IMonitoringWrapper {

    void clean();

    void addPoint(MonitorPoint point);

    void writeToDirectory();
}

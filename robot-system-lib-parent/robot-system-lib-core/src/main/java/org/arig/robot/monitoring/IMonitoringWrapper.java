package org.arig.robot.monitoring;

import org.influxdb.dto.Point;

/**
 * @author gdepuille on 11/10/16.
 */
public interface IMonitoringWrapper {

    void addPoint(Point point);

    void sendToDb();
}

package org.arig.robot.monitoring;

import org.arig.robot.model.monitor.AbstractMonitorMouvement;
import org.arig.robot.model.monitor.MonitorTimeSerie;

/**
 * @author gdepuille on 11/10/16.
 */
public interface IMonitoringWrapper {

    void cleanAllPoints();

    void cleanTimeSeriePoints();

    void cleanMouvementPoints();

    void addTimeSeriePoint(MonitorTimeSerie point);

    void addMouvementPoint(AbstractMonitorMouvement point);

    void save();
}

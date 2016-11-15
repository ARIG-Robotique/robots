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

    void forceAddTimeSeriePoint(MonitorTimeSerie pont);

    void addMouvementPoint(AbstractMonitorMouvement point);

    void forceAddMouvementPoint(AbstractMonitorMouvement point);

    void save();
}

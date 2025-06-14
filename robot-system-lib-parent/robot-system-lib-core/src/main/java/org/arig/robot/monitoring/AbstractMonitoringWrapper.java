package org.arig.robot.monitoring;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.arig.robot.model.AbstractRobotStatus;
import org.arig.robot.model.monitor.AbstractMonitorMouvement;
import org.arig.robot.model.monitor.MonitorTimeSerie;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author gdepuille on 30/10/16.
 */
@Slf4j
public abstract class AbstractMonitoringWrapper implements MonitoringWrapper {

  @Autowired(required = false)
  private AbstractRobotStatus robotStatus = null;

  @Getter(AccessLevel.PUBLIC)
  @Accessors(fluent = true)
  private final List<MonitorTimeSerie> monitorTimeSeriePoints = Collections.synchronizedList(new ArrayList<>());

  @Getter(AccessLevel.PUBLIC)
  @Accessors(fluent = true)
  private final List<AbstractMonitorMouvement> monitorMouvementPoints = Collections.synchronizedList(new ArrayList<>());

  @Setter
  @Getter(AccessLevel.PROTECTED)
  private boolean enabled = true;

  @Override
  public final void save() {
    // Enregistrement
    saveTimeSeriePoints();
    saveMouvementPoints();

    // Nettoyage
    cleanAllPoints();
  }

  protected abstract void saveTimeSeriePoints();

  protected abstract void saveMouvementPoints();

  @Override
  public final void cleanAllPoints() {
    cleanTimeSeriePoints();
    cleanMouvementPoints();
  }

  public final void cleanTimeSeriePoints() {
    log.info("Nettoyage des points de monitoring time serie.");
    monitorTimeSeriePoints.clear();
  }

  public final void cleanMouvementPoints() {
    log.info("Nettoyage des points de monitoring de mouvement.");
    monitorMouvementPoints.clear();
  }

  @Override
  public void addTimeSeriePoint(MonitorTimeSerie point) {
    if (robotStatus == null || robotStatus.forceMonitoring() || robotStatus.matchEnabled()) {
      forceAddTimeSeriePoint(point);
    }
  }

  @Override
  public void forceAddTimeSeriePoint(MonitorTimeSerie point) {
    if (enabled) {
      monitorTimeSeriePoints.add(point);
    }
  }

  @Override
  public void addMouvementPoint(final AbstractMonitorMouvement point) {
    if (robotStatus == null || robotStatus.forceMonitoring() || robotStatus.matchEnabled()) {
      forceAddMouvementPoint(point);
    }
  }

  @Override
  public void forceAddMouvementPoint(AbstractMonitorMouvement point) {
    if (enabled) {
      monitorMouvementPoints.add(point);
    }
  }

  protected boolean hasTimeSeriePoints() {
    return CollectionUtils.isNotEmpty(monitorTimeSeriePoints);
  }

  protected boolean hasMouvementPoints() {
    return CollectionUtils.isNotEmpty(monitorMouvementPoints);
  }
}

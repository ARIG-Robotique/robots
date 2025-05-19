package org.arig.robot.system.encoders;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.monitor.MonitorTimeSerie;
import org.arig.robot.monitoring.MonitoringWrapper;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public abstract class AbstractEncoder {

  @Autowired
  private MonitoringWrapper monitoringWrapper;

  @Getter
  private double value = 0;

  @Setter
  private double coef = 1.0;

  private final String name;

  protected AbstractEncoder(final String name) {
    this.name = name;
  }

  public void lectureValeur() {
    value = lecture() * coef;
    sendMonitoring();
  }

  public abstract void reset();

  protected abstract double lecture();

  private void sendMonitoring() {
    // Construction du monitoring
    MonitorTimeSerie serie = new MonitorTimeSerie()
      .measurementName("encodeurs")
      .addTag(MonitorTimeSerie.TAG_NAME, name)
      .addField("value", getValue());

    monitoringWrapper.addTimeSeriePoint(serie);
  }
}

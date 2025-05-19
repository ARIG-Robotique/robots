package org.arig.robot.filters.pid;

import com.google.common.collect.ImmutableMap;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.arig.robot.filters.common.ProportionalFilter;
import org.arig.robot.model.monitor.MonitorTimeSerie;
import org.arig.robot.monitoring.MonitoringWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
public abstract class AbstractPidFilter implements PidFilter {

  @Autowired
  private MonitoringWrapper monitoringWrapper;

  @Getter
  private final String name;

  @Getter
  private double sampleTimeS;

  @Getter
  @Accessors(fluent = true)
  private Double consigne;

  @Getter(AccessLevel.PROTECTED)
  @Accessors(fluent = true)
  private final ProportionalFilter kp;

  @Getter(AccessLevel.PROTECTED)
  @Accessors(fluent = true)
  private final ProportionalFilter ki;

  @Getter(AccessLevel.PROTECTED)
  @Accessors(fluent = true)
  private final ProportionalFilter integralTime;

  @Getter(AccessLevel.PROTECTED)
  @Accessors(fluent = true)
  private final ProportionalFilter kd;

  @Getter(AccessLevel.PROTECTED)
  @Accessors(fluent = true)
  private final ProportionalFilter derivateTime;

  @Setter(AccessLevel.PROTECTED)
  @Getter(AccessLevel.PROTECTED)
  @Accessors(fluent = true)
  private Double input;

  @Setter(AccessLevel.PROTECTED)
  @Getter(AccessLevel.PROTECTED)
  @Accessors(fluent = true)
  private Double output;

  @Setter(AccessLevel.PROTECTED)
  @Getter(AccessLevel.PROTECTED)
  @Accessors(fluent = true)
  private Double error;

  protected abstract String pidImpl();

  protected abstract Double filterImpl(Double input);

  AbstractPidFilter(final String name) {
    this.name = name;

    kp = new ProportionalFilter(0d);
    ki = new ProportionalFilter(0d);
    integralTime = new ProportionalFilter(1d);
    kd = new ProportionalFilter(0d);
    derivateTime = new ProportionalFilter(1d);
  }

  public void consigne(Double consigne) {
    this.consigne = consigne;
  }

  /**
   * Sets the sample time ms.
   *
   * @param value the new sample time ms
   */
  public void setSampleTimeMs(final double value) {
    sampleTimeS = value / 1000;
    integralTime.setGain(sampleTimeS);
    derivateTime.setGain(1 / sampleTimeS);
  }

  public void setSampleTime(double value, TimeUnit unit) {
    setSampleTimeMs(unit.toMillis((long) value));
  }

  @Override
  public void setTunings(final double kp, final double ki, final double kd) {
    log.info("Configuration des paramètres PID {} ( Kp = {} ; Ki = {} ; Kd = {} )", getName(), kp, ki, kd);

    this.kp.setGain(kp);
    this.ki.setGain(ki);
    this.kd.setGain(kd);
  }

  @Override
  public Map<String, Double> getTunings() {
    return ImmutableMap.of(
      "kp", kp.getGain(),
      "ki", ki.getGain(),
      "kd", kd.getGain()
    );
  }

  @Override
  public void reset() {
    input = 0d;
    output = 0d;
  }

  @Override
  public final Double filter(Double input) {
    Assert.notNull(input, FILTER_VALUE_NULL_MESSAGE);

    this.input = input;
    error = consigne - input;
    output = filterImpl(input);

    // Construction du monitoring
    MonitorTimeSerie serie = new MonitorTimeSerie()
      .measurementName("correcteur_pid")
      .addTag(MonitorTimeSerie.TAG_NAME, name)
      .addTag(MonitorTimeSerie.TAG_IMPLEMENTATION, pidImpl())
      .addField("kp", kp().getGain())
      .addField("ki", ki().getGain())
      .addField("kd", kd().getGain())
      .addField("consigne", consigne)
      .addField("input", input)
      .addField("output", output)
      .addField("error", error)
      .addField("errorSum", getErrorSum());
    customMonitoringFields().forEach(serie::addField);

    monitoringWrapper.addTimeSeriePoint(serie);

    return output();
  }

  @Override
  public Double lastResult() {
    return output();
  }

  protected Map<String, Number> customMonitoringFields() {
    return MapUtils.EMPTY_SORTED_MAP;
  }
}

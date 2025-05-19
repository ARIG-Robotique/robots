package org.arig.robot.filters.ramp;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.filters.common.ProportionalFilter;

import java.util.Map;

@Slf4j
public abstract class AbstractGainFactorRampFilter extends AbstractRampFilter implements GainFactorRampFilter {

  private final ProportionalFilter gain = new ProportionalFilter(1d);

  protected AbstractGainFactorRampFilter(final String name, final double sampleTimeMs,
                                         final double rampAcc, final double rampDec) {
    this(name, sampleTimeMs, rampAcc, rampDec, 1);
  }

  protected AbstractGainFactorRampFilter(final String name, final double sampleTimeMs,
                                         final double rampAcc, final double rampDec, double gain) {
    super(name, sampleTimeMs, rampAcc, rampDec);
    this.gain.setGain(gain);
  }

  @Override
  public void setGain(final double gain) {
    this.gain.setGain(gain);
  }

  @Override
  protected double getRampAcc() {
    return gain.filter(super.getRampAcc());
  }

  @Override
  protected double getRampDec() {
    return gain.filter(super.getRampDec());
  }

  @Override
  protected double getStepVitesseAccel() {
    return gain.filter(super.getStepVitesseAccel());
  }

  @Override
  protected double getStepVitesseDecel() {
    return gain.filter(super.getStepVitesseDecel());
  }

  @Override
  public double getConsigneVitesse() {
    return gain.filter(super.getConsigneVitesse());
  }

  @Override
  protected final Map<String, Number> specificMonitoringFields() {
    Map<String, Number> fields = gainFactorSpecificMonitoringFields();
    fields.put("gain", gain.getGain());
    return fields;
  }

  protected abstract Map<String, Number> gainFactorSpecificMonitoringFields();
}

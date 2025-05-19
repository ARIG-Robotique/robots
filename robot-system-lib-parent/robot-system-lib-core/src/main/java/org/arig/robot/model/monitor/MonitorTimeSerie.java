package org.arig.robot.model.monitor;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author gdepuille on 30/10/16.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MonitorTimeSerie extends AbstractMonitor {

  public static final String TAG_NAME = "name";
  public static final String TAG_IMPLEMENTATION = "implementation";

  @Setter(AccessLevel.PROTECTED)
  private String measurementName;

  private Map<String, Number> fields = new LinkedHashMap<>();
  private Map<String, String> tags = new LinkedHashMap<>();

  public MonitorTimeSerie measurementName(String measurementName) {
    setMeasurementName(measurementName);
    return this;
  }

  public MonitorTimeSerie addField(String name, Number value) {
    fields.put(name, value);
    return this;
  }

  public MonitorTimeSerie addTag(String name, String value) {
    tags.put(name, value);
    return this;
  }
}

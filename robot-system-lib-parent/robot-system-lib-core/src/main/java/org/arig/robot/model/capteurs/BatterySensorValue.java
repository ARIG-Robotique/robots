package org.arig.robot.model.capteurs;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true, chain = true)
public class BatterySensorValue {

  private double voltage;
  private double percentage;

  private double cell1Voltage;
  private double cell1Percentage;

  private double cell2Voltage;
  private double cell2Percentage;

  private double cell3Voltage;
  private double cell3Percentage;

  private double cell4Voltage;
  private double cell4Percentage;

  public boolean warning() {
    return percentage < 30;
  }

  public boolean critical() {
    return percentage < 20;
  }

  public String infos() {
    return String.format("Battery: %.2fV (%.0f%%) - Cell1: %.2fV (%.0f%%) - Cell2: %.2fV (%.0f%%) - Cell3: %.2fV (%.0f%%) - Cell4: %.2fV (%.0f%%)",
      voltage, percentage,
      cell1Voltage, cell1Percentage,
      cell2Voltage, cell2Percentage,
      cell3Voltage, cell3Percentage,
      cell4Voltage, cell4Percentage
    );
  }
}

package org.arig.robot.model.bouchon;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true, chain = true)
public class BouchonEncoderValue {

  private int vitesseMoteur;
  private double value;
}

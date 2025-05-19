package org.arig.robot.model.bras;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AnglesBras {
  public final double a1;
  public final double a2;
  public final double a3;
  public boolean a1Error;
  public boolean a2Error;
  public boolean a3Error;

  public boolean isError() {
    return a1Error || a2Error || a3Error;
  }
}

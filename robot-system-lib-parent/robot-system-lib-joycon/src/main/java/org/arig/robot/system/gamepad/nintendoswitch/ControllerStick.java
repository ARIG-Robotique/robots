package org.arig.robot.system.gamepad.nintendoswitch;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

@NoArgsConstructor
public class ControllerStick {

  @Getter
  private float horizontal = 0.0f;

  @Getter
  private float vertical = 0.0f;

  public void analogStickCalc(int x, int y, int[] xCalc, int[] yCalc) {
    float xF;
    float yF;
    float hori;
    float vert;
    float deadZoneCenter = 0.15f;
    float deadZoneOuter = 0.10f;

    x = Math.max(xCalc[0], Math.min(xCalc[2], x));
    y = Math.max(yCalc[0], Math.min(yCalc[2], y));

    if (x >= xCalc[1]) {
      xF = (float) (x - xCalc[1]) / (float) (xCalc[2] - xCalc[1]);
    } else {
      xF = -((float) (x - xCalc[1]) / (float) (xCalc[0] - xCalc[1]));
    }
    if (y >= yCalc[1]) {
      yF = (float) (y - yCalc[1]) / (float) (yCalc[2] - yCalc[1]);
    } else {
      yF = -((float) (y - yCalc[1]) / (float) (yCalc[0] - yCalc[1]));
    }

    float mag = (float) Math.sqrt(xF * xF + yF * yF);

    if (mag > deadZoneCenter) {
      // scale such that output magnitude is in the range [0.0f, 1.0f]
      float legalRange = 1.0f - deadZoneOuter - deadZoneCenter;
      float normalizedMag = Math.min(1.0f, (mag - deadZoneCenter) / legalRange);
      float scale = normalizedMag / mag;
      hori = xF * scale;
      vert = yF * scale;
    } else {
      // stick is in the inner dead zone
      hori = 0.0f;
      vert = 0.0f;
    }

    BigDecimal bdHori = BigDecimal.valueOf(hori);
    bdHori = bdHori.setScale(2, RoundingMode.HALF_EVEN);
    BigDecimal bdVert = BigDecimal.valueOf(vert);
    bdVert = bdVert.setScale(2, RoundingMode.HALF_EVEN);

    horizontal = bdHori.floatValue();
    vertical = bdVert.floatValue();
  }
}

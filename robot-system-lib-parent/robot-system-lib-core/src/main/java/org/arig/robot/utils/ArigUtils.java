package org.arig.robot.utils;

public enum ArigUtils {
  ;

  public static double lerp(double val, double start_1, double end_1, double start_2, double end_2) {
    double lambda = (val - start_1) / (end_1 - start_1);
    return start_2 + lambda * (end_2 - start_2);
  }
}

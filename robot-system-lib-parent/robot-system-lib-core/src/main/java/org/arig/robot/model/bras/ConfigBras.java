package org.arig.robot.model.bras;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ConfigBras {
  public final boolean back;
  // position de l'axe principal en mm
  public final int x;
  public final int y;
  // rayon de chaque segment en mm
  public final int r1;
  public final int r2;
  public final int r3;
  // angles maximaux en degres
  public final int a1Min;
  public final int a1Max;
  public final int a2Min;
  public final int a2Max;
  public final int a3Min;
  public final int a3Max;
  // préfère la solution qui met le coude au bas
  public final boolean preferA1Min;
}

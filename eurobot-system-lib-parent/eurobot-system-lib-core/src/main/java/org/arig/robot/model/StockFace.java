package org.arig.robot.model;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Accessors(fluent = true)
public class StockFace {

  private boolean pinceGauche;
  private boolean pinceDroite;
  private boolean centreGauche;
  private boolean centreDroite;
  private boolean tiroirHaut;
  private boolean tiroirBas;

  public boolean isEmpty() {
    return !pinceGauche && !pinceDroite && !centreGauche && !centreDroite && !tiroirHaut && !tiroirBas;
  }

  public int nbEtageConstructible() {
    int nbEtage = 0;
    if (tiroirBas && centreGauche && centreDroite) {
      nbEtage = 1;
    }
    if (nbEtage == 1 && tiroirHaut && pinceGauche && pinceDroite) {
      nbEtage = 2;
    }

    return nbEtage;
  }
}

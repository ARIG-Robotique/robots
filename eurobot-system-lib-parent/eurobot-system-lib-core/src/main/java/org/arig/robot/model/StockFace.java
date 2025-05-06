package org.arig.robot.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Accessors(fluent = true)
public class StockFace {

  @JsonProperty
  private boolean pinceGauche;
  @JsonProperty
  private boolean pinceDroite;
  @JsonProperty
  private boolean solGauche;
  @JsonProperty
  private boolean solDroite;
  @JsonProperty
  private boolean tiroirHaut;
  @JsonProperty
  private boolean tiroirBas;

  @JsonIgnore
  public boolean isEmpty() {
    return !pinceGauche && !pinceDroite && !solGauche && !solDroite && !tiroirHaut && !tiroirBas;
  }

  public int nbEtageConstructible() {
    int nbEtage = 0;
    if (tiroirBas && solGauche && solDroite) {
      nbEtage = 1;
    }
    if (nbEtage == 1 && tiroirHaut && pinceGauche && pinceDroite) {
      nbEtage = 2;
    }

    return nbEtage;
  }
}

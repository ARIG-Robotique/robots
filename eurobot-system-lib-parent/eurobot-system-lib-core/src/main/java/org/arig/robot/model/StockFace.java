package org.arig.robot.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
@Accessors(fluent = true, chain = true)
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

  public boolean isInvalid() {
    return nbEtageConstructible() == 0 && !isEmpty();
  }

  public int nbEtageConstructible() {
    if (!tiroirHaut && !tiroirBas) {
      return 0;
    }
    if ((solGauche != solDroite) || (pinceGauche != pinceDroite)) {
      // On a un couple hybride -> Non géré pour le moment
      return 0;
    }

    if (tiroirBas && tiroirHaut && solGauche && solDroite && pinceGauche && pinceDroite) {
      // Collecte complete
      return 2;
    }
    if (tiroirBas && ((solGauche && solDroite) || (pinceGauche && pinceDroite))) {
      // Tiroir bas et au moins un couple de colonne
      return 1;
    }

    // Pour le moment autres couple hybride ne sont pas gérés
    return 0;
  }

  public void clear() {
    pinceGauche = false;
    pinceDroite = false;
    solGauche = false;
    solDroite = false;
    tiroirHaut = false;
    tiroirBas = false;
  }
}

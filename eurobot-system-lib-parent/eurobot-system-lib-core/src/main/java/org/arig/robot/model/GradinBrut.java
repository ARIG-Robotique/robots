package org.arig.robot.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Accessors(fluent = true)
public class GradinBrut extends Point {

  @Getter
  @Accessors(fluent = true)
  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  public enum ID {
    JAUNE_RESERVE,
    JAUNE_HAUT_GAUCHE,
    JAUNE_MILIEU_CENTRE,
    JAUNE_BAS_GAUCHE,
    JAUNE_BAS_CENTRE,

    BLEU_RESERVE,
    BLEU_HAUT_DROITE,
    BLEU_MILIEU_CENTRE,
    BLEU_BAS_DROITE,
    BLEU_BAS_CENTRE
  }

  public enum Orientation {
    VERTICAL, HORIZONTAL
  }

  @JsonProperty("id")
  private final ID id;
  private final boolean bordure;
  @JsonProperty("present")
  private boolean present = true;
  @JsonProperty("bloque")
  private boolean bloque = false;
  @JsonIgnore
  private final Orientation orientation;

  public GradinBrut(ID id, int x, int y, boolean bordure, Orientation orientation) {
    super(x, y);
    this.id = id;
    this.bordure = bordure;
    this.orientation = orientation;
  }

  public void setGradinPris() {
    log.info("[RS] Gradin brut {} pris", id);
    present = false;
  }

  public void setGradinBloque() {
    log.warn("[RS] Gradin brut {} bloqué", id);
    bloque = true;
  }
}

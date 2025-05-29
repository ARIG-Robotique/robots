package org.arig.robot.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

@Slf4j
@Getter
@Setter
@Accessors(fluent = true)
public class ConstructionArea {

  private final String name;
  private final byte nbRang;
  @Getter(AccessLevel.NONE)
  private final byte nbEtage = 3;
  private final byte nbEtageConstructible = 2;
  private final boolean[][] data;

  public ConstructionArea(String name) {
    this(name, (byte) 1);
  }

  public ConstructionArea(String name, byte nbRang) {
    this.name = name;
    this.nbRang = nbRang;
    this.data = new boolean[nbRang][nbEtage];
  }

  public int score() {
    int score = 0;
    for (int row = 0; row < nbRang; row++) {
      for (int col = 0; col < nbEtage; col++) {
        if (data[row][col]) {
          switch (col) {
            case 0 -> score += 4;
            case 1 -> score += 8;
            case 2 -> score += 16;
          }
        }
      }
    }

    return score;
  }

  public void addGradin(Rang rang, Etage etage) {
    addGradin(rang, etage, false);
  }
  public void addGradin(Rang rang, Etage etage, boolean skipLog) {
    if (!skipLog) {
      log.info("{} : Ajout tribune {} {}", name, rang, etage);
    }
    data[rang.idx][etage.idx] = true;
  }

  public void removeGradin(Rang rang, Etage etage) {
    removeGradin(rang, etage, false);
  }
  public void removeGradin(Rang rang, Etage etage, boolean skipLog) {
    if (!skipLog) {
      log.info("{} : Remove tribune {} {}", name, rang, etage);
    }
    data[rang.idx][etage.idx] = false;
    if ((etage.idx + 1) < nbEtage) {
      removeGradin(rang, Etage.fromIdx((byte) (etage.idx + 1)), skipLog);
    }
  }

  public Rang getFirstRangWithElement(int expected) {
    for (byte row = 0; row < nbRang; row++) {
      Rang rang = Rang.fromIdx(row);
      int nbElements = getNbElementsInRang(rang);
      if (nbElements == expected) {
        return rang;
      }
    }
    return null;
  }

  public Rang getFirstConstructibleRang() {
    // Récupération du premier rang non vide
    Rang firstRangNonVide = null;
    boolean firstRangNonVideConstructible = false;
    if (nbRang > 2 && !rangEmpty(Rang.RANG_3, nbEtageConstructible)) {
      firstRangNonVide = Rang.RANG_3;
      firstRangNonVideConstructible = getFirstConstructibleEtage(Rang.RANG_3) != null;
    } else if (nbRang > 1 && !rangEmpty(Rang.RANG_2, nbEtageConstructible)) {
      firstRangNonVide = Rang.RANG_2;
      firstRangNonVideConstructible = getFirstConstructibleEtage(Rang.RANG_2) != null;
    } else if (!rangEmpty(Rang.RANG_1, nbEtageConstructible)) {
      firstRangNonVide = Rang.RANG_1;
      firstRangNonVideConstructible = getFirstConstructibleEtage(Rang.RANG_1) != null;
    }

    Rang firstRangVide = null;
    boolean firstRangVideConstructible = false;
    if (rangEmpty(Rang.RANG_1, nbEtageConstructible)) {
      firstRangVide = Rang.RANG_1;
      firstRangVideConstructible = getFirstConstructibleEtage(Rang.RANG_1) != null;
    } else if (nbRang > 1 && rangEmpty(Rang.RANG_2, nbEtageConstructible)) {
      firstRangVide = Rang.RANG_2;
      firstRangVideConstructible = getFirstConstructibleEtage(Rang.RANG_2) != null;
    } else if (nbRang > 2 && rangEmpty(Rang.RANG_3, nbEtageConstructible)) {
      firstRangVide = Rang.RANG_3;
      firstRangVideConstructible = getFirstConstructibleEtage(Rang.RANG_3) != null;
    }

    if (firstRangNonVide == null && firstRangVide == null) {
      // Aucun rang constructible
      return null;
    }
    if (firstRangVide == null && firstRangNonVideConstructible) {
      // Rang non vide constructible
      return firstRangNonVide;
    }
    if (firstRangNonVide == null && firstRangVideConstructible) {
      // Rang vide constructible
      return firstRangVide;
    }

    // Si les deux rang vide et non vide sont les mêmes -> bizarrerie
    if (firstRangVide == firstRangNonVide && firstRangVideConstructible) {
      return firstRangVide;
    }

    if (firstRangVide != null && firstRangNonVide != null) {
      // Cas nominal
      if (firstRangVide.after(firstRangNonVide)) {
        return firstRangNonVideConstructible ? firstRangNonVide : firstRangVide;
      }

      // Si le premier rang vide est inférieur au premier rang non vide -> pb
      if (firstRangVide.before(firstRangNonVide)) {
        if (firstRangNonVideConstructible) {
          return firstRangNonVide;
        }
      }
    }

    // Aucun rang constructible
    return null;
  }

  public Etage getFirstConstructibleEtage(Rang rang) {
    if (rang != null) {
      for (byte etage = 0; etage < nbEtageConstructible; etage++) {
        if (!data[rang.idx][etage]) {
          return Etage.fromIdx(etage);
        }
      }
    }
    return null;
  }

  public int getNbElementsInRang(Rang rang) {
    if (rang == null) {
      return -1;
    }
    if (data[rang.idx][Etage.ETAGE_3.idx]) {
      return 3;
    }
    if (data[rang.idx][Etage.ETAGE_2.idx]) {
      return 2;
    }
    if (data[rang.idx][Etage.ETAGE_1.idx]) {
      return 1;
    }
    return 0;
  }

  public boolean isEmpty() {
    for (byte row = 0; row < nbRang; row++) {
      for (byte col = 0; col < nbEtage; col++) {
        if (data[row][col]) {
          return false;
        }
      }
    }
    return true;
  }

  public boolean isUnconstructable() {
    for (byte row = 0; row < nbRang; row++) {
      for (byte col = 0; col < nbEtageConstructible; col++) {
        if (!data[row][col]) {
          return false;
        }
      }
    }
    return true;
  }

  @Override
  public String toString() {
    return Arrays.deepToString(data);
  }

  public void clean() {
    for (byte row = 0; row < nbRang; row++) {
      for (byte col = 0; col < nbEtage; col++) {
        data[row][col] = false;
      }
    }
  }

  private boolean rangEmpty(Rang rang, byte nbEtage) {
    for (byte etage = 0; etage < nbEtage; etage++) {
      if (data[rang.idx][etage]) {
        return false;
      }
    }
    return true;
  }

  public ConstructionArea clone() {
    ConstructionArea clone = new ConstructionArea(name + " [CLONE]", nbRang);
    for (byte row = 0; row < nbRang; row++) {
      for (byte col = 0; col < nbEtage; col++) {
        clone.data[row][col] = data[row][col];
      }
    }
    return clone;
  }
}

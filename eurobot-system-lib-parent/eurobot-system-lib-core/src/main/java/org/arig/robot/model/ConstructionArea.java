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
    private final boolean[][] data;

    @Accessors(fluent = true)
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public enum Rang {
        RANG_1((byte) 0),
        RANG_2((byte) 1),
        RANG_3((byte) 2);

        private final byte idx;

        private static Rang fromIdx(byte rang) {
            return switch (rang) {
                case 0 -> RANG_1;
                case 1 -> RANG_2;
                case 2 -> RANG_3;
                default -> throw new IllegalArgumentException("Invalid rang index: " + rang);
            };
        }
    }

    @Accessors(fluent = true)
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public enum Etage {
        ETAGE_1((byte) 0),
        ETAGE_2((byte) 1),
        ETAGE_3((byte) 2);

        private final byte idx;

        private static Etage fromIdx(byte etage) {
            return switch (etage) {
                case 0 -> ETAGE_1;
                case 1 -> ETAGE_2;
                case 2 -> ETAGE_3;
                default -> throw new IllegalArgumentException("Invalid etage index: " + etage);
            };
        }
    }

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
                    switch(col) {
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
        log.info("Ajout tribune rang {} etage {}", rang, etage);
        data[rang.idx][etage.idx] = true;
    }

    public void removeGradin(Rang rang, Etage etage) {
        log.info("Remove tribune rang {} etage {}", rang, etage);
        data[rang.idx][etage.idx] = false;
    }

    public Rang getFirstConstructibleRang(boolean limit2Etage) {
        byte nbEtage = limit2Etage ? 2 : this.nbEtage;

        // Récupération du premier rang non vide
        Rang firstRangNonVide = null;
        boolean firstRangNonVideConstructible = false;
        if (nbRang > 2 && !rangEmpty(Rang.RANG_3, nbEtage)) {
            firstRangNonVide = Rang.RANG_3;
            firstRangNonVideConstructible = getFirstConstructibleEtage(Rang.RANG_3, limit2Etage) != null;
        } else if (nbRang > 1 && !rangEmpty(Rang.RANG_2, nbEtage)) {
            firstRangNonVide = Rang.RANG_2;
            firstRangNonVideConstructible = getFirstConstructibleEtage(Rang.RANG_2, limit2Etage) != null;
        } else if (!rangEmpty(Rang.RANG_1, nbEtage)) {
            firstRangNonVide = Rang.RANG_1;
            firstRangNonVideConstructible = getFirstConstructibleEtage(Rang.RANG_1, limit2Etage) != null;
        }

        Rang firstRangVide = null;
        boolean firstRangVideConstructible = false;
        if (rangEmpty(Rang.RANG_1, nbEtage)) {
            firstRangVide = Rang.RANG_1;
            firstRangVideConstructible = getFirstConstructibleEtage(Rang.RANG_1, limit2Etage) != null;
        } else if (nbRang > 1 && rangEmpty(Rang.RANG_2, nbEtage)) {
            firstRangVide = Rang.RANG_2;
            firstRangVideConstructible = getFirstConstructibleEtage(Rang.RANG_2, limit2Etage) != null;
        } else if (nbRang > 2 && rangEmpty(Rang.RANG_3, nbEtage)) {
            firstRangVide = Rang.RANG_3;
            firstRangVideConstructible = getFirstConstructibleEtage(Rang.RANG_3, limit2Etage) != null;
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
            if (firstRangVide.idx > firstRangNonVide.idx) {
                return firstRangNonVideConstructible ? firstRangNonVide : firstRangVide;
            }

            // Si le premier rang vide est inférieur au premier rang non vide -> pb
            if (firstRangVide.idx < firstRangNonVide.idx) {
                if (firstRangNonVideConstructible) {
                    return firstRangNonVide;
                }
            }
        }

        // Aucun rang constructible
        return null;
    }

    public Etage getFirstConstructibleEtage(Rang rang, boolean limit2Etage) {
        if(rang != null) {
            byte nbEtage = limit2Etage ? 2 : this.nbEtage;
            for (byte etage = 0; etage < nbEtage; etage++) {
                if (!data[rang.idx][etage]) {
                    return Etage.fromIdx(etage);
                }
            }
        }
        return null;
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

    @Override
    public String toString() {
        return Arrays.deepToString(data);
    }

    void clean() {
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
}

package org.arig.robot.model;

import lombok.Getter;
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
    private final byte nbEtage = 3;
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

    public void addTribune(int rang, int etage) {
        checkPosition(rang, etage);
        log.info("[RS] Ajout tribune rang {} etage {}", rang, etage);
        data[rang][etage] = true;
    }

    public void removeTribune(int rang, int etage) {
        checkPosition(rang, etage);
        log.info("[RS] Remove tribune rang {} etage {}", rang, etage);
        data[rang][etage] = false;
    }

    public boolean isEmpty() {
        for (int row = 0; row < nbRang; row++) {
            for (int col = 0; col < nbEtage; col++) {
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

    private void checkPosition(int rang, int etage) {
        if (rang < 0 || rang >= nbRang) {
            throw new IllegalArgumentException("Rang " + rang + " out of bounds");
        }
        if (etage < 0 || etage >= nbEtage) {
            throw new IllegalArgumentException("Etage " + etage + " out of bounds");
        }
    }
}

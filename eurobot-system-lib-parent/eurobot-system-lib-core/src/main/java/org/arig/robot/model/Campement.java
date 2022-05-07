package org.arig.robot.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Campement {

    public static final int MAX_DEPOSE = 5;

    private final List<CouleurEchantillon> rouge = new ArrayList<>(4);
    private final List<CouleurEchantillon> vert = new ArrayList<>(4);
    private final List<CouleurEchantillon> vertTemp = new ArrayList<>(4);
    private final List<CouleurEchantillon> bleu = new ArrayList<>(4);

    public void addRouge(final CouleurEchantillon echantillon) {
        rouge.add(echantillon);
    }

    public void addVert(final CouleurEchantillon echantillon) {
        vert.add(echantillon);
    }

    public void addVertTemp(final CouleurEchantillon echantillon) {
        vertTemp.add(echantillon);
    }

    public void addBleu(final CouleurEchantillon echantillon) {
        bleu.add(echantillon);
    }

    public int sizeRouge() {
        return rouge.size();
    }

    public int sizeVert() {
        return vert.size();
    }

    public int sizeVertTemp() {
        return vertTemp.size();
    }

    public int sizeBleu() {
        return bleu.size();
    }

    public void poussetteVert() {
        vert.addAll(vertTemp);
        vertTemp.clear();
    }

    int score() {
        AtomicInteger points = new AtomicInteger(0);
        rouge.forEach(couleurEchantillon -> points.addAndGet(couleurEchantillon == CouleurEchantillon.ROUGE ? 2 : 1));
        vert.forEach(couleurEchantillon -> points.addAndGet(couleurEchantillon == CouleurEchantillon.VERT ? 2 : 1));
        points.addAndGet(vertTemp.size());
        bleu.forEach(couleurEchantillon -> points.addAndGet(couleurEchantillon == CouleurEchantillon.BLEU ? 2 : 1));
        return points.get();
    }
}

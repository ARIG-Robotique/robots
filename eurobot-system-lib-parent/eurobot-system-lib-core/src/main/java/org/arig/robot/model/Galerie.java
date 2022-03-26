package org.arig.robot.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Galerie {

    private final List<CouleurEchantillon> rouge = new ArrayList<>(2);
    private final List<CouleurEchantillon> rougeVert = new ArrayList<>(2);
    private final List<CouleurEchantillon> vert = new ArrayList<>(1);
    private final List<CouleurEchantillon> vertBleu = new ArrayList<>(2);
    private final List<CouleurEchantillon> bleu = new ArrayList<>(2);

    public void addRouge(final CouleurEchantillon echantillon) {
        rouge.add(echantillon);
    }
    public void addRougeVert(final CouleurEchantillon echantillon) {
        rougeVert.add(echantillon);
    }
    public void addVert(final CouleurEchantillon echantillon) {
        vert.add(echantillon);
    }
    public void addVertBleu(final CouleurEchantillon echantillon) {
        vertBleu.add(echantillon);
    }
    public void addBleu(final CouleurEchantillon echantillon) {
        bleu.add(echantillon);
    }

    int score() {
        AtomicInteger points = new AtomicInteger(0);
        rouge.forEach(couleurEchantillon -> points.addAndGet(couleurEchantillon == CouleurEchantillon.ROUGE ? 6 : 3));
        rougeVert.forEach(couleurEchantillon -> points.addAndGet(couleurEchantillon == CouleurEchantillon.ROUGE || couleurEchantillon == CouleurEchantillon.VERT ? 6 : 3));
        vert.forEach(couleurEchantillon -> points.addAndGet(couleurEchantillon == CouleurEchantillon.VERT ? 6 : 3));
        vertBleu.forEach(couleurEchantillon -> points.addAndGet(couleurEchantillon == CouleurEchantillon.VERT || couleurEchantillon == CouleurEchantillon.BLEU ? 6 : 3));
        bleu.forEach(couleurEchantillon -> points.addAndGet(couleurEchantillon == CouleurEchantillon.BLEU ? 6 : 3));
        return points.get();
    }
}

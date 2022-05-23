package org.arig.robot.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Campement {

    public enum Position {
        NORD,
        SUD
    }

    public static final int MAX_DEPOSE = 5;

    private final List<CouleurEchantillon> rougeVertNord = new ArrayList<>(MAX_DEPOSE);
    private final List<CouleurEchantillon> rougeVertSud = new ArrayList<>(MAX_DEPOSE);
    private final List<CouleurEchantillon> bleuVertNord = new ArrayList<>(MAX_DEPOSE);
    private final List<CouleurEchantillon> bleuVertSud = new ArrayList<>(MAX_DEPOSE);

    public void addRougeVertNord(final CouleurEchantillon echantillon) {
        rougeVertNord.add(echantillon);
    }

    public void addRougeVertSud(final CouleurEchantillon echantillon) {
        rougeVertSud.add(echantillon);
    }

    public void addBleuVertNord(final CouleurEchantillon echantillon) {
        bleuVertNord.add(echantillon);
    }

    public void addBleuVertSud(final CouleurEchantillon echantillon) {
        bleuVertSud.add(echantillon);
    }

    public int sizeRougeVertNord() {
        return rougeVertNord.size();
    }

    public int sizeRougeVertSud() {
        return rougeVertSud.size();
    }

    public int sizeBleuVertNord() {
        return bleuVertNord.size();
    }

    public int sizeBleuVertSud() {
        return bleuVertSud.size();
    }

    public int score() {
        AtomicInteger points = new AtomicInteger(0);
        rougeVertNord.forEach(c -> points.addAndGet(c == CouleurEchantillon.ROUGE || c == CouleurEchantillon.VERT ? 2 : 1));
        rougeVertSud.forEach(c -> points.addAndGet(c == CouleurEchantillon.ROUGE || c == CouleurEchantillon.VERT ? 2 : 1));
        bleuVertNord.forEach(c -> points.addAndGet(c == CouleurEchantillon.BLEU || c == CouleurEchantillon.VERT ? 2 : 1));
        bleuVertSud.forEach(c -> points.addAndGet(c == CouleurEchantillon.BLEU || c == CouleurEchantillon.VERT ? 2 : 1));
        return points.get();
    }
     public Campement clone() {
        Campement c = new Campement();
        c.rougeVertNord.addAll(rougeVertNord);
        c.rougeVertSud.addAll(rougeVertSud);
        c.bleuVertNord.addAll(bleuVertNord);
        c.bleuVertSud.addAll(bleuVertSud);
        return c;
     }
}

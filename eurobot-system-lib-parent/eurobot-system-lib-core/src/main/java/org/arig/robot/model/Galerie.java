package org.arig.robot.model;

import java.util.ArrayList;
import java.util.List;

public class Galerie {

    private final List<CouleurEchantillon> rouge = new ArrayList<>(2);
    private final List<CouleurEchantillon> rougeVert = new ArrayList<>(2);
    private final List<CouleurEchantillon> vert = new ArrayList<>(1);
    private final List<CouleurEchantillon> bleuVert = new ArrayList<>(2);
    private final List<CouleurEchantillon> bleu = new ArrayList<>(2);

    public int score() {
        return 0; // TODO
    }
}

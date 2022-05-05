package org.arig.robot.model;

import lombok.Data;

@Data
public class Echantillon extends Point {
    enum ID {
        SITE_ECHANTILLONS_JAUNE,
        SITE_ECHANTILLONS_VIOLET,
        SITE_FOUILLE_JAUNE,
        SITE_FOUILLE_VIOLET
    }

    private final ID id;
    private final CouleurEchantillon couleur;
    private boolean blocking; // génère un masque sur la pathfinder

    public Echantillon(ID id, CouleurEchantillon couleur, int x, int y, boolean blocking) {
        super(x, y);
        this.id = id;
        this.couleur = couleur;
        this.blocking = blocking;
    }

    public Echantillon(CouleurEchantillon couleur, int x, int y) {
        super(x, y);
        this.id = null;
        this.couleur = couleur;
        this.blocking = false;
    }
}

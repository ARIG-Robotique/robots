package org.arig.robot.model;

public class ZoneDeFouille {

    private CarreeFouille[] carreeFouilles = new CarreeFouille[]{
            new CarreeFouille(1),
            new CarreeFouille(2, CouleurCarreeFouille.JAUNE),
            new CarreeFouille(3),
            new CarreeFouille(4),
            new CarreeFouille(5),
            new CarreeFouille(6),
            new CarreeFouille(7),
            new CarreeFouille(8),
            new CarreeFouille(9, CouleurCarreeFouille.VIOLET),
            new CarreeFouille(10)
    };

    public int score() {
        return 0; // TODO
    }
}

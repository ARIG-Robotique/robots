package org.arig.robot.strategy.actions.active;

import org.arig.robot.model.Bouee;
import org.arig.robot.model.ECouleurBouee;
import org.arig.robot.model.Point;
import org.springframework.stereotype.Component;

@Component
public class BoueeHautFond extends AbstractBouee {

    private static final Bouee BOUEE_BIDON;

    static {
        BOUEE_BIDON = new Bouee(0, ECouleurBouee.INCONNU, new Point(0, 0));
        BOUEE_BIDON.setPrise();
    }

    private Bouee bouee = BOUEE_BIDON;

    public Bouee bouee() {
        return bouee;
    }

    @Override
    public boolean isValid() {
        // isValid est la première méthode appellée par le manager
        // c'est là qu'on choisit quelle bouée traiter
        selectBouee();
        return super.isValid();
    }

    @Override
    public String name() {
        return "Bouee haut fond";
    }

    /**
     * prend la bouée du haut fond la plus proche, qui rentre dans le robot
     */
    private void selectBouee() {
        // FIXME obsolète avec le capteur couleur ?
        final boolean rougeDispo = !io.presenceVentouse1() || !io.presenceVentouse2();
        final boolean vertDispo = !io.presenceVentouse3() || !io.presenceVentouse4();

        bouee = null;
        double dst = 9999;

        for (Bouee newBouee : rs.hautFond()) {
            if (newBouee.couleur() == ECouleurBouee.ROUGE && rougeDispo
                    || newBouee.couleur() == ECouleurBouee.VERT && vertDispo
                    || newBouee.couleur() == ECouleurBouee.INCONNU && (rougeDispo || vertDispo)) {
                double newDst = tableUtils.distance(newBouee.pt());
                if (newDst < dst) {
                    bouee = newBouee;
                    dst = newDst;
                }
            }
        }

        if (bouee == null) {
            bouee = BOUEE_BIDON;
        }
    }

}

package org.arig.robot.model;

import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Objects;

public abstract class Chenaux {

    protected ArrayList<ECouleurBouee> chenalVert = new ArrayList<>();

    protected ArrayList<ECouleurBouee> chenalRouge = new ArrayList<>();

    // L'introspection est trop coûteuse en temps
    protected abstract Chenaux newInstance();

    public boolean chenalVertEmpty() {
        return chenalVert.isEmpty();
    }

    public boolean chenalRougeEmpty() {
        return chenalRouge.isEmpty();
    }

    public int score() {
        long nbBoueeChenalRouge = chenalRouge.stream().filter(Objects::nonNull).count();
        long nbBoueeOkRouge = chenalRouge.stream().filter(ECouleurBouee.isRouge).count();

        long nbBoueeChenalVert = chenalVert.stream().filter(Objects::nonNull).count();
        long nbBoueeOkVert = chenalVert.stream().filter(ECouleurBouee.isVert).count();

        long pair = Math.min(nbBoueeOkRouge, nbBoueeOkVert);
        return (int) (nbBoueeChenalVert + nbBoueeChenalRouge + nbBoueeOkRouge + nbBoueeOkVert + (pair * 2));
    }

    public void addRouge(ECouleurBouee... bouees) {
        for (ECouleurBouee bouee : bouees) {
            if (bouee != null) {
                chenalRouge.add(bouee);
            }
        }
    }

    public void addVert(ECouleurBouee... bouees) {
        for (ECouleurBouee bouee : bouees) {
            if (bouee != null) {
                chenalVert.add(bouee);
            }
        }
    }

    public Chenaux with(ECouleurBouee[] newChenalRouge, ECouleurBouee[] newChenalVert) {
        Chenaux copy = newInstance();
        copy.chenalRouge.addAll(chenalRouge);
        if (newChenalRouge != null) {
            CollectionUtils.addAll(copy.chenalRouge, newChenalRouge);
        }
        copy.chenalVert.addAll(chenalVert);
        if (newChenalVert != null) {
            CollectionUtils.addAll(copy.chenalVert, newChenalVert);
        }
        return copy;
    }
}

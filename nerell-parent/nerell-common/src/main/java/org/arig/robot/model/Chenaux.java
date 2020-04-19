package org.arig.robot.model;

import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Objects;

public class Chenaux {

    private ArrayList<ECouleurBouee> chenalVert = new ArrayList<>();

    private ArrayList<ECouleurBouee> chenalRouge = new ArrayList<>();

    public int score() {
        long nbBoueeChenalRouge = chenalRouge.stream().filter(Objects::nonNull).count();
        long nbBoueeOkRouge = chenalRouge.stream().filter(ECouleurBouee.isRouge).count();

        long nbBoueeChenalVert = chenalVert.stream().filter(Objects::nonNull).count();
        long nbBoueeOkVert = chenalVert.stream().filter(ECouleurBouee.isVert).count();

        long pair = Math.min(nbBoueeOkRouge, nbBoueeOkVert);
        return (int) (nbBoueeChenalVert + nbBoueeChenalRouge + nbBoueeOkRouge + nbBoueeOkVert + (pair * 2));
    }

    public void addRouge(ECouleurBouee... bouees) {
        CollectionUtils.addAll(chenalRouge, bouees);
    }

    public void addVert(ECouleurBouee... bouees) {
        CollectionUtils.addAll(chenalVert, bouees);
    }

    public Chenaux with(ECouleurBouee[] newChenalRouge, ECouleurBouee[] newChenalVert) {
        Chenaux copy = new Chenaux();
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

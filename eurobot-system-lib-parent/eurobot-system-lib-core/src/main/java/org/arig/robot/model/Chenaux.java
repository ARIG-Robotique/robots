package org.arig.robot.model;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.arig.robot.utils.ArigCollectionUtils;

import java.util.ArrayList;
import java.util.Objects;

@ToString()
@EqualsAndHashCode()
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
        ArigCollectionUtils.addAllIgnoreNull(chenalRouge, bouees);
    }

    public void addVert(ECouleurBouee... bouees) {
        ArigCollectionUtils.addAllIgnoreNull(chenalVert, bouees);
    }

    public Chenaux copy() {
        Chenaux copy = newInstance();
        copy.chenalRouge.addAll(chenalRouge);
        copy.chenalVert.addAll(chenalVert);
        return copy;
    }
}

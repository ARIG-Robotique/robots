package org.arig.robot.model;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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

    public Chenaux copy() {
        Chenaux copy = newInstance();
        copy.chenalRouge.addAll(chenalRouge);
        copy.chenalVert.addAll(chenalVert);
        return copy;
    }

    public void writeStatus(ObjectOutputStream os) throws IOException {
        os.writeByte(chenalVert.size());
        for (ECouleurBouee bouee : chenalVert) {
            os.writeByte(bouee.ordinal());
        }

        os.writeByte(chenalRouge.size());
        for (ECouleurBouee bouee : chenalRouge) {
            os.writeByte(bouee.ordinal());
        }
    }

    public void readStatus(ObjectInputStream is) throws IOException {
        chenalVert.clear();
        byte nbVert = is.readByte();
        for (byte i = 0; i < nbVert; i++) {
            chenalVert.add(ECouleurBouee.values()[is.readByte()]);
        }

        chenalRouge.clear();
        byte nbRouge = is.readByte();
        for (byte i = 0; i < nbRouge; i++) {
            chenalRouge.add(ECouleurBouee.values()[is.readByte()]);
        }
    }
}

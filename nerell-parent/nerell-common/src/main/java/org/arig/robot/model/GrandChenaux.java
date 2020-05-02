package org.arig.robot.model;

public class GrandChenaux extends Chenaux {

    @Override
    protected Chenaux newInstance() {
        return new GrandChenaux();
    }

    public boolean deposeArriereImpossible() {
        return chenalRouge.size() >= 5 && chenalVert.size() >= 5;
    }
}

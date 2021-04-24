package org.arig.robot.model;

public class PetitChenaux extends Chenaux {
    @Override
    protected Chenaux newInstance() {
        return new PetitChenaux();
    }
}

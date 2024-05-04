package org.arig.robot.services;

public interface NerellIOService extends CommonRobotIOService {

    @Override
    default boolean puissanceServosOk() {
        return true;
    }

    @Override
    default boolean puissanceMoteursOk() {
        return true;
    }

    boolean inductifGaucheAverage();

    boolean inductifDroitAverage();

    boolean pinceAvantGaucheAverage(boolean expectedSimulateur);

    boolean pinceAvantCentreAverage(boolean expectedSimulateur);

    boolean pinceAvantDroiteAverage(boolean expectedSimulateur);

    boolean pinceArriereGaucheAverage(boolean expectedSimulateur);

    boolean pinceArriereCentreAverage(boolean expectedSimulateur);

    boolean pinceArriereDroiteAverage(boolean expectedSimulateur);
}

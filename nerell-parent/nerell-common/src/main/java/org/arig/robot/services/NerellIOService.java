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

    boolean pinceAvantGaucheAverage();

    boolean pinceAvantCentreAverage();

    boolean pinceAvantDroiteAverage();

    boolean pinceArriereGaucheAverage();

    boolean pinceArriereCentreAverage();

    boolean pinceArriereDroiteAverage();
}

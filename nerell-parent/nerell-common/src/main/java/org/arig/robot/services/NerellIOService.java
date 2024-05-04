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

}

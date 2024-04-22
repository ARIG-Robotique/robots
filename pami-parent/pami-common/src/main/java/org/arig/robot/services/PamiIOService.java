package org.arig.robot.services;

public interface PamiIOService extends CommonPamiIOService {

  @Override
  default boolean puissanceServosOk() {
    return true;
  }

  @Override
  default boolean puissanceMoteursOk() {
    return true;
  }

}

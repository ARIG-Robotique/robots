package org.arig.robot.services;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

public abstract class AbstractIOServiceBouchon implements IOService {

  @Setter
  @Accessors(fluent = true)
  private boolean au = true;

  @Setter
  @Accessors(fluent = true)
  private boolean tirette = false;

  @Getter
  @Accessors(fluent = true)
  private boolean alimServos = false;

  @Getter
  @Accessors(fluent = true)
  private boolean alimMoteurs = false;

  // --------------------------------------------------------- //
  // --------------------- INFOS TECHNIQUE ------------------- //
  // --------------------------------------------------------- //

  @Override
  public void refreshAllIO() {
  }

  @Override
  public boolean auOk() {
    return au;
  }

  @Override
  public boolean tirette() {
    return tirette;
  }

  // --------------------------------------------------------- //
  // -------------------------- INPUT ------------------------ //
  // --------------------------------------------------------- //

  @Override
  public boolean puissanceServosOk() {
    return alimServos;
  }

  @Override
  public boolean puissanceMoteursOk() {
    return alimMoteurs;
  }

  // --------------------------------------------------------- //
  // -------------------------- OUTPUT ----------------------- //
  // --------------------------------------------------------- //

  @Override
  public void enableAlimServos() {
    alimServos = true;
  }

  @Override
  public void disableAlimServos() {
    alimServos = false;
  }

  @Override
  public void enableAlimMoteurs() {
    alimMoteurs = true;
  }

  @Override
  public void disableAlimMoteurs() {
    alimMoteurs = false;
  }
}

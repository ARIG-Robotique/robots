package org.arig.robot.system.vacuum;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BouchonARIGVacuumController extends AbstractARIGVacuumController {

  @Override
  public void readAllValues() {
  }

  @Override
  public void readData(byte pompeNb) {
  }

  @Override
  public void printVersion() {
  }

  @Override
  protected void sendToController() {
  }

  @Override
  protected byte[] readFromController(byte register, int size) {
    return new byte[0];
  }

}

package org.arig.robot.system.gamepad.nintendoswitch;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@Data
@RequiredArgsConstructor
public class ControllerEvent {
  private final Map<ControllerButton, Boolean> newInputs;
  private final float horizontal;
  private final float vertical;
  private final byte battery;

  static ControllerEvent fromController(Controller j) {
    return new ControllerEvent(j.inputs(), j.horizontal(), j.vertical(), j.battery());
  }
}

package org.arig.robot.system.gamepad.nintendoswitch.joycon;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.arig.robot.system.gamepad.nintendoswitch.ControllerButton;
import org.arig.robot.system.gamepad.nintendoswitch.ControllerConstants;
import org.arig.robot.system.gamepad.nintendoswitch.ControllerEventListener;
import org.arig.robot.system.gamepad.nintendoswitch.ControllerStick;
import org.arig.robot.system.gamepad.nintendoswitch.NintendoSwitchHID;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Accessors(fluent = true)
public class JoyConRight extends JoyCon {

  private final ControllerEventListener eventListener;
  private int lastShared;
  private int lastRight;
  @Getter(AccessLevel.PROTECTED)
  private Map<ControllerButton, Boolean> inputs;
  private Map<ControllerButton, Boolean> oldInputs;
  @Getter(AccessLevel.PROTECTED)
  private byte battery;
  private ControllerStick stick;
  private int[] stickCalXRight;
  private int[] stickCalYRight;

  private static final List<ControllerButton> sharedButtons = new ArrayList<>();
  private static final List<ControllerButton> rightButtons = new ArrayList<>();

  static {
    // Shared register
    sharedButtons.add(ControllerConstants.plus);
    sharedButtons.add(ControllerConstants.rightStick);
    sharedButtons.add(ControllerConstants.home);

    // Right register
    rightButtons.add(ControllerConstants.y);
    rightButtons.add(ControllerConstants.x);
    rightButtons.add(ControllerConstants.b);
    rightButtons.add(ControllerConstants.a);
    rightButtons.add(ControllerConstants.sr);
    rightButtons.add(ControllerConstants.sl);
    rightButtons.add(ControllerConstants.r);
    rightButtons.add(ControllerConstants.zr);
  }

  public JoyConRight(ControllerEventListener eventListener) {
    super(NintendoSwitchHID.JOYCON_RIGHT, "Right");

    this.eventListener = eventListener;
    lastShared = 0;
    lastRight = 0;
    battery = 0;
    stick = new ControllerStick();
    inputs = new HashMap<>();
    stickCalXRight = new int[3];
    stickCalYRight = new int[3];
  }

  @Override
  protected ControllerEventListener eventListener() {
    return eventListener;
  }

  @Override
  protected float horizontal() {
    return stick.getHorizontal();
  }

  @Override
  protected float vertical() {
    return stick.getVertical();
  }

  @Override
  protected void saveCalibration(final int[] factoryCal) {
    stickCalXRight[1] = (factoryCal[10] << 8) & 0xF00 | factoryCal[9];
    stickCalYRight[1] = (factoryCal[11] << 4) | (factoryCal[10] >> 4);
    stickCalXRight[0] = stickCalXRight[1] - ((factoryCal[13] << 8) & 0xF00 | factoryCal[12]);
    stickCalYRight[0] = stickCalYRight[1] - ((factoryCal[14] << 4) | (factoryCal[13] >> 4));
    stickCalXRight[2] = stickCalXRight[1] + ((factoryCal[16] << 8) & 0xF00 | factoryCal[15]);
    stickCalYRight[2] = stickCalYRight[1] + ((factoryCal[17] << 4) | (factoryCal[16] >> 4));
  }

  @Override
  protected void processData(byte[] data) {
    inputs.clear();

    int[] temp = new int[12];
    for (int i = 5; i < 12; i++) {
      byte b = data[i];
      if (b < 0) {
        temp[i] = b + 256;
      } else {
        temp[i] = b;
      }
    }
    int x = temp[8] | ((temp[9] & 0xF) << 8);
    int y = (temp[9] >> 4) | (temp[10] << 4);
    stick.analogStickCalc(x, y, stickCalXRight, stickCalYRight);

    // Getting input change
    int shared = data[3];
    int right = data[2];
    if (data[3] < 0) {
      shared = data[3] + 256;
    }
    if (data[2] < 0) {
      right = data[2] + 256;
    }
    int sharedByte = shared - lastShared;
    lastShared = shared;
    int rightByte = right - lastRight;
    lastRight = right;

    // Battery translation
    int batteryInt = data[1];
    if (data[1] < 0) {
      batteryInt = data[1] + 256;
    }
    battery = Byte.parseByte(Integer.toHexString(batteryInt).substring(0, 1));

    sharedButtons.forEach(b -> {
      if (Math.abs(sharedByte) == b.adress()) {
        inputs.put(b, b.on() == sharedByte);
      }
    });
    rightButtons.forEach(b -> {
      if (Math.abs(rightByte) == b.adress()) {
        inputs.put(b, b.on() == rightByte);
      }
    });

    // Clearing inputs if the same
    if (inputs.equals(oldInputs)) {
      oldInputs = inputs;
      inputs.clear();
    }
  }
}

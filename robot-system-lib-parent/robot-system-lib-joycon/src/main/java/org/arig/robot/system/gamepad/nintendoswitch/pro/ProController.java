package org.arig.robot.system.gamepad.nintendoswitch.pro;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.system.gamepad.nintendoswitch.Controller;
import org.arig.robot.system.gamepad.nintendoswitch.ControllerButton;
import org.arig.robot.system.gamepad.nintendoswitch.ControllerConstants;
import org.arig.robot.system.gamepad.nintendoswitch.ControllerEventListener;
import org.arig.robot.system.gamepad.nintendoswitch.ControllerStick;
import org.arig.robot.system.gamepad.nintendoswitch.NintendoSwitchHID;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Accessors(fluent = true)
public class ProController extends Controller {

  private final ControllerEventListener eventListener;
  private int lastShared;
  private int lastLeft;
  private int lastRight;
  @Getter
  private Map<ControllerButton, Boolean> inputs;
  private Map<ControllerButton, Boolean> oldInputs;
  @Getter
  private byte battery;
  private ControllerStick stickLeft;
  private ControllerStick stickRight;
  private int[] stickCalXLeft;
  private int[] stickCalYLeft;
  private int[] stickCalXRight;
  private int[] stickCalYRight;

  private static final List<ControllerButton> sharedButtons = new ArrayList<>();
  private static final List<ControllerButton> leftButtons = new ArrayList<>();
  private static final List<ControllerButton> rightButtons = new ArrayList<>();

  static {
    // Shared register
    sharedButtons.add(ControllerConstants.minus);
    sharedButtons.add(ControllerConstants.leftStick);
    sharedButtons.add(ControllerConstants.capture);
    sharedButtons.add(ControllerConstants.plus);
    sharedButtons.add(ControllerConstants.rightStick);
    sharedButtons.add(ControllerConstants.home);

    // Left register
    leftButtons.add(ControllerConstants.down);
    leftButtons.add(ControllerConstants.up);
    leftButtons.add(ControllerConstants.right);
    leftButtons.add(ControllerConstants.left);
    leftButtons.add(ControllerConstants.l);
    leftButtons.add(ControllerConstants.zl);

    // Right register
    rightButtons.add(ControllerConstants.y);
    rightButtons.add(ControllerConstants.x);
    rightButtons.add(ControllerConstants.b);
    rightButtons.add(ControllerConstants.a);
    rightButtons.add(ControllerConstants.r);
    rightButtons.add(ControllerConstants.zr);
  }

  public ProController(ControllerEventListener eventListener) {
    super(NintendoSwitchHID.PRO_CONTROLLER, "Pro");

    this.eventListener = eventListener;
    lastShared = 0;
    lastLeft = 0;
    lastRight = 0;
    battery = 0;
    stickLeft = new ControllerStick();
    stickRight = new ControllerStick();
    inputs = new HashMap<>();
    stickCalXLeft = new int[3];
    stickCalYLeft = new int[3];
    stickCalXRight = new int[3];
    stickCalYRight = new int[3];
  }

  @Override
  protected ControllerEventListener eventListener() {
    return eventListener;
  }

  @Override
  protected float horizontal() {
    return stickLeft.getHorizontal();
  }

  @Override
  protected float vertical() {
    return stickLeft.getVertical();
  }

  @Override
  protected void saveCalibration(final int[] factoryCal) {
    stickCalXLeft[1] = (factoryCal[4] << 8) & 0xF00 | factoryCal[3];
    stickCalYLeft[1] = (factoryCal[5] << 4) | (factoryCal[4] >> 4);
    stickCalXLeft[0] = stickCalXLeft[1] - ((factoryCal[7] << 8) & 0xF00 | factoryCal[6]);
    stickCalYLeft[0] = stickCalYLeft[1] - ((factoryCal[8] << 4) | (factoryCal[7] >> 4));
    stickCalXLeft[2] = stickCalXLeft[1] + ((factoryCal[1] << 8) & 0xF00 | factoryCal[0]);
    stickCalYLeft[2] = stickCalYLeft[1] + ((factoryCal[2] << 4) | (factoryCal[2] >> 4));
  }

  @Override
  protected void processData(byte[] data) {
    inputs.clear();

    int[] temp = new int[8];
    for (int i = 5; i < 8; i++) {
      byte b = data[i];
      if (b < 0) {
        temp[i] = b + 256;
      } else {
        temp[i] = b;
      }
    }
    int x = temp[5] | ((temp[6] & 0xF) << 8);
    int y = (temp[6] >> 4) | (temp[7] << 4);
    stickLeft.analogStickCalc(x, y, stickCalXLeft, stickCalYLeft);

    // Getting input change
    int shared = data[3];
    int left = data[4];
    if (data[3] < 0) {
      shared = data[3] + 256;
    }
    if (data[4] < 0) {
      left = data[4] + 256;
    }
    int sharedByte = shared - lastShared;
    lastShared = shared;
    int leftByte = left - lastLeft;
    lastLeft = left;

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
    leftButtons.forEach(b -> {
      if (Math.abs(leftByte) == b.adress()) {
        inputs.put(b, b.on() == leftByte);
      }
    });

    // Clearing inputs if the same
    if (inputs.equals(oldInputs)) {
      oldInputs = inputs;
      inputs.clear();
    }
  }
}

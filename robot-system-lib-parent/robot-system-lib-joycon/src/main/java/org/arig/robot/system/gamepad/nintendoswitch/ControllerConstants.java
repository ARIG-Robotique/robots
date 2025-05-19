package org.arig.robot.system.gamepad.nintendoswitch;

public interface ControllerConstants {

  // Shared buttons
  ControllerButton minus = new ControllerButton("Minus", 1);
  ControllerButton plus = new ControllerButton("Plus", 2);
  ControllerButton rightStick = new ControllerButton("Right Stick", 4);
  ControllerButton leftStick = new ControllerButton("Left Stick", 8);
  ControllerButton capture = new ControllerButton("Capture", 32);
  ControllerButton home = new ControllerButton("Home", 16);

  // Left & Right JoyCon
  ControllerButton sr = new ControllerButton("SR", 16);
  ControllerButton sl = new ControllerButton("SL", 32);

  // Left JoyCon
  ControllerButton down = new ControllerButton("Down", 1);
  ControllerButton up = new ControllerButton("Up", 2);
  ControllerButton right = new ControllerButton("Right", 4);
  ControllerButton left = new ControllerButton("Left", 8);
  ControllerButton l = new ControllerButton("L", 64);
  ControllerButton zl = new ControllerButton("ZL", 128);

  // Right JoyCon
  ControllerButton y = new ControllerButton("Y", 1);
  ControllerButton x = new ControllerButton("X", 2);
  ControllerButton b = new ControllerButton("B", 4);
  ControllerButton a = new ControllerButton("A", 8);
  ControllerButton r = new ControllerButton("R", 64);
  ControllerButton zr = new ControllerButton("ZR", 128);
}

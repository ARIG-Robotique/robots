package org.arig.robot.system.gamepad.nintendoswitch.joycon;

public interface JoyConConstants {

    // Shared buttons
    JoyConButton minus = new JoyConButton("Minus", 1);
    JoyConButton plus = new JoyConButton("Plus", 2);
    JoyConButton rightStick = new JoyConButton("Right Stick", 4);
    JoyConButton leftStick = new JoyConButton("Left Stick", 8);
    JoyConButton capture = new JoyConButton("Capture", 32);
    JoyConButton home = new JoyConButton("Home", 16);

    // Left & Right JoyCon
    JoyConButton sr = new JoyConButton("SR", 16);
    JoyConButton sl = new JoyConButton("SL", 32);

    // Left JoyCon
    JoyConButton down = new JoyConButton("Down", 1);
    JoyConButton up = new JoyConButton("Up", 2);
    JoyConButton right = new JoyConButton("Right", 4);
    JoyConButton left = new JoyConButton("Left", 8);
    JoyConButton l = new JoyConButton("L", 64);
    JoyConButton zl = new JoyConButton("ZL", 128);

    // Right JoyCon
    JoyConButton y = new JoyConButton("Y", 1);
    JoyConButton x = new JoyConButton("X", 2);
    JoyConButton b = new JoyConButton("B", 4);
    JoyConButton a = new JoyConButton("A", 8);
    JoyConButton r = new JoyConButton("R", 64);
    JoyConButton zr = new JoyConButton("ZR", 128);
}

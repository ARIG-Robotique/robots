package com.pi4j.gpio.extension.mcp;

import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinMode;
import com.pi4j.io.gpio.impl.PinImpl;

import java.util.EnumSet;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: GPIO Extension
 * FILENAME      :  MCP3008Pin.java
 *
 * This file is part of the Pi4J project. More information about
 * this project can be found here:  https://www.pi4j.com/
 * **********************************************************************
 * %%
 * Copyright (C) 2012 - 2021 Pi4J
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

/**
 * <p>
 * This GPIO provider implements the MCP3008 SPI GPIO expansion board as native Pi4J GPIO pins. It is a 10-bit ADC providing 8 input
 * channels. More information about the board can be found here: * http://ww1.microchip.com/downloads/en/DeviceDoc/21295d.pdf
 * </p>
 *
 * <p>
 * The MCP3008 is connected via SPI connection to the Raspberry Pi and provides 8 GPIO pins that can be used for analog input pins.
 * </p>
 *
 * @author pojd
 */
public class MCP3008Pin {

  public static final Pin CH0 = createAnalogInputPin(0, "ANALOG INPUT 0");
  public static final Pin CH1 = createAnalogInputPin(1, "ANALOG INPUT 1");
  public static final Pin CH2 = createAnalogInputPin(2, "ANALOG INPUT 2");
  public static final Pin CH3 = createAnalogInputPin(3, "ANALOG INPUT 3");
  public static final Pin CH4 = createAnalogInputPin(4, "ANALOG INPUT 4");
  public static final Pin CH5 = createAnalogInputPin(5, "ANALOG INPUT 5");
  public static final Pin CH6 = createAnalogInputPin(6, "ANALOG INPUT 6");
  public static final Pin CH7 = createAnalogInputPin(7, "ANALOG INPUT 7");

  public static Pin[] ALL = {MCP3008Pin.CH0,
    MCP3008Pin.CH1,
    MCP3008Pin.CH2,
    MCP3008Pin.CH3,
    MCP3008Pin.CH4,
    MCP3008Pin.CH5,
    MCP3008Pin.CH6,
    MCP3008Pin.CH7};

  private static Pin createAnalogInputPin(int channel, String name) {
    return new PinImpl(MCP3008GpioProvider.NAME, channel, name, EnumSet.of(PinMode.ANALOG_INPUT));
  }
}

package com.pi4j.gpio.extension.mcp;

/*
 * #%L
 * **********************************************************************
 * ORGANIZATION  :  Pi4J
 * PROJECT       :  Pi4J :: GPIO Extension
 * FILENAME      :  MCP3004GpioProvider.java
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

import com.pi4j.gpio.extension.base.AdcGpioProvider;
import com.pi4j.io.spi.SpiChannel;
import com.pi4j.io.spi.SpiDevice;
import com.pi4j.io.spi.SpiMode;

import java.io.IOException;

/**
 * <p>
 * This GPIO provider implements the MCP3004 SPI GPIO expansion board as native Pi4J GPIO pins. It is a 10-bit ADC
 * providing 4 input channels. More information about the board can be found here: -
 * http://ww1.microchip.com/downloads/en/DeviceDoc/21295d.pdf
 * </p>
 *
 * <p>
 * The MCP3004 is connected via SPI connection to the Raspberry Pi and provides 4 GPIO pins that can be used for analog
 * input pins. The values returned are in the range 0-1023 (10 bit value).
 * <p>
 * Note: This implementation currently only supports single-ended inputs.
 * </p>
 *
 * @author pojd, Hendrik Motza
 */
public class MCP3004GpioProvider extends MCP3x0xGpioProvider implements AdcGpioProvider {

  public static final String NAME = "com.pi4j.gpio.extension.mcp.MCP3004GpioProvider";
  public static final String DESCRIPTION = "MCP3004 GPIO Provider";
  public static final int INPUT_COUNT = 4;
  public static final int RESOLUTION = 10;

  /**
   * Create new instance of this MCP3004 provider with background monitoring and pin notification events enabled.
   *
   * @param channel spi channel the MCP3004 is connected to
   * @throws IOException if an error occurs during initialization of the SpiDevice
   */
  public MCP3004GpioProvider(final SpiChannel channel) throws IOException {
    this(channel, SpiDevice.DEFAULT_SPI_SPEED, SpiDevice.DEFAULT_SPI_MODE, true);
  }

  /**
   * Create new instance of this MCP3004 provider with background monitoring and pin notification events enabled.
   *
   * @param channel spi channel the MCP3004 is connected to
   * @param speed   spi speed to communicate with MCP3004
   * @throws IOException if an error occurs during initialization of the SpiDevice
   */
  public MCP3004GpioProvider(final SpiChannel channel, final int speed) throws IOException {
    this(channel, speed, SpiDevice.DEFAULT_SPI_MODE, true);
  }

  /**
   * Create new instance of this MCP3004 provider with background monitoring and pin notification events enabled.
   *
   * @param channel spi channel the MCP3004 is connected to
   * @param mode    spi mode to communicate with MCP3004
   * @throws IOException if an error occurs during initialization of the SpiDevice
   */
  public MCP3004GpioProvider(final SpiChannel channel, final SpiMode mode) throws IOException {
    this(channel, SpiDevice.DEFAULT_SPI_SPEED, mode, true);
  }

  /**
   * Create new instance of this MCP3004 provider with background monitoring and pin notification events enabled.
   *
   * @param channel spi channel the MCP3004 is connected to
   * @param speed   spi speed to communicate with MCP3004
   * @param mode    spi mode to communicate with MCP3004
   * @throws IOException if an error occurs during initialization of the SpiDevice
   */
  public MCP3004GpioProvider(final SpiChannel channel, final int speed, final SpiMode mode) throws IOException {
    this(channel, speed, mode, true);
  }

  /**
   * Create new instance of this MCP3004 provider. Optionally enable or disable background monitoring and pin
   * notification events.
   *
   * @param channel                    spi channel the MCP3004 is connected to
   * @param speed                      spi speed to communicate with MCP3004
   * @param mode                       spi mode to communicate with MCP3004
   * @param enableBackgroundMonitoring if enabled, then a background thread will be created to constantly acquire the ADC input values and
   *                                   publish pin change listeners if the value change is beyond the configured threshold.
   * @throws IOException if an error occurs during initialization of the SpiDevice
   */
  public MCP3004GpioProvider(final SpiChannel channel, final int speed, final SpiMode mode,
                             final boolean enableBackgroundMonitoring) throws IOException {
    super(MCP3004Pin.ALL, channel, speed, RESOLUTION, mode);

    // default background monitoring interval
    setMonitorInterval(DEFAULT_MONITOR_INTERVAL);

    // enable|disable background monitoring
    if (enableBackgroundMonitoring) {
      setMonitorEnabled(enableBackgroundMonitoring);
    }
  }

  // ------------------------------------------------------------------------------------------
  // PUBLIC METHODS
  // ------------------------------------------------------------------------------------------
  @Override
  public String getName() {
    return NAME;
  }
}

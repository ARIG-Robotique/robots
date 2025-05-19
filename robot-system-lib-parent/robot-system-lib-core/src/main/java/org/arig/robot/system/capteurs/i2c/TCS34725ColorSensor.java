package org.arig.robot.system.capteurs.i2c;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.arig.robot.communication.i2c.I2CManager;
import org.arig.robot.exception.I2CException;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Classe d'interface avec le composant I2C Adafruit TCS34725 (Digital Color Sensor)
 * <p>
 * Ceci est un portage du driver Arduino Adafruit.
 *
 * @author gdepuille
 * @see <a href="http://www.adafruit.com/products/1334">TCS34725 Documentation</a>
 */
@Slf4j
public class TCS34725ColorSensor {

  private static final byte TCS34725_INVALID_VALUE = -1;

  private static final byte TCS34725_COMMAND_BIT = (byte) 0x80;

  private static final byte TCS34725_ENABLE = 0x00;
  private static final byte TCS34725_ENABLE_AIEN = 0x10;    /* RGBC Interrupt Enable */
  private static final byte TCS34725_ENABLE_WEN = 0x08;    /* Wait enable - Writing 1 activates the wait timer */
  private static final byte TCS34725_ENABLE_AEN = 0x02;    /* RGBC Enable - Writing 1 actives the ADC, 0 disables it */
  private static final byte TCS34725_ENABLE_PON = 0x01;    /* Power on - Writing 1 activates the internal oscillator, 0 disables it */
  private static final byte TCS34725_ATIME = 0x01;    /* Integration time */
  private static final byte TCS34725_WTIME = 0x03;    /* Wait time (if TCS34725_ENABLE_WEN is asserted) */
  private static final byte TCS34725_WTIME_2_4MS = (byte) 0xFF;    /* WLONG0 = 2.4ms   WLONG1 = 0.029s */
  private static final byte TCS34725_WTIME_204MS = (byte) 0xAB;    /* WLONG0 = 204ms   WLONG1 = 2.45s  */
  private static final byte TCS34725_WTIME_614MS = 0x00;    /* WLONG0 = 614ms   WLONG1 = 7.4s   */
  private static final byte TCS34725_AILTL = 0x04;    /* Clear channel lower interrupt threshold */
  private static final byte TCS34725_AILTH = 0x05;
  private static final byte TCS34725_AIHTL = 0x06;    /* Clear channel upper interrupt threshold */
  private static final byte TCS34725_AIHTH = 0x07;
  private static final byte TCS34725_PERS = 0x0C;    /* Persistence register - basic SW filtering mechanism for interrupts */
  private static final byte TCS34725_PERS_NONE = 0b0000;  /* Every RGBC cycle generates an interrupt                                */
  private static final byte TCS34725_PERS_1_CYCLE = 0b0001;  /* 1 clean channel value outside threshold range generates an interrupt   */
  private static final byte TCS34725_PERS_2_CYCLE = 0b0010;  /* 2 clean channel values outside threshold range generates an interrupt  */
  private static final byte TCS34725_PERS_3_CYCLE = 0b0011;  /* 3 clean channel values outside threshold range generates an interrupt  */
  private static final byte TCS34725_PERS_5_CYCLE = 0b0100;  /* 5 clean channel values outside threshold range generates an interrupt  */
  private static final byte TCS34725_PERS_10_CYCLE = 0b0101;  /* 10 clean channel values outside threshold range generates an interrupt */
  private static final byte TCS34725_PERS_15_CYCLE = 0b0110;  /* 15 clean channel values outside threshold range generates an interrupt */
  private static final byte TCS34725_PERS_20_CYCLE = 0b0111;  /* 20 clean channel values outside threshold range generates an interrupt */
  private static final byte TCS34725_PERS_25_CYCLE = 0b1000;  /* 25 clean channel values outside threshold range generates an interrupt */
  private static final byte TCS34725_PERS_30_CYCLE = 0b1001;  /* 30 clean channel values outside threshold range generates an interrupt */
  private static final byte TCS34725_PERS_35_CYCLE = 0b1010;  /* 35 clean channel values outside threshold range generates an interrupt */
  private static final byte TCS34725_PERS_40_CYCLE = 0b1011;  /* 40 clean channel values outside threshold range generates an interrupt */
  private static final byte TCS34725_PERS_45_CYCLE = 0b1100;  /* 45 clean channel values outside threshold range generates an interrupt */
  private static final byte TCS34725_PERS_50_CYCLE = 0b1101;  /* 50 clean channel values outside threshold range generates an interrupt */
  private static final byte TCS34725_PERS_55_CYCLE = 0b1110;  /* 55 clean channel values outside threshold range generates an interrupt */
  private static final byte TCS34725_PERS_60_CYCLE = 0b1111;  /* 60 clean channel values outside threshold range generates an interrupt */
  private static final byte TCS34725_CONFIG = 0x0D;
  private static final byte TCS34725_CONFIG_WLONG = 0x02;    /* Choose between short and long (12x) wait times via TCS34725_WTIME */
  private static final byte TCS34725_CONTROL = 0x0F;    /* Set the gain level for the sensor */
  private static final byte TCS34725_ID = 0x12;    /* 0x44 = TCS34721/TCS34725, 0x4D = TCS34723/TCS34727 */
  private static final byte TCS34725_STATUS = 0x13;
  private static final byte TCS34725_STATUS_AINT = 0x10;    /* RGBC Clean channel interrupt */
  private static final byte TCS34725_STATUS_AVALID = 0x01;    /* Indicates that the RGBC channels have completed an integration cycle */
  private static final byte TCS34725_CDATAL = 0x14;    /* Clear channel data */
  private static final byte TCS34725_CDATAH = 0x15;
  private static final byte TCS34725_RDATAL = 0x16;    /* Red channel data */
  private static final byte TCS34725_RDATAH = 0x17;
  private static final byte TCS34725_GDATAL = 0x18;    /* Green channel data */
  private static final byte TCS34725_GDATAH = 0x19;
  private static final byte TCS34725_BDATAL = 0x1A;    /* Blue channel data */
  private static final byte TCS34725_BDATAH = 0x1B;

  public static final byte TCS34725_ADDRESS = 0x29;

  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  public enum IntegrationTime {
    TCS34725_INTEGRATIONTIME_2_4MS((byte) 0xFF, 3), /* <  2.4ms - 1 cycle    - Max Count: 1024 */
    TCS34725_INTEGRATIONTIME_24MS((byte) 0xF6, 24), /* <  24ms  - 10 cycles  - Max Count: 10240 */
    TCS34725_INTEGRATIONTIME_50MS((byte) 0xEB, 50), /* <  50ms  - 20 cycles  - Max Count: 20480 */
    TCS34725_INTEGRATIONTIME_101MS((byte) 0xD5, 101), /* <  101ms - 42 cycles  - Max Count: 43008 */
    TCS34725_INTEGRATIONTIME_154MS((byte) 0xC0, 154), /* <  154ms - 64 cycles  - Max Count: 65535 */
    TCS34725_INTEGRATIONTIME_700MS((byte) 0x00, 700); /* <  700ms - 256 cycles - Max Count: 65535 */

    @Getter
    private byte value;

    @Getter
    private long delay;

    public static IntegrationTime fromDelay(long delay) {
      for (IntegrationTime time : values()) {
        if (delay <= time.delay) {
          return time;
        }
      }
      return TCS34725_INTEGRATIONTIME_2_4MS;
    }
  }

  @AllArgsConstructor(access = AccessLevel.PRIVATE)
  public enum Gain {
    TCS34725_GAIN_1X((byte) 0x00), /* <  No gain */
    TCS34725_GAIN_4X((byte) 0x01), /* <  4x gain */
    TCS34725_GAIN_16X((byte) 0x02), /* <  16x gain */
    TCS34725_GAIN_60X((byte) 0x03); /* <  60x gain */

    @Getter
    private byte value;

    public static Gain fromValue(int value) {
      for (Gain gain : values()) {
        if (gain.value == value) {
          return gain;
        }
      }
      return TCS34725_GAIN_1X;
    }
  }

  @Data
  @Accessors(fluent = true, chain = true)
  @NoArgsConstructor
  @AllArgsConstructor
  public static class ColorData {
    private int r;
    private int g;
    private int b;
    private int c;

    public String hexColor() {
      return String.format("0x%s%s%s", toHex(r), toHex(g), toHex(b));
    }

    private String toHex(int v) {
      return StringUtils.leftPad(Integer.toHexString(v), 2, '0');
    }
  }

  @Autowired
  private I2CManager i2cManager;

  @Getter
  @Accessors(fluent = true)
  private final String deviceName;

  private boolean initialised;
  private IntegrationTime integrationTime;
  private Gain gain;

  public TCS34725ColorSensor(final String deviceName) {
    this(deviceName, IntegrationTime.TCS34725_INTEGRATIONTIME_2_4MS, Gain.TCS34725_GAIN_4X);
  }

  public TCS34725ColorSensor(final String deviceName, IntegrationTime integrationTime, Gain gain) {
    this.initialised = false;
    this.deviceName = deviceName;
    this.integrationTime = integrationTime;
    this.gain = gain;
  }

  public boolean begin() {
    /* Make sure we're actually connected */
    byte x = read8(TCS34725_ID);
    if ((x != 0x44) && (x != 0x10)) {
      log.warn("Erreur d'initialisation du composant");
      return false;
    }
    initialised = true;
    log.info("Capteur de couleur initialisé.");

    /* Set default integration time and gain */
    setIntegrationTime(integrationTime);
    setGain(gain);

    /* Note: by default, the device is in power down mode on bootup */
    enable();

    return true;
  }

  public void setIntegrationTime(IntegrationTime integrationTime) {
    this.integrationTime = integrationTime;

    if (initialised) {
      /* Update the timing register */
      write8(TCS34725_ATIME, integrationTime.getValue());
    }
  }

  public void setGain(Gain gain) {
    this.gain = gain;

    if (!initialised) {
      /* Update the gain register */
      write8(TCS34725_CONTROL, gain.getValue());
    }
  }

  /**
   * Reads the raw red, green, blue and clear channel values
   *
   * @return The read data
   */
  public ColorData getColorData() {
    if (!initialised) {
      begin();
    }

    if (log.isDebugEnabled()) {
      log.debug("Lecture du capteur de couleur {}", deviceName);
    }

    /* Set a delay for the integration time */
    try {
      Thread.sleep(this.integrationTime.getDelay());
    } catch (InterruptedException e) {
      log.warn("Erreur d'attente pour l'intégration.", e);
    }

    int[] data = readColorData();

    return new ColorData()
      .c(data[0])
      .r(data[1])
      .g(data[2])
      .b(data[3]);
  }

  public int calculateColorTemperature(ColorData rd) {
    return this.calculateColorTemperature(rd.r, rd.g, rd.b);
  }

  /**
   * Converts the raw R/G/B values to color temperature in degree Kelvin
   *
   * @param r Composante Rouge
   * @param g Composante Verte
   * @param b Composante Bleu
   * @return Temperature en degrée Kelvin
   */
  public int calculateColorTemperature(int r, int g, int b) {
    float X, Y, Z;      /* RGB to XYZ correlation      */
    float xc, yc;       /* Chromaticity co-ordinates   */
    float n;            /* McCamy's formula            */
    float cct;

    /* 1. Map RGB values to their XYZ counterparts.    */
    /* Based on 6500K fluorescent, 3000K fluorescent   */
    /* and 60W incandescent values for a wide range.   */
    /* Note: Y = Illuminance or lux                    */
    X = (-0.14282F * r) + (1.54924F * g) + (-0.95641F * b);
    Y = (-0.32466F * r) + (1.57837F * g) + (-0.73191F * b);
    Z = (-0.68202F * r) + (0.77073F * g) + (0.56332F * b);

    /* 2. Calculate the chromaticity co-ordinates      */
    xc = (X) / (X + Y + Z);
    yc = (Y) / (X + Y + Z);

    /* 3. Use McCamy's formula to determine the CCT    */
    n = (xc - 0.3320F) / (0.1858F - yc);

    /* Calculate the final CCT */
    cct = (449.0F * powf(n, 3)) + (3525.0F * powf(n, 2)) + (6823.3F * n) + 5520.33F;

    /* Return the results in degrees Kelvin */
    return (int) cct;
  }

  public int calculateLux(ColorData rd) {
    return this.calculateLux(rd.r, rd.g, rd.b);
  }

  /**
   * Converts the raw R/G/B values to lux
   *
   * @param r Composante Rouge
   * @param g Composante Verte
   * @param b Composante Bleu
   * @return Luminosité en Lux
   */
  public int calculateLux(int r, int g, int b) {
    float illuminance;

    /* This only uses RGB ... how can we integrate clear or calculate lux */
    /* based exclusively on clear since this might be more reliable?      */
    illuminance = (-0.32466F * r) + (1.57837F * g) + (-0.73191F * b);

    return (int) illuminance;
  }

  public void setInterrupt(boolean flag) {
    byte r = read8(TCS34725_ENABLE);
    if (flag) {
      r |= TCS34725_ENABLE_AIEN;
    } else {
      r &= ~TCS34725_ENABLE_AIEN;
    }
    write8(TCS34725_ENABLE, r);
  }

  public void clearInterrupt() {
    try {
      i2cManager.sendData(deviceName, (byte) (TCS34725_COMMAND_BIT | 0x66));
    } catch (I2CException e) {
      log.error(String.format("Erreur lors de l'écriture sur le capteur de couleur %s : %s", deviceName, e.toString()), e);
    }
  }

  public void setIntLimits(short low, short high) {
    write8((byte) 0x04, low & 0xFF);
    write8((byte) 0x05, low >> 8);
    write8((byte) 0x06, high & 0xFF);
    write8((byte) 0x07, high >> 8);
  }

  public void enable() {
    try {
      write8(TCS34725_ENABLE, TCS34725_ENABLE_PON);
      Thread.sleep(10);
      write8(TCS34725_ENABLE, TCS34725_ENABLE_PON | TCS34725_ENABLE_AEN);
    } catch (InterruptedException e) {
      log.warn("Enable du capteur de couleur en erreur", e);
    }
  }

  private void disable() {
    /* Turn the device off to save power */
    byte reg = read8(TCS34725_ENABLE);
    write8(TCS34725_ENABLE, reg & ~(TCS34725_ENABLE_PON | TCS34725_ENABLE_AEN));
  }

  // ---------------------------- PRIVATE METHODS ------------------------ */

  private void write8(byte reg, int value) {
    try {
      i2cManager.sendData(deviceName, (byte) (TCS34725_COMMAND_BIT | reg), (byte) (value & 0XFF));
    } catch (I2CException e) {
      log.error(String.format("Erreur lors de l'écriture sur le capteur de couleur %s : %s", deviceName, e.toString()), e);
    }
  }

  private byte read8(byte reg) {
    try {
      i2cManager.sendData(deviceName, (byte) (TCS34725_COMMAND_BIT | reg));
      return i2cManager.getData(deviceName);
    } catch (I2CException e) {
      log.error("Erreur de lecture du capteur de couleur {} : {}", deviceName, e.toString());
      return TCS34725_INVALID_VALUE;
    }
  }

  private int read16(byte reg) {
    try {
      i2cManager.sendData(deviceName, (byte) (TCS34725_COMMAND_BIT | reg));
      final byte[] data = i2cManager.getData(deviceName, 2);
      return (data[1] & 0xFF) << 8 | (data[0] & 0xFF);
    } catch (I2CException e) {
      log.error("Erreur de lecture du capteur de couleur {} : {}", deviceName, e.toString());
      return TCS34725_INVALID_VALUE;
    }
  }

  /**
   * Lecture des tous les registres de couleur en une seule fois
   */
  private int[] readColorData() {
    try {
      i2cManager.sendData(deviceName, (byte) (TCS34725_COMMAND_BIT | TCS34725_CDATAL));
      final byte[] data = i2cManager.getData(deviceName, 8);
      int c = (data[1] & 0xFF) << 8 | (data[0] & 0xFF);
      int r = (data[3] & 0xFF) << 8 | (data[2] & 0xFF);
      int g = (data[5] & 0xFF) << 8 | (data[4] & 0xFF);
      int b = (data[7] & 0xFF) << 8 | (data[6] & 0xFF);
      return new int[]{c, r, g, b};
    } catch (I2CException e) {
      log.error("Erreur de lecture du capteur de couleur {} : {}", deviceName, e.toString());
      return new int[]{-1, -1, -1, -1};
    }
  }

  private float powf(float x, float y) {
    return (float) (Math.pow((double) x, (double) y));
  }
}

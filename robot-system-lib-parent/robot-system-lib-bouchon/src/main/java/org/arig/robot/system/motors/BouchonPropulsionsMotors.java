package org.arig.robot.system.motors;

public class BouchonPropulsionsMotors extends AbstractPropulsionsMotors {

  private BouchonMotor motor1;
  private BouchonMotor motor2;

  public BouchonPropulsionsMotors(int offset, int min, int max) {
    super(offset);
    motor1 = new BouchonMotor(offset, min, max);
    motor2 = new BouchonMotor(offset, min, max);
  }

  @Override
  public void init() {
    motor1.init();
    motor2.init();
  }

  @Override
  protected void motorConfiguration() {
  }

  @Override
  public void speedMoteur1(final int val) {
    motor1.speed(val);
  }

  @Override
  public void speedMoteur2(final int val) {
    motor2.speed(val);
  }

  @Override
  public int getMinSpeed() {
    return motor1.getMinSpeed();
  }

  @Override
  public int getMaxSpeed() {
    return motor1.getMaxSpeed();
  }

  @Override
  protected int currentSpeedMoteur1() {
    return motor1.currentSpeed();
  }

  @Override
  protected int currentSpeedMoteur2() {
    return motor2.currentSpeed();
  }

  @Override
  public void printVersion() {
  }
}

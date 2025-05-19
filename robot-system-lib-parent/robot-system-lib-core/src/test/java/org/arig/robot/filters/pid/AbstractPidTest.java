package org.arig.robot.filters.pid;

import lombok.AccessLevel;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.ConstantesConfig;
import org.arig.robot.monitoring.MonitoringWrapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.UUID;

@Slf4j
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {PidTestContext.class})
public abstract class AbstractPidTest {

  @Autowired
  private MonitoringWrapper monitoringWrapper;

  @Setter(value = AccessLevel.PROTECTED)
  private boolean hasIntegralLimiter = false;

  protected abstract PidFilter pid();

  @BeforeEach
  public void before() {
    System.setProperty(ConstantesConfig.keyExecutionId, UUID.randomUUID().toString());
    monitoringWrapper.cleanAllPoints();
    pid().reset();
    hasIntegralLimiter = false;
  }

  @AfterEach
  public void after() {
    monitoringWrapper.save();
  }

  @Test
  @SneakyThrows
  public void testP() {
    pid().setTunings(1, 0, 0);

    double consigne = 100;
    double input = 0, output, error;
    for (int i = 0; i < 100; i++) {
      if (i > 10) {
        input = (i * consigne) / 100;
      }
      pid().consigne(consigne);
      error = consigne - input;
      output = pid().filter(input);
      log.info("Test P : consigne {}, mesure {}, output {}", consigne, input, output);
      Assertions.assertEquals(error, output, 0);
    }
  }

  @Test
  @SneakyThrows
  public void testPI() {
    pid().setTunings(1, 1, 0);

    double consigne = 100;
    double input = 0, output, error, errorSum = 0;
    for (int i = 0; i < 100; i++) {
      if (i > 10) {
        input = (i * consigne) / 100;
      }
      pid().consigne(consigne);
      error = consigne - input;
      errorSum += error;
      if (hasIntegralLimiter && errorSum > 4096) {
        errorSum = 4096;
      }
      output = pid().filter(input);
      log.info("Test PI : consigne {}, mesure {}, output {}", consigne, input, output);

      double expected = error + errorSum;
      Assertions.assertEquals(expected, output, 0);
    }
  }

  @Test
  @SneakyThrows
  public void testPID() {
    pid().setTunings(1, 1, 1);

    double consigne = 100;
    double input = 0, output, error, errorSum = 0, errorPrec = 0;
    for (int i = 0; i < 100; i++) {
      if (i > 10) {
        input = (i * consigne) / 100;
      }
      pid().consigne(consigne);
      error = consigne - input;
      errorSum += error;
      if (hasIntegralLimiter && errorSum > 4096) {
        errorSum = 4096;
      }
      output = pid().filter(input);
      log.info("Test PID : consigne {}, mesure {}, output {}", consigne, input, output);

      double expected = error + errorSum + error - errorPrec;
      errorPrec = error;
      Assertions.assertEquals(expected, output, 0);
    }
  }
}

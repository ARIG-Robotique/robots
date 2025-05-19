package org.arig.robot.strategy.actions;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.Point;
import org.arig.robot.strategy.AbstractAction;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author gdepuille on 06/05/15.
 */
@Slf4j
public class InvalidWhenNotRunAfter10SecondsAndCompletedAfter2ExecutionAction extends AbstractAction {

  private final LocalDateTime ldt;

  private int executionCount = 0;

  public InvalidWhenNotRunAfter10SecondsAndCompletedAfter2ExecutionAction() {
    ldt = LocalDateTime.now().plusSeconds(10);
  }

  @Override
  public String name() {
    return "Invalid but valid after " + ldt.format(DateTimeFormatter.ISO_DATE_TIME) + " for 2 times";
  }

  @Override
  public Point entryPoint() {
    return null;
  }

  @Override
  public int order() {
    return 50;
  }

  @Override
  public boolean isValid() {
    return ldt.isBefore(LocalDateTime.now());
  }

  @Override
  public boolean isCompleted() {
    return executionCount > 1;
  }

  @Override
  public void execute() {
    executionCount++;
    log.info("Est devenu valid après un certain temps. Execution n°{}", executionCount);
  }
}

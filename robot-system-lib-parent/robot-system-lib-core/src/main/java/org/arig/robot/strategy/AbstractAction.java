package org.arig.robot.strategy;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.Point;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Slf4j
public abstract class AbstractAction implements Action {

  @Getter
  @Accessors(fluent = true)
  private String uuid = java.util.UUID.randomUUID().toString();

  @Getter
  @Setter
  private LocalDateTime validTime = LocalDateTime.now();

  @Getter
  private boolean completed = false;

  @Getter
  @Accessors(fluent = true)
  public List<String> blockingActions = Collections.emptyList();

  @Getter
  @Accessors(fluent = true)
  public Rectangle blockingZone = null;

  public abstract Point entryPoint();

  protected boolean isTimeValid() {
    return validTime.isBefore(LocalDateTime.now());
  }

  protected void updateValidTime() {
    setValidTime(LocalDateTime.now().plusSeconds(2));
  }

  protected void complete() {
    complete(false);
  }

  protected void complete(boolean withLog) {
    if (withLog) {
      log.info("Action '{}' complete", name());
    }
    completed = true;
  }

  @Override
  public void refreshCompleted() {
  }
}

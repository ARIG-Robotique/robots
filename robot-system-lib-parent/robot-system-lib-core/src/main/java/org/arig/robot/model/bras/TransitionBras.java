package org.arig.robot.model.bras;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Accessors(fluent = true)
@Getter
@AllArgsConstructor
public class TransitionBras implements Serializable {

  private int speed;

  private PointBras[] points;

}

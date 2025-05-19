package org.arig.robot.model.ecran;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.arig.robot.model.Strategy;
import org.arig.robot.model.Team;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class EcranState extends AbstractEcranState {
  private Team team;
  private Strategy strategy = Strategy.QUALIF;
}

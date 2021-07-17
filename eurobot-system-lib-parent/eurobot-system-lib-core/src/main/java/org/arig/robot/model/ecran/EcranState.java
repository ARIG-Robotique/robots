package org.arig.robot.model.ecran;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.arig.robot.model.EStrategy;
import org.arig.robot.model.ETeam;

@Data
@NoArgsConstructor
public class EcranState extends AbstractEcranState {
    private ETeam team;
    private EStrategy strategy = EStrategy.BASIC;
}

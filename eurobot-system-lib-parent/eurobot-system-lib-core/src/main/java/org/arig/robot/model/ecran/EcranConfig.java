package org.arig.robot.model.ecran;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.arig.robot.model.EStrategy;
import org.arig.robot.model.ETeam;

@Data
@NoArgsConstructor
public class EcranConfig extends AbstractEcranConfig {
    private ETeam team;
    private EStrategy strategy = EStrategy.BASIC;
}

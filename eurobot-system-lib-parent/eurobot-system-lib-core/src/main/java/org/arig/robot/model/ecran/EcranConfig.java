package org.arig.robot.model.ecran;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.arig.robot.model.EStrategy;
import org.arig.robot.model.ETeam;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class EcranConfig extends AbstractEcranConfig {
    private ETeam team;
    private EStrategy strategy = EStrategy.BASIC;
}

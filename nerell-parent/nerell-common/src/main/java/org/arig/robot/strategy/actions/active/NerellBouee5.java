package org.arig.robot.strategy.actions.active;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.arig.robot.constants.IEurobotConfig;
import org.arig.robot.model.ETeam;
import org.arig.robot.model.Point;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class NerellBouee5 extends AbstractNerellBoueeBordure {

    public NerellBouee5() {
        super(5);
    }

    @Getter
    @Accessors(fluent = true)
    public List<String> blockingActions = Arrays.asList(
            IEurobotConfig.ACTION_PRISE_BOUEE_PREFIX + "6",
            IEurobotConfig.ACTION_ECUEIL_COMMUN_BLEU
    );

    @Override
    protected Point beforeEntry() {
        if (rs.team() == ETeam.JAUNE) {
            return new Point(710, 1500);
        }
        return null;
    }
}

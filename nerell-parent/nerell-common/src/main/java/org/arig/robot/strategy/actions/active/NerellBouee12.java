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
public class NerellBouee12 extends AbstractNerellBoueeBordure {

    public NerellBouee12() {
        super(12);
    }

    @Getter
    @Accessors(fluent = true)
    public List<String> blockingActions = Arrays.asList(
            IEurobotConfig.ACTION_PRISE_BOUEE_PREFIX + "11",
            IEurobotConfig.ACTION_ECUEIL_COMMUN_JAUNE
    );

    @Override
    protected Point beforeEntry() {
        if (rs.team() == ETeam.BLEU) {
            return new Point(2290, 1500);
        }
        return null;
    }

}

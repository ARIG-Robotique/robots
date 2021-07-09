package org.arig.robot.strategy.actions.active;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.arig.robot.constants.IEurobotConfig;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class OdinBouee12 extends AbstractOdinBoueeBordure {

    public OdinBouee12() {
        super(12);
    }

    @Getter
    @Accessors(fluent = true)
    public List<String> blockingActions = Arrays.asList(
            IEurobotConfig.ACTION_PRISE_BOUEE_PREFIX + "11",
            IEurobotConfig.ACTION_PRISE_BOUEE_BASIC,
            IEurobotConfig.ACTION_ECUEIL_COMMUN_JAUNE
    );
}

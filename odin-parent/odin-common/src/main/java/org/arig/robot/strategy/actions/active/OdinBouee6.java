package org.arig.robot.strategy.actions.active;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.arig.robot.constants.IEurobotConfig;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class OdinBouee6 extends AbstractOdinBouee {

    public OdinBouee6() {
        super(6);
    }

    @Getter
    @Accessors(fluent = true)
    public List<String> blockingActions = Arrays.asList(
            IEurobotConfig.ACTION_PRISE_BOUEE_PREFIX + "5",
            IEurobotConfig.ACTION_PRISE_BOUEE_BASIC,
            IEurobotConfig.ACTION_ECUEIL_COMMUN_BLEU
    );

}

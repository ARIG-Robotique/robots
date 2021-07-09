package org.arig.robot.strategy.actions.active;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.arig.robot.constants.IEurobotConfig;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class OdinBouee5 extends AbstractOdinBoueeBordure {

    public OdinBouee5() {
        super(5);
    }

    @Getter
    @Accessors(fluent = true)
    public List<String> blockingActions = Arrays.asList(
            IEurobotConfig.ACTION_PRISE_BOUEE_PREFIX + "6",
            IEurobotConfig.ACTION_PRISE_BOUEE_NORD,
            IEurobotConfig.ACTION_ECUEIL_COMMUN_BLEU
    );
}

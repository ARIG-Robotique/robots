package org.arig.robot.strategy.actions.active;

import lombok.Getter;
import lombok.experimental.Accessors;
import org.arig.robot.constants.IEurobotConfig;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class OdinBouee10 extends AbstractOdinBouee {

    public OdinBouee10() {
        super(10);
    }
    @Getter
    @Accessors(fluent = true)
    public List<String> blockingActions = Arrays.asList(
            IEurobotConfig.ACTION_ECUEIL_COMMUN_JAUNE,
            IEurobotConfig.ACTION_PRISE_BOUEE_AGGRESSIVE
    );
}

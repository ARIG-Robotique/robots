package org.arig.test.robot.strategy;

import org.arig.robot.filters.ramp.IRampFilter;
import org.arig.robot.filters.ramp.Ramp;
import org.arig.robot.strategy.IAction;
import org.arig.robot.strategy.StrategyManager;
import org.arig.test.robot.strategy.actions.InvalidWhenNotRunAfter10SecondsAndCompletedAfter2ExecutionAction;
import org.arig.test.robot.strategy.actions.OneShotAction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by gdepuille on 15/03/15.
 */
@Configuration
public class StrategyManagerTestContext {

    @Bean
    public StrategyManager strategyManager() {
        return new StrategyManager();
    }

    @Bean
    public IAction oneShotAction() {
        return new OneShotAction();
    }

    @Bean
    public IAction invalidDuring10Seconds() {
        return new InvalidWhenNotRunAfter10SecondsAndCompletedAfter2ExecutionAction();
    }
}

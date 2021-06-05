package org.arig.robot.strategy;

import org.arig.robot.model.AbstractRobotStatus;
import org.arig.robot.model.TestRobotStatus;
import org.arig.robot.strategy.actions.InvalidWhenNotRunAfter10SecondsAndCompletedAfter2ExecutionAction;
import org.arig.robot.strategy.actions.OneShotAction;
import org.arig.robot.system.group.IRobotGroup;
import org.arig.robot.system.group.RobotGroupTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author gdepuille on 15/03/15.
 */
@Configuration
public class StrategyManagerTestContext {

    @Bean
    public StrategyManager strategyManager() {
        return new StrategyManager();
    }

    @Bean
    public AbstractRobotStatus robotStatus() {
        return new TestRobotStatus();
    }

    @Bean
    public IRobotGroup robotGroup() {
        return new RobotGroupTest();
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

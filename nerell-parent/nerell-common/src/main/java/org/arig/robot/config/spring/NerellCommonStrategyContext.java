package org.arig.robot.config.spring;

import org.arig.robot.strategy.StrategyManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author gdepuille on 06/05/15.
 */
@Configuration
@ComponentScan({"org.arig.robot.strategy.actions.active"})
public class NerellCommonStrategyContext {

    @Bean
    public StrategyManager strategyManager() {
        return new StrategyManager();
    }
}

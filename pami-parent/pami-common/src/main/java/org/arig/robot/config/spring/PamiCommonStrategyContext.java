package org.arig.robot.config.spring;

import org.arig.robot.strategy.StrategyManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan({"org.arig.robot.strategy.actions.active.common", "org.arig.robot.strategy.actions.active.pami"})
public class PamiCommonStrategyContext {
    @Bean
    public StrategyManager strategyManager() {
        return new StrategyManager();
    }
}

package org.arig.eurobot.config.spring;

import org.arig.robot.strategy.StrategyManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Created by gdepuille on 06/05/15.
 */
@Configuration
@ComponentScan({"org.arig.eurobot.strategy.actions"})
public class StrategyContext {

    @Bean
    public StrategyManager strategyManager() {
        return new StrategyManager();
    }
}

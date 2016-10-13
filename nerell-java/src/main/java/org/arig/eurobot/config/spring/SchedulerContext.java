package org.arig.eurobot.config.spring;

import org.arig.eurobot.constants.IConstantesSpringConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @author gdepuille on 12/01/15.
 */
@Configuration
@EnableScheduling
@ComponentScan({"org.arig.eurobot.scheduler"})
public class SchedulerContext implements SchedulingConfigurer {

    @Bean(destroyMethod="shutdown")
    public Executor taskExecutor() {
        return Executors.newScheduledThreadPool(IConstantesSpringConfig.nbThreadScheduledExecutor);
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(taskExecutor());
    }
}

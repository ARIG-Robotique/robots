package org.arig.eurobot.config.spring;

import org.arig.eurobot.constants.IConstantesRobot;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by gdepuille on 12/01/15.
 */
@Configuration
@EnableScheduling
@ComponentScan({"org.arig.eurobot.scheduler"})
public class SchedulerContext implements SchedulingConfigurer {

    @Bean(destroyMethod="shutdown")
    public Executor taskExecutor() {
        return Executors.newScheduledThreadPool(IConstantesRobot.nbThreadScheduledExecutor);
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(taskExecutor());
    }
}

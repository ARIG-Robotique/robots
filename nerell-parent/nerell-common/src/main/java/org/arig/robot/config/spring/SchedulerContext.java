package org.arig.robot.config.spring;

import org.arig.robot.constants.IConstantesNerellConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author gdepuille on 12/01/15.
 */
@Configuration
@EnableScheduling
@ComponentScan({"org.arig.robot.scheduler"})
public class SchedulerContext implements SchedulingConfigurer {

    @Bean(destroyMethod="shutdown")
    public Executor taskExecutor() {
        return Executors.newScheduledThreadPool(IConstantesNerellConfig.nbThreadScheduledExecutor);
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(taskExecutor());
    }
}

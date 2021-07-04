package org.arig.robot.config.spring;

import org.arig.robot.constants.IOdinConstantesConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@EnableScheduling
@ComponentScan({"org.arig.robot.scheduler"})
public class OdinCommonSchedulerContext implements SchedulingConfigurer {

    @Bean(destroyMethod="shutdown")
    public ExecutorService taskExecutor() {
        return Executors.newScheduledThreadPool(IOdinConstantesConfig.nbThreadScheduledExecutor);
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(taskExecutor());
    }
}

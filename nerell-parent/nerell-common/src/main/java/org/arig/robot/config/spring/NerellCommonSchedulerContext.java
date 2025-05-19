package org.arig.robot.config.spring;

import org.arig.robot.constants.NerellConstantesConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author gdepuille on 12/01/15.
 */
@Configuration
@EnableScheduling
@ComponentScan({"org.arig.robot.scheduler"})
public class NerellCommonSchedulerContext implements SchedulingConfigurer {

  @Bean(destroyMethod = "shutdown")
  public ExecutorService taskExecutor() {
    return Executors.newScheduledThreadPool(NerellConstantesConfig.nbThreadScheduledExecutor);
  }

  @Override
  public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
    taskRegistrar.setScheduler(taskExecutor());
  }
}

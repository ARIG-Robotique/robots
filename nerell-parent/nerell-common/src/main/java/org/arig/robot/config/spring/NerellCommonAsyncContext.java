package org.arig.robot.config.spring;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.NerellConstantesConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author gdepuille on 29/04/15.
 */
@Slf4j
@Configuration
public class NerellCommonAsyncContext {

  @Bean(destroyMethod = "shutdown")
  public ThreadPoolExecutor threadPoolTaskExecutor() {
    return (ThreadPoolExecutor) Executors.newFixedThreadPool(NerellConstantesConfig.nbThreadAsyncExecutor);
  }
}

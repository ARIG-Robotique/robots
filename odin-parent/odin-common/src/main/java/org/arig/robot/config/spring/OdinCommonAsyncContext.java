package org.arig.robot.config.spring;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesOdinConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Configuration
public class OdinCommonAsyncContext {

    @Bean
    public ThreadPoolExecutor threadPoolTaskExecutor() {
        return (ThreadPoolExecutor) Executors.newFixedThreadPool(IConstantesOdinConfig.nbThreadAsyncExecutor);
    }
}
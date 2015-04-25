package org.arig.eurobot.config.spring;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Created by gdepuille on 12/01/15.
 */
@Configuration
@EnableAsync
@EnableScheduling
@ComponentScan({"org.arig.eurobot.scheduler"})
public class SchedulerContext {
}

package org.arig.robot.config.spring;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.utils.TableUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class EurobotContext {


    @Bean
    public TableUtils tableUtils() {
        return new TableUtils(EurobotConfig.tableWidth, EurobotConfig.tableHeight, EurobotConfig.tableBorder);
    }
}

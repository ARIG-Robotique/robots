package org.arig.robot.config.spring;

import org.arig.robot.system.capteurs.IEcran;
import org.arig.robot.system.capteurs.NerellTestEcran;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

@Configuration
public class NerellSimulatorTestContext extends NerellSimulatorContext {

    @Override
    @Bean
    @DependsOn("ecranProcess")
    public IEcran ecran() throws Exception {
        return new NerellTestEcran();
    }


}

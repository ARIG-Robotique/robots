package org.arig.robot.config.spring;

import org.arig.robot.model.ecran.EcranConfig;
import org.arig.robot.model.ecran.EcranState;

import org.arig.robot.system.capteurs.socket.IEcran;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

@Configuration
public class PamiSimulatorCommonContextOverride extends PamiCommonContext {

  @Bean
  @DependsOn("ecranProcess")
  public IEcran<EcranConfig, EcranState> ecran() {
    return super.ecran();
  }
}

package org.arig.robot.config.spring;

import org.arig.robot.model.NerellRobotStatus;
import org.arig.robot.services.NerellFaceArriereService;
import org.arig.robot.services.NerellFaceAvantService;
import org.arig.robot.services.NerellIOService;
import org.arig.robot.services.NerellRobotServosService;
import org.arig.robot.services.TrajectoryManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author gdepuille on 23/04/15.
 */
@Configuration
@ComponentScan({"org.arig.robot.services"})
public class NerellCommonServicesContext {

  @Bean
  @ConditionalOnMissingBean
  public NerellFaceAvantService nerellFaceAvantService(NerellRobotStatus rs, TrajectoryManager mv,
                                                       NerellRobotServosService servos, NerellIOService ioService) {
    return new NerellFaceAvantService(rs, mv, servos, ioService);
  }

  @Bean
  @ConditionalOnMissingBean
  public NerellFaceArriereService nerellFaceArriereService(NerellRobotStatus rs, TrajectoryManager mv,
                                                           NerellRobotServosService servos, NerellIOService ioService) {
    return new NerellFaceArriereService(rs, mv, servos, ioService);
  }

}

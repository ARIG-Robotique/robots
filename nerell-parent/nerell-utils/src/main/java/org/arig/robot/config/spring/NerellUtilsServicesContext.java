package org.arig.robot.config.spring;

import org.arig.robot.model.NerellRobotStatus;
import org.arig.robot.services.NerellFaceArriereService;
import org.arig.robot.services.NerellFaceArriereServiceUtils;
import org.arig.robot.services.NerellFaceAvantService;
import org.arig.robot.services.NerellFaceAvantServiceUtils;
import org.arig.robot.services.NerellIOService;
import org.arig.robot.services.NerellRobotServosService;
import org.arig.robot.services.TrajectoryManager;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AutoConfigureBefore(NerellCommonServicesContext.class)
public class NerellUtilsServicesContext {

  @Bean
  public NerellFaceAvantService nerellFaceAvantService(NerellRobotStatus rs, TrajectoryManager mv,
                                                       NerellRobotServosService servos, NerellIOService ioService) {
    return new NerellFaceAvantServiceUtils(rs, mv, servos, ioService);
  }

  @Bean
  public NerellFaceArriereService nerellFaceArriereService(NerellRobotStatus rs, TrajectoryManager mv,
                                                         NerellRobotServosService servos, NerellIOService ioService) {
    return new NerellFaceArriereServiceUtils(rs, mv, servos, ioService);
  }
}

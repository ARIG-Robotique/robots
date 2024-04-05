package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.system.servos.SD21Servos;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PamiRobotServosService extends AbstractCommonRobotServosService {
    public PamiRobotServosService(SD21Servos sd21Servos) {
        super(sd21Servos);
    }
}

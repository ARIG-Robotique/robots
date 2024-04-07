package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.system.servos.i2c.ARIG2024IoPamiServos;
import org.arig.robot.system.servos.i2c.SD21Servos;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PamiRobotServosService extends AbstractCommonPamiServosService {
    public PamiRobotServosService(ARIG2024IoPamiServos pamiServos) {
        super(pamiServos);
    }
}

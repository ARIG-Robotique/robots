package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Gestion auto du serrage et desserage des palets au sol
 */
@Slf4j
@Service
public class SerrageService {

    @Autowired
    private RightSideService rightSideService;

    @Autowired
    private LeftSideService leftSideService;

    public void process() {
        lockPalet(rightSideService);
        lockPalet(leftSideService);
    }

    private void lockPalet(IRobotSide side) {
        if (side.buteePalet() && side.presencePalet()) {
            side.pinceSerrageFerme();
        } else {
            side.pinceSerrageOuvert();
        }
    }

}

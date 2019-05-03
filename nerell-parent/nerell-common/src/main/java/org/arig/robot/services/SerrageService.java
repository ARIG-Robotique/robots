package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicBoolean;

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

    private AtomicBoolean enabled = new AtomicBoolean(true);

    public void disable() {
        enabled.set(false);
    }

    public void enable() {
        enabled.set(true);
    }

    public void process() {
        if (enabled.get()) {
            lockPalet(rightSideService);
            lockPalet(leftSideService);
        }
    }

    private void lockPalet(IRobotSide side) {
        if (side.buteePalet() && side.presencePalet()) {
            side.pinceSerrageLock(false);
        } else {
            side.pinceSerrageRepos(false);
        }
    }

}

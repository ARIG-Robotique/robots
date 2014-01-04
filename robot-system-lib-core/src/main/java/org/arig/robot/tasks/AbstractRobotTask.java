package org.arig.robot.tasks;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by mythril on 03/01/14.
 */
@Slf4j
public abstract class AbstractRobotTask extends Thread implements IRobotTask {

    @Setter
    private int delay;

    @Setter
    private DelayMode mode;

    private boolean isRun = true;

    public void shutdown() {
        isRun = false;
    }

    @Override
    public void run() {
        init();

        while(isRun) {
            long startTime = System.currentTimeMillis();

            process();

            long elapsedTime = (mode == DelayMode.AT_START) ? System.currentTimeMillis() - startTime : 0;

            try {
                sleep(delay - elapsedTime);
            } catch (InterruptedException e) {
                log.error("Thread interrompu : " + e.toString());
            }
        }

        end();
    }

    protected abstract void init();
    protected abstract void process();
    protected abstract void end();
}

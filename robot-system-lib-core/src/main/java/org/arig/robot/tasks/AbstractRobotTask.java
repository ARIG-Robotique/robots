package org.arig.robot.tasks;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * Created by mythril on 03/01/14.
 */
@Slf4j
public abstract class AbstractRobotTask extends Thread implements IRobotTask {

    @Setter
    private long delay;

    /**
     * Setter pour le delay d'éxécution de la tâche.
     *
     * @param delay valeur de temp
     * @param unit unité de la valeur de temps
     */
    public void setDelay(long delay, TimeUnit unit) {
        setDelay(unit.toMillis(delay));
    }
}

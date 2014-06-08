package org.arig.robot.tasks;

import java.util.concurrent.TimeUnit;

/**
 * Created by mythril on 03/01/14.
 */
public interface IRobotTask extends Runnable {

    /**
     * Setter pour le delay d'éxecution de la tâche
     *
     * @param delay en milisecondes
     */
    void setDelay(long delay);

    /**
     * Setter pour le delay d'éxécution de la tâche.
     *
     * @param delay valeur de temp
     * @param unit unité de la valeur de temps
     */
    void setDelay(long delay, TimeUnit unit);
}

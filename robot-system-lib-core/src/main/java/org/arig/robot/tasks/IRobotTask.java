package org.arig.robot.tasks;

/**
 * Created by mythril on 03/01/14.
 */
public interface IRobotTask extends Runnable {

    /**
     * Setter pour le delay d'éxecution de la tâche
     *
     * @param delay en milisecondes
     */
    void setDelay(int delay);

    /**
     * Configuration du mode de délai
     *
     * @param mode
     */
    void setMode(DelayMode mode);

    void shutdown();

    /**
     * Enumération de configuration du mode de fonctionnement
     */
    enum DelayMode {
        AT_START, AT_END;
    }
}

package org.arig.robot.stats;

import java.util.Map;

/**
 * Created by mythril on 04/01/14.
 */
public interface IStatsObject {

    /**
     * Définition d'un préfix devant les clé de valeurs
     *
     * @param prefix
     */
    void setKeyPrefix(String prefix);

    /**
     * Retourne l'ensemble des valeurs pour chaque clé
     *
     * @return
     */
    Map<String, String> getValues();
}

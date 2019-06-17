package org.arig.robot.filters.average;

import org.arig.robot.filters.IFilter;

/**
 * @author gdepuille on 08/05/15.
 */
public interface IAverage<T> extends IFilter<T, T> {

    /**
     * Nombre max de valeur pour le calcul
     *
     * @param nbValues Nombre de valeur à inclure
     */
    void setNbValues(int nbValues);

    /**
     * Nombre d'élement courant pour le filtrage
     *
     * @return Le nombre d'élement utilisé pour le filtrage
     */
    int size();
}

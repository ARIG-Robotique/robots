package org.arig.robot.filters.values;

/**
 * @author gdepuille on 08/05/15.
 */
public interface IAverage<T> {

    /**
     * Nombre limit de valeur pour le calcul
     *
     * @param limit Nombre limit de valeur
     */
    void setLimit(int limit);

    /**
     * Reset les calculs de moyenne.
     */
    void reset();

    /**
     * Réalise le calcul avec la nouvelle valeur.
     *
     * @param newValue Nouvelle valeur lu pour le calcul
     *
     * @return La nouvelle valeur moyenne après intégration de la nouvelle lecture.
     */
    T average(T newValue);
}

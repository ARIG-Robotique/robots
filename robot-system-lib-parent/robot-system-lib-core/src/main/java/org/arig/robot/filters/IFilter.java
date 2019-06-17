package org.arig.robot.filters;

public interface IFilter<In, Out> {

    String FILTER_VALUE_NULL_MESSAGE = "La valeur a filtrer ne peut être null";

    /**
     * Réinitialisation du filtre
     */
    default void reset() { }

    /**
     * Application du filtre
     *
     * @param value Valeur a filtrer
     * @return La valeur filtré selon l'algorithme implémenté
     */
    Out filter(In value);
}

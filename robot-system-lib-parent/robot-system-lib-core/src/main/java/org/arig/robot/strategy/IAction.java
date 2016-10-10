package org.arig.robot.strategy;

/**
 * Contrat pour la définition des actions.
 *
 * @author gdepuille
 */
public interface IAction {

    /**
     * Nom de la statégy
     *
     * @return Le nom de la stratégy
     */
    String name();

    /**
     * Valeur indiquant l'ordre de priorité d'éxécution.
     * Plus la valeur est grande plus cela est prioritaire.
     *
     * @return La valeur de l'ordre
     */
    int order();

    /**
     * Est-ce que toute les conditions sont réunies pour l'éxécution ?
     *
     * @return true si cette action peut être éxécuter
     */
    boolean isValid();

    /**
     * Renvoi l'information concernant la réalisation complète de l'action.
     *
     * @return true si cette action est terminé. Elle sera supprimé de la liste des actions possibles
     */
    boolean isCompleted();

    /**
     * Processus d'éxécution de l'action
     */
    void execute();

}

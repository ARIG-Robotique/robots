package org.arig.robot.strategy;

import java.awt.*;
import java.util.List;

/**
 * Contrat pour la définition des actions.
 *
 * @author gdepuille
 */
public interface IAction {

    /**
     * Nom de l'action
     *
     * @return Le nom de l'action
     */
    String name();

    /**
     * Valeur indiquant l'ordre de priorité d'exécution.
     * Plus la valeur est grande plus cela est prioritaire.
     *
     * @return La valeur de l'ordre
     */
    int order();

    /**
     * Est-ce que toute les conditions sont réunies pour l'exécution ?
     *
     * @return true si cette action peut être éxécuter
     */
    boolean isValid();

    /**
     * Retourne le nom des actions de l'autre robot empechant d'executer cette action
     */
    List<String> blockingActions();

    Rectangle blockingZone();

    /**
     * Renvoi l'information concernant la réalisation complète de l'action.
     *
     * @return true si cette action est terminé. Elle sera supprimé de la liste des actions possibles
     */
    boolean isCompleted();

    /**
     * Processus d'exécution de l'action
     */
    void execute();

    /**
     * Recupere l'UUID de l'action
     */
    String uuid();

}

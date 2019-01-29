package org.arig.robot.constants;

/**
 * @author gdepuille on 13/10/16.
 */
public interface IConstantesConfig {

    // Profile de configuration pour le monitoring
    String profileMonitoring = "monitoring";

    // Profile de configuration l'IHM
    String profileUI = "ui";

    // Clé pour la récupération de l'identifiant d'éxécution
    String keyExecutionId = "execution.id";

    // Clé pour la récupération du context spring dans DataFX
    String keySpringContext = "datafx.springCtx";
}

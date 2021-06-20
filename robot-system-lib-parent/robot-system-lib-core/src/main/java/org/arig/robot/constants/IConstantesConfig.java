package org.arig.robot.constants;

public interface IConstantesConfig {

    // Profile de configuration pour le monitoring
    String profileMonitoring = "monitoring";

    // Clé pour la récupération de l'identifiant d'exécution
    String keyExecutionId = "execution.id";

    String executiondIdFormat = "yyyyMMddHHmmss";
    String executiondDateFormat = "yyyy-MM-dd HH:mm:ss";

    // Paramètre VM pour ne pas lancer l'écran (pour débug local)
    String disableEcran = "disableEcran";
}

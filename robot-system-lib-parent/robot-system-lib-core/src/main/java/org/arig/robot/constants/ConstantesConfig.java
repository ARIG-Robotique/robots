package org.arig.robot.constants;

public interface ConstantesConfig {

    // Profile de configuration pour le monitoring
    String profileMonitoring = "monitoring";
    // Profile de configuration pour le simulateur
    String profileSimulator = "simulateur";

    // Clé pour la récupération de l'identifiant d'exécution
    String keyExecutionId = "execution.id";

    // Clé pour l'identification du PAMI
    String keyPamiId = "pami.id";

    // Clé pour les profiles Spring
    String keySpringProfiles = "spring.profiles.active";

    String executiondIdFormat = "yyyyMMddHHmmss";
    String executiondDateFormat = "yyyy-MM-dd HH:mm:ss";

    // Paramètre VM pour ne pas lancer l'écran (pour débug local)
    String disableEcran = "disableEcran";
}

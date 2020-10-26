package org.arig.robot.system;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TrajectoryManagerConfig {
    /* Fenetre d'arret / approche distance (en pulse) */
    private final double fenetreApprocheSansFreinDistance;
    private final double fenetreApprocheAvecFreinDistance;
    private final double fenetreArretDistance;

    /* Fenetre d'arret / approche orientation (en pulse) */
    private final double fenetreApprocheSansFreinOrientation;
    private final double fenetreApprocheAvecFreinOrientation;
    private final double fenetreArretOrientation;

    /* Angle de départ pour les déplacements (en pulse).
     * Si l'angle est supérieur en absolu, on annule la distance
     * afin de naviguer en priorité en marche avant.
     * Cela a pour effet de tourner sur place en reculant avant de partir en avant.
     */
    private final double startAngleDemiTour;

    /* Angle a partir duquel la vitesse de déplacement devient null (en pulse)
     * Si l'angle du point suivant est inférieur un coef visant a limiter la vitesse est calculé
     */
    private final double startAngleLimitSpeedDistance;

    private final double sampleTimeS;
}

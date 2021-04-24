package org.arig.robot.model;

import java.util.function.Predicate;

public enum ECouleurBouee {
    ROUGE,
    VERT,
    INCONNU;

    public static Predicate<ECouleurBouee> isRouge = b -> b == ECouleurBouee.ROUGE;
    public static Predicate<ECouleurBouee> isVert = b -> b == ECouleurBouee.VERT;
    public static Predicate<ECouleurBouee> isNotRouge = b -> b != null && b != ECouleurBouee.ROUGE;
    public static Predicate<ECouleurBouee> isNotVert = b -> b != null && b != ECouleurBouee.VERT;
}

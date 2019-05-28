package org.arig.robot.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.arig.robot.system.ICarouselManager;

@Getter
@AllArgsConstructor
public enum ESide {
    DROITE(ICarouselManager.VENTOUSE_DROITE),
    GAUCHE(ICarouselManager.VENTOUSE_GAUCHE);

    private int positionVentouse;
}

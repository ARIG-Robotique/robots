package org.arig.robot.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.arig.robot.system.ICarouselManager;

@Getter
@AllArgsConstructor
public enum ESide {
    DROITE(ICarouselManager.VENTOUSE_DROITE, ICarouselManager.MAGASIN_DROIT),
    GAUCHE(ICarouselManager.VENTOUSE_GAUCHE, ICarouselManager.MAGASIN_GAUCHE);

    private int positionVentouse;
    private int positionMagasin;
}

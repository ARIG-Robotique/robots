package org.arig.robot.services;

import org.arig.robot.model.ESide;

/**
 * Interface représentant les IO et Servos d'un côté du robot
 */
public interface IRobotSide {

    ESide id();

    int positionCarouselVentouse();

    int positionCarouselMagasin();

    // IO

    boolean buteePalet();

    boolean presencePalet();

    boolean presenceVentouse();

    void enablePompeAVide();

    void disablePompeAVide();

    void releaseElectroVanne();

    boolean paletPrisDansVentouse();

    int nbPaletDansMagasin();

    boolean presencePaletVentouse();

    // SERVOS

    void ascenseurTable(boolean wait);

    void ascenseurTableGold(boolean wait);

    void ascenseurDistributeur(boolean wait);

    void ascenseurAccelerateur(boolean wait);

    void ascenseurCarousel(boolean wait);

    void ascenseurCarouselDepose(boolean wait);

    void pivotVentouseTable(boolean wait);

    void pivotVentouseFacade(boolean wait);

    void pivotVentouseCarouselVertical(boolean wait);

    void pivotVentouseCarouselSortie(boolean wait);

    void pinceSerrageRepos(boolean wait);

    void pinceSerrageLock(boolean wait);

    void porteBarilletOuvert(boolean wait);

    void porteBarilletFerme(boolean wait);

    void pousseAccelerateurFerme(boolean wait);

    void pousseAccelerateurStandby(boolean wait);

    void pousseAccelerateurAction(boolean wait);

    void ejectionMagasinFerme(boolean wait);

    void ejectionMagasinOuvert(boolean wait);

    void trappeMagasinFerme(boolean wait);

    void trappeMagasinOuvert(boolean wait);
}

package org.arig.robot.services;

/**
 * Interface représentant les IO et Servos d'un côté du robot
 */
public interface IRobotSide {

    int id();

    int positionCarouselPince();

    int positionCarouselMagasin();

    // IO

    boolean buteePalet();

    boolean presencePalet();

    boolean presenceVentouse();

    void enablePompeAVide();

    void disablePompeAVide();

    boolean paletPrisDansVentouse();

    int nbPaletDansMagasin();

    // SERVOS

    void ascenseurTable();

    void ascenseurDistributeur();

    void ascenseurAccelerateur();

    void ascenseurCarousel();

    void pivotVentouseTable();

    void pivotVentouseFacade();

    void pivotVentouseCarousel();

    void pinceSerrageOuvert();

    void pinseSerrageLock();

    void pinceSerrageFerme();

    void porteBarilletOuvert();

    void porteBarilletFerme();

    void pousseAccelerateurFerme();

    void pousseAccelerateurStandby();

    void pousseAccelerateurAction();

    void ejectionMagasinFerme();

    void ejectionMagasinOuvert();

    void trappeMagasinFerme();

    void trappeMagasinOuvert();
}

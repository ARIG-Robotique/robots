package org.arig.robot.services;

import org.arig.robot.model.ESide;

/**
 * Interface représentant les IO et Servos d'un côté du robot
 */
public interface IRobotSide {

    ESide id();

    int positionCarouselPince();

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

    // SERVOS

    default void ascenseurAndVentouseHome() {
        ascenseurDistributeur();
        pivotVentouseTable();
    }

    void ascenseurTable();

    void ascenseurTableGold();

    void ascenseurDistributeur();

    void ascenseurAccelerateur();

    void ascenseurCarousel();

    void ascenseurPreAccelerateur();

    void pivotVentouseTable();

    void pivotVentouseFacade();

    void pivotVentouseCarousel();

    void pivotPinceSortieCarousel();

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

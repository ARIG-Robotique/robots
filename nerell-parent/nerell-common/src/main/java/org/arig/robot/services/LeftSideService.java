package org.arig.robot.services;

import org.arig.robot.model.Carousel;
import org.arig.robot.model.ESide;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LeftSideService implements IRobotSide {

    @Autowired
    private IIOService ioService;

    @Autowired
    private ServosService servosService;

    @Override
    public ESide id() {
        return ESide.GAUCHE;
    }

    @Override
    public int positionCarouselPince() {
        return Carousel.PINCE_GAUCHE;
    }

    @Override
    public int positionCarouselMagasin() {
        return Carousel.MAGASIN_GAUCHE;
    }

    @Override
    public boolean buteePalet() {
        return ioService.buteePaletGauche();
    }

    @Override
    public boolean presencePalet() {
        return ioService.presencePaletGauche();
    }

    @Override
    public boolean presenceVentouse() {
        return ioService.presencePaletVentouseGauche();
    }

    @Override
    public void enablePompeAVide() {
        ioService.enablePompeAVideGauche();
    }

    @Override
    public void disablePompeAVide() {
        ioService.disablePompeAVideGauche();
    }

    @Override
    public boolean paletPrisDansVentouse() {
        return ioService.paletPrisDansVentouseGauche();
    }

    @Override
    public int nbPaletDansMagasin() {
        return ioService.nbPaletDansMagasinGauche();
    }

    @Override
    public void ascenseurTable() {
        servosService.ascenseurGaucheTable();
    }

    @Override
    public void ascenseurTableGold() {
        servosService.ascenseurGaucheTableGold();
    }

    @Override
    public void ascenseurDistributeur() {
        servosService.ascenseurGaucheDistributeur();
    }

    @Override
    public void ascenseurAccelerateur() {
        servosService.ascenseurGaucheAccelerateur();
    }

    @Override
    public void ascenseurCarousel() {
        servosService.ascenseurGaucheCarousel();
    }

    @Override
    public void ascenseurPreAccelerateur() {
        servosService.ascenseurGauchePreAccelerateur();
    }

    @Override
    public void pivotVentouseTable() {
        servosService.pivotVentouseGaucheTable();
    }

    @Override
    public void pivotVentouseFacade() {
        servosService.pivotVentouseGaucheFacade();
    }

    @Override
    public void pivotVentouseCarousel() {
        servosService.pivotVentouseGaucheCarousel();
    }

    @Override
    public void pivotPinceSortieCarousel() {
        servosService.pivotVentouseGaucheSortieCarousel();
    }

    @Override
    public void pinceSerrageOuvert() {
        servosService.pinceSerragePaletGaucheOuvert();
    }

    @Override
    public void pinseSerrageLock() {
        servosService.pinceSerragePaletGaucheLock();
    }

    @Override
    public void pinceSerrageFerme() {
        servosService.pinceSerragePaletGaucheFerme();
    }

    @Override
    public void porteBarilletFerme() {
        servosService.porteBarilletGaucheFerme();
    }

    @Override
    public void porteBarilletOuvert() {
        servosService.porteBarilletGaucheOuvert();
    }

    @Override
    public void pousseAccelerateurFerme() {
        servosService.pousseAccelerateurGaucheFerme();
    }

    @Override
    public void pousseAccelerateurStandby() {
        servosService.pousseAccelerateurGaucheStandby();
    }

    @Override
    public void pousseAccelerateurAction() {
        servosService.pousseAccelerateurGaucheAction();
    }

    @Override
    public void ejectionMagasinFerme() {
        servosService.ejectionMagasinGaucheFerme();
    }

    @Override
    public void ejectionMagasinOuvert() {
        servosService.ejectionMagasinGaucheOuvert();
    }

    @Override
    public void trappeMagasinFerme() {
        servosService.trappeMagasinGaucheFerme();
    }

    @Override
    public void trappeMagasinOuvert() {
        servosService.trappeMagasinGaucheOuvert();
    }
}

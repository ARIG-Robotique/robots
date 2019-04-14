package org.arig.robot.services;

import org.arig.robot.model.Carousel;
import org.arig.robot.model.ESide;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RightSideService implements IRobotSide {

    @Autowired
    private IIOService ioService;

    @Autowired
    private ServosService servosService;

    @Override
    public ESide id() {
        return ESide.DROITE;
    }

    @Override
    public int positionCarouselPince() {
        return Carousel.PINCE_DROITE;
    }

    @Override
    public int positionCarouselMagasin() {
        return Carousel.MAGASIN_DROIT;
    }

    @Override
    public boolean buteePalet() {
        return ioService.buteePaletDroit();
    }

    @Override
    public boolean presencePalet() {
        return ioService.presencePaletDroit();
    }

    @Override
    public boolean presenceVentouse() {
        return ioService.presencePaletVentouseDroit();
    }

    @Override
    public void enablePompeAVide() {
        ioService.enablePompeAVideDroite();
    }

    @Override
    public void disablePompeAVide() {
        ioService.disablePompeAVideDroite();
    }

    @Override
    public boolean paletPrisDansVentouse() {
        return ioService.paletPrisDansVentouseDroit();
    }

    @Override
    public int nbPaletDansMagasin() {
        return ioService.nbPaletDansMagasinDroit();
    }

    @Override
    public void ascenseurTable() {
        servosService.ascenseurDroitTable();
    }

    @Override
    public void ascenseurTableGold() {
        servosService.ascenseurDroitTableGold();
    }

    @Override
    public void ascenseurDistributeur() {
        servosService.ascenseurDroitDistributeur();
    }

    @Override
    public void ascenseurAccelerateur() {
        servosService.ascenseurDroitAccelerateur();
    }

    @Override
    public void ascenseurCarousel() {
        servosService.ascenseurDroitCarousel();
    }

    @Override
    public void ascenseurPreAccelerateur() {
        servosService.ascenseurDroitPreAccelerateur();
    }

    @Override
    public void pivotVentouseTable() {
        servosService.pivotVentouseDroitTable();
    }

    @Override
    public void pivotVentouseFacade() {
        servosService.pivotVentouseDroitFacade();
    }

    @Override
    public void pivotVentouseCarousel() {
        servosService.pivotVentouseDroitCarousel();
    }

    @Override
    public void pivotPinceSortieCarousel() {
        servosService.pivotVentouseDroitSortieCarousel();
    }

    @Override
    public void pinceSerrageOuvert() {
        servosService.pinceSerragePaletDroitOuvert();
    }

    @Override
    public void pinseSerrageLock() {
        servosService.pinceSerragePaletDroitLock();
    }

    @Override
    public void pinceSerrageFerme() {
        servosService.pinceSerragePaletDroitFerme();
    }

    @Override
    public void porteBarilletFerme() {
        servosService.porteBarilletDroitFerme();
    }

    @Override
    public void porteBarilletOuvert() {
        servosService.porteBarilletDroitOuvert();
    }

    @Override
    public void pousseAccelerateurFerme() {
        servosService.pousseAccelerateurDroitFerme();
    }

    @Override
    public void pousseAccelerateurStandby() {
        servosService.pousseAccelerateurDroitStandby();
    }

    @Override
    public void pousseAccelerateurAction() {
        servosService.pousseAccelerateurDroitAction();
    }

    @Override
    public void ejectionMagasinFerme() {
        servosService.ejectionMagasinDroitFerme();
    }

    @Override
    public void ejectionMagasinOuvert() {
        servosService.ejectionMagasinDroitOuvert();
    }

    @Override
    public void trappeMagasinFerme() {
        servosService.trappeMagasinDroitFerme();
    }

    @Override
    public void trappeMagasinOuvert() {
        servosService.trappeMagasinDroitOuvert();
    }
}
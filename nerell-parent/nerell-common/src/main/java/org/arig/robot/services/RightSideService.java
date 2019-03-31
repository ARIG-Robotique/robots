package org.arig.robot.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RightSideService implements IRobotSide {

    @Autowired
    private IIOService ioService;

    @Autowired
    private ServosService servosService;

    @Override
    public int id() {
        return 2;
    }

    @Override
    public int positionCarouselPince() {
        return -1;
    }

    @Override
    public int positionCarouselMagasin() {
        return -1;
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

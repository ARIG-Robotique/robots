package org.arig.robot.services;

import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.model.ESide;
import org.arig.robot.system.ICarouselManager;
import org.arig.robot.utils.ThreadUtils;
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
    public int positionCarouselVentouse() {
        return ICarouselManager.VENTOUSE_DROITE;
    }

    @Override
    public int positionCarouselMagasin() {
        return ICarouselManager.MAGASIN_DROIT;
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
        ioService.videElectroVanneDroite();
        ioService.enablePompeAVideDroite();
    }

    @Override
    public void disablePompeAVide() {
        ioService.disablePompeAVideDroite();
    }

    @Override
    public void releaseElectroVanne() {
        ioService.airElectroVanneDroite();
        ThreadUtils.sleep(IConstantesNerellConfig.tempsActivationElectrovanne);
        ioService.videElectroVanneDroite();
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
    public boolean presencePaletVentouse() {
        return ioService.presencePaletVentouseDroit();
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
    public void ascenseurCarouselDepose() {
        servosService.ascenseurDroitCarouselDepose();
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
    public void pivotVentouseCarouselVertical() {
        servosService.pivotVentouseDroitCarouselVertical();
    }

    @Override
    public void pivotVentouseCarouselSortie() {
        servosService.pivotVentouseDroitCarouselSortie();
    }

    @Override
    public void pinceSerrageRepos() {
        servosService.pinceSerragePaletDroitRepos();
    }

    @Override
    public void pinceSerrageLock() {
        servosService.pinceSerragePaletDroitLock();
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

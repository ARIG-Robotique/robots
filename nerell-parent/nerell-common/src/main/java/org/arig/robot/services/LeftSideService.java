package org.arig.robot.services;

import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.model.ESide;
import org.arig.robot.system.ICarouselManager;
import org.arig.robot.utils.ThreadUtils;
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
        return ICarouselManager.PINCE_GAUCHE;
    }

    @Override
    public int positionCarouselMagasin() {
        return ICarouselManager.MAGASIN_GAUCHE;
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
        ioService.videElectroVanneGauche();
        ioService.enablePompeAVideGauche();
    }

    @Override
    public void disablePompeAVide() {
        ioService.disablePompeAVideGauche();
    }

    @Override
    public void releaseElectroVanne() {
        ioService.airElectroVanneGauche();
        ThreadUtils.sleep(IConstantesNerellConfig.tempsActivationElectrvanne);
        ioService.videElectroVanneGauche();
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
    public void ascenseurCarouselDepose() {
        servosService.ascenseurGaucheCarouselDepose();
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
    public void pivotVentouseCarouselVertical() {
        servosService.pivotVentouseGaucheCarouselVertical();
    }

    @Override
    public void pivotVentouseCarouselSortie() {
        servosService.pivotVentouseGaucheCarouselSortie();
    }

    @Override
    public void pinceSerrageRepos() {
        servosService.pinceSerragePaletGaucheRepos();
    }

    @Override
    public void pinceSerrageLock() {
        servosService.pinceSerragePaletGaucheLock();
    }

    @Override
    public void pinceSerrageStandby() {
        servosService.pinceSerragePaletGaucheStandby();
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

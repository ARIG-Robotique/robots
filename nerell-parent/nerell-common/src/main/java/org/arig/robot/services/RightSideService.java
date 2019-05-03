package org.arig.robot.services;

import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.constants.IConstantesServos;
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
    public void ascenseurTable(boolean wait) {
        servosService.ascenseurDroit(IConstantesServos.ASCENSEUR_DROIT_TABLE, wait);
    }

    @Override
    public void ascenseurTableGold(boolean wait) {
        servosService.ascenseurDroit(IConstantesServos.ASCENSEUR_DROIT_TABLE_GOLD, wait);
    }

    @Override
    public void ascenseurDistributeur(boolean wait) {
        servosService.ascenseurDroit(IConstantesServos.ASCENSEUR_DROIT_DISTRIBUTEUR, wait);
    }

    @Override
    public void ascenseurAccelerateur(boolean wait) {
        servosService.ascenseurDroit(IConstantesServos.ASCENSEUR_DROIT_ACCELERATEUR, wait);
    }

    @Override
    public void ascenseurCarousel(boolean wait) {
        servosService.ascenseurDroit(IConstantesServos.ASCENSEUR_DROIT_CAROUSEL, wait);
    }

    @Override
    public void ascenseurCarouselDepose(boolean wait) {
        servosService.ascenseurDroit(IConstantesServos.ASCENSEUR_DROIT_CAROUSEL_DEPOSE, wait);
    }

    @Override
    public void pivotVentouseTable(boolean wait) {
        servosService.pivotVentouseDroit(IConstantesServos.PIVOT_VENTOUSE_DROIT_TABLE, wait);
    }

    @Override
    public void pivotVentouseFacade(boolean wait) {
        servosService.pivotVentouseDroit(IConstantesServos.PIVOT_VENTOUSE_DROIT_FACADE, wait);
    }

    @Override
    public void pivotVentouseCarouselVertical(boolean wait) {
        servosService.pivotVentouseDroit(IConstantesServos.PIVOT_VENTOUSE_DROIT_CAROUSEL_VERTICAL, wait);
    }

    @Override
    public void pivotVentouseCarouselSortie(boolean wait) {
        servosService.pivotVentouseDroit(IConstantesServos.PIVOT_VENTOUSE_DROIT_CAROUSEL_VERTICAL, wait);
    }

    @Override
    public void pinceSerrageRepos(boolean wait) {
        servosService.pinceSerragePaletDroit(IConstantesServos.PINCE_SERRAGE_PALET_DROIT_REPOS, wait);
    }

    @Override
    public void pinceSerrageLock(boolean wait) {
        servosService.pinceSerragePaletDroit(IConstantesServos.PINCE_SERRAGE_PALET_DROIT_LOCK, wait);
    }

    @Override
    public void porteBarilletFerme(boolean wait) {
        servosService.porteBarilletDroit(IConstantesServos.PORTE_BARILLET_DROIT_FERME, wait);
    }

    @Override
    public void porteBarilletOuvert(boolean wait) {
        servosService.porteBarilletDroit(IConstantesServos.PORTE_BARILLET_DROIT_OUVERT, wait);
    }

    @Override
    public void pousseAccelerateurFerme(boolean wait) {
        servosService.pousseAccelerateurDroit(IConstantesServos.POUSSE_ACCELERATEUR_DROIT_FERME, wait);
    }

    @Override
    public void pousseAccelerateurStandby(boolean wait) {
        servosService.pousseAccelerateurDroit(IConstantesServos.POUSSE_ACCELERATEUR_DROIT_STANDBY, wait);
    }

    @Override
    public void pousseAccelerateurAction(boolean wait) {
        servosService.pousseAccelerateurDroit(IConstantesServos.POUSSE_ACCELERATEUR_DROIT_ACTION, wait);
    }

    @Override
    public void ejectionMagasinFerme(boolean wait) {
        servosService.ejectionMagasinDroit(IConstantesServos.EJECTION_MAGASIN_DROIT_FERME, wait);
    }

    @Override
    public void ejectionMagasinOuvert(boolean wait) {
        servosService.ejectionMagasinDroit(IConstantesServos.EJECTION_MAGASIN_DROIT_OUVERT, wait);
    }

    @Override
    public void trappeMagasinFerme(boolean wait) {
        servosService.trappeMagasinDroit(IConstantesServos.TRAPPE_MAGASIN_DROIT_FERME, wait);
    }

    @Override
    public void trappeMagasinOuvert(boolean wait) {
        servosService.trappeMagasinDroit(IConstantesServos.TRAPPE_MAGASIN_DROIT_OUVERT, wait);
    }
}

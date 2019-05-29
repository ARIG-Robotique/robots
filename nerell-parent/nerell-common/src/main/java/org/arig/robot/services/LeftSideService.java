package org.arig.robot.services;

import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.constants.IConstantesServos;
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
    public int positionCarouselVentouse() {
        return ICarouselManager.VENTOUSE_GAUCHE;
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
        ThreadUtils.sleep(IConstantesNerellConfig.tempsActivationElectrovanne);
        ioService.videElectroVanneGauche();
    }

    @Override
    public void airElectroVanne() {
        ioService.airElectroVanneGauche();
    }

    @Override
    public void videElectroVanne() {
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
    public boolean presencePaletVentouse() {
        return ioService.presencePaletVentouseGauche();
    }

    @Override
    public void ascenseurTable(boolean wait) {
        servosService.ascenseurGauche(IConstantesServos.ASCENSEUR_GAUCHE_TABLE, wait);
    }

    @Override
    public void ascenseurTableGold(boolean wait) {
        servosService.ascenseurGauche(IConstantesServos.ASCENSEUR_GAUCHE_TABLE_GOLD, wait);
    }

    @Override
    public void ascenseurDistributeur(boolean wait) {
        servosService.ascenseurGauche(IConstantesServos.ASCENSEUR_GAUCHE_DISTRIBUTEUR, wait);
    }

    @Override
    public void ascenseurAccelerateur(boolean wait) {
        servosService.ascenseurGauche(IConstantesServos.ASCENSEUR_GAUCHE_ACCELERATEUR, wait);
    }

    @Override
    public void ascenseurAccelerateurDepose(final boolean wait) {
        servosService.ascenseurGauche(IConstantesServos.ASCENSEUR_GAUCHE_ACCELERATEUR_DEPOSE, wait);
    }

    @Override
    public void ascenseurGold(boolean wait) {
        servosService.ascenseurGauche(IConstantesServos.ASCENSEUR_GAUCHE_GOLD, wait);
    }

    @Override
    public void ascenseurCarousel(boolean wait) {
        servosService.ascenseurGauche(IConstantesServos.ASCENSEUR_GAUCHE_CAROUSEL, wait);
    }

    @Override
    public void ascenseurCarouselDepose(boolean wait) {
        servosService.ascenseurGauche(IConstantesServos.ASCENSEUR_GAUCHE_CAROUSEL_DEPOSE, wait);
    }

    @Override
    public void pivotVentouseTable(boolean wait) {
        servosService.pivotVentouseGauche(IConstantesServos.PIVOT_VENTOUSE_GAUCHE_TABLE, wait);
    }

    @Override
    public void pivotVentouseFacade(boolean wait) {
        servosService.pivotVentouseGauche(IConstantesServos.PIVOT_VENTOUSE_GAUCHE_FACADE, wait);
    }

    @Override
    public void pivotVentouseCarouselVertical(boolean wait) {
        servosService.pivotVentouseGauche(IConstantesServos.PIVOT_VENTOUSE_GAUCHE_CAROUSEL_VERTICAL, wait);
    }

    @Override
    public void pivotVentouseCarouselSortie(boolean wait) {
        servosService.pivotVentouseGauche(IConstantesServos.PIVOT_VENTOUSE_GAUCHE_CAROUSEL_VERTICAL, wait);
    }

    @Override
    public void pinceSerrageRepos(boolean wait) {
        servosService.pinceSerragePaletGauche(IConstantesServos.PINCE_SERRAGE_PALET_GAUCHE_REPOS, wait);
    }

    @Override
    public void pinceSerrageLock(boolean wait) {
        servosService.pinceSerragePaletGauche(IConstantesServos.PINCE_SERRAGE_PALET_GAUCHE_LOCK, wait);
    }

    @Override
    public void porteBarilletFerme(boolean wait) {
        servosService.porteBarilletGauche(IConstantesServos.PORTE_BARILLET_GAUCHE_FERME, wait);
    }

    @Override
    public void porteBarilletOuvert(boolean wait) {
        servosService.porteBarilletGauche(IConstantesServos.PORTE_BARILLET_GAUCHE_OUVERT, wait);
    }

    @Override
    public void pousseAccelerateurFerme(boolean wait) {
        servosService.pousseAccelerateurGauche(IConstantesServos.POUSSE_ACCELERATEUR_GAUCHE_FERME, wait);
    }

    @Override
    public void pousseAccelerateurStandby(boolean wait) {
        servosService.pousseAccelerateurGauche(IConstantesServos.POUSSE_ACCELERATEUR_GAUCHE_STANDBY, wait);
    }

    @Override
    public void pousseAccelerateurAction(boolean wait) {
        servosService.pousseAccelerateurGauche(IConstantesServos.POUSSE_ACCELERATEUR_GAUCHE_ACTION, wait);
    }

    @Override
    public void ejectionMagasinFerme(boolean wait) {
        servosService.ejectionMagasinGauche(IConstantesServos.EJECTION_MAGASIN_GAUCHE_FERME, wait);
    }

    @Override
    public void ejectionMagasinOuvert(boolean wait) {
        servosService.ejectionMagasinGauche(IConstantesServos.EJECTION_MAGASIN_GAUCHE_OUVERT, wait);
    }

    @Override
    public void trappeMagasinFerme(boolean wait) {
        servosService.trappeMagasinGauche(IConstantesServos.TRAPPE_MAGASIN_GAUCHE_FERME, wait);
    }

    @Override
    public void trappeMagasinOuvert(boolean wait) {
        servosService.trappeMagasinGauche(IConstantesServos.TRAPPE_MAGASIN_GAUCHE_OUVERT, wait);
    }
}

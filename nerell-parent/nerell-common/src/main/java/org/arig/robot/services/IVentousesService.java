package org.arig.robot.services;

import org.arig.robot.exceptions.CarouselNotAvailableException;
import org.arig.robot.exceptions.VentouseNotAvailableException;
import org.arig.robot.model.ESide;
import org.arig.robot.model.enums.CouleurPalet;

import java.util.concurrent.CompletableFuture;

public interface IVentousesService {
    boolean isWorking(ESide side);

    CouleurPalet getCouleur(ESide side);

    void waitAvailable(ESide side) throws VentouseNotAvailableException;

    boolean priseTable(CouleurPalet couleur, ESide side);

    CompletableFuture<Boolean> preparePriseDistributeur(ESide side);

    CompletableFuture<Boolean> priseDistributeur(CouleurPalet couleur, ESide side);

    CompletableFuture<Void> finishPriseDistributeur(boolean ok, ESide side);

    void preparePriseGoldenium(ESide side);

    boolean priseGoldenium(ESide side);

    CompletableFuture<Void> finishPriseGoldenium(boolean ok, ESide side);

    void prepareDeposeAccelerateur(ESide side);

    boolean preparePriseAccelerateur(ESide side);

    void pousseAccelerateur(ESide side);

    boolean priseAccelerateur(ESide side);

    boolean deposeAccelerateur(CouleurPalet couleur, ESide side) throws CarouselNotAvailableException;

    void finishDeposeAccelerateur(ESide side);

    boolean deposeBalance(CouleurPalet couleur, ESide side) throws CarouselNotAvailableException;

    CompletableFuture<Void> finishDepose(ESide side);

    boolean deposeGoldeniumTable(ESide side);

    CompletableFuture<Boolean> deposeTable(ESide side);

    void stockageCarousel(ESide side);

    void stockageCarouselMaisResteEnHaut(ESide side);

    void releaseSide(ESide side);
}

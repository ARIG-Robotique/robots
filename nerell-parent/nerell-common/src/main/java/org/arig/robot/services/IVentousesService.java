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

    CompletableFuture<Boolean> priseTable(CouleurPalet couleur, ESide side);

    CompletableFuture<Boolean> preparePriseDistributeur(ESide side);

    CompletableFuture<Boolean> priseDistributeur(CouleurPalet couleur, ESide side);

    CompletableFuture<Void> finishPriseDistributeur(boolean ok, ESide side);

    CompletableFuture<Void> preparePriseGoldenium(ESide side);

    CompletableFuture<Boolean> priseGoldenium(ESide side);

    CompletableFuture<Void> finishPriseGoldenium(boolean ok, ESide side);

    CompletableFuture<Void> prepareDeposeAccelerateur(ESide side);

    CompletableFuture<Boolean> preparePriseAccelerateur(ESide side);

    CompletableFuture<Void> pousseAccelerateur(ESide side);

    CompletableFuture<Boolean> priseAccelerateur(ESide side);

    CompletableFuture<Boolean> deposeAccelerateur(CouleurPalet couleur, ESide side) throws CarouselNotAvailableException;

    CompletableFuture<Void> finishDeposeAccelerateur(ESide side);

    CompletableFuture<Boolean> deposeBalance(CouleurPalet couleur, ESide side) throws CarouselNotAvailableException;

    CompletableFuture<Void> finishDepose(ESide side);

    CompletableFuture<Boolean> deposeGoldeniumTable(ESide side);

    CompletableFuture<Boolean> deposeTable(ESide side);

    CompletableFuture<Void> stockageCarousel(ESide side);

    CompletableFuture<Void> stockageCarouselMaisResteEnHaut(ESide side);
}

package org.arig.robot.services;

import org.arig.robot.exceptions.CarouselNotAvailableException;
import org.arig.robot.exceptions.VentouseNotAvailableException;
import org.arig.robot.model.ESide;
import org.arig.robot.model.enums.CouleurPalet;
import org.springframework.scheduling.annotation.Async;

import java.util.concurrent.CompletableFuture;

public interface IVentousesService {
    boolean isWorking(ESide side);

    CouleurPalet getCouleur(ESide side);

    void waitAvailable(ESide side) throws VentouseNotAvailableException;

    @Async
    CompletableFuture<Boolean> priseTable(CouleurPalet couleur, ESide side);

    @Async
    CompletableFuture<Boolean> preparePriseDistributeur(ESide side);

    @Async
    CompletableFuture<Boolean> priseDistributeur(CouleurPalet couleur, ESide side);

    @Async
    CompletableFuture<Void> finishPriseDistributeur(boolean ok, ESide side);

    @Async
    CompletableFuture<Void> preparePriseGoldenium(ESide side);

    @Async
    CompletableFuture<Boolean> priseGoldenium(ESide side);

    @Async
    CompletableFuture<Void> finishPriseGoldenium(boolean ok, ESide side);

    @Async
    CompletableFuture<Void> prepareDeposeAccelerateur(ESide side);

    @Async
    CompletableFuture<Boolean> preparePriseAccelerateur(ESide side);

    @Async
    CompletableFuture<Void> pousseAccelerateur(ESide side);

    @Async
    CompletableFuture<Boolean> priseAccelerateur(ESide side);

    @Async
    CompletableFuture<Boolean> deposeAccelerateur(CouleurPalet couleur, ESide side) throws CarouselNotAvailableException;

    @Async
    CompletableFuture<Void> finishDeposeAccelerateur(ESide side);

    @Async
    CompletableFuture<Boolean> deposeBalance(CouleurPalet couleur, ESide side) throws CarouselNotAvailableException;

    @Async
    CompletableFuture<Void> finishDepose(ESide side);

    @Async
    CompletableFuture<Boolean> deposeGoldeniumTable(ESide side);

    @Async
    CompletableFuture<Boolean> deposeTable(ESide side);

    @Async
    CompletableFuture<Void> stockageCarousel(ESide side);

    @Async
    CompletableFuture<Void> stockageCarouselMaisResteEnHaut(ESide side);
}

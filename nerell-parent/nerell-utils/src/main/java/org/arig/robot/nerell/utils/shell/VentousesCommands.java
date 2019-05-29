package org.arig.robot.nerell.utils.shell;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.exceptions.CarouselNotAvailableException;
import org.arig.robot.exceptions.VentouseNotAvailableException;
import org.arig.robot.model.ESide;
import org.arig.robot.model.enums.CouleurPalet;
import org.arig.robot.services.IIOService;
import org.arig.robot.services.IVentousesService;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;

import javax.validation.constraints.NotNull;

@Slf4j
@ShellComponent
@ShellCommandGroup("Ventouses")
@AllArgsConstructor
public class VentousesCommands {

    private final IIOService ioService;
    private final IVentousesService ventouses;

    @ShellMethodAvailability
    public Availability alimentationOk() {
        return ioService.auOk() && ioService.alimPuissance5VOk() && ioService.alimPuissance12VOk()
                ? Availability.available() : Availability.unavailable("Les alimentations ne sont pas bonnes");
    }

    @ShellMethod("Release côté")
    @SneakyThrows
    public void releaseSide(@NotNull ESide side) {
        ventouses.finishDepose(side);
    }

    @ShellMethod("Prise sur la table")
    @SneakyThrows
    public void priseTable(@NotNull ESide side) {
        if (ventouses.priseTable(CouleurPalet.INCONNU, side)) {
            log.info("Prise sur table côté {}", side.name());
            ventouses.stockageCarousel(side);
        } else {
            log.info("Echec de prise sur table côté {}", side.name());
            ventouses.finishDepose(side);
        }
    }

    @ShellMethod("Prise sur un distributeur")
    @SneakyThrows
    public void priseDistributeur(@NotNull ESide side) {
        ventouses.preparePriseDistributeur(side);
        ThreadUtils.sleep(5000);
        boolean ok = ventouses.priseDistributeur(CouleurPalet.ROUGE, side).get();
        ThreadUtils.sleep(5000);
        ventouses.finishPriseDistributeur(ok, side);
    }

    @ShellMethod("Prise du un accelerateur")
    @SneakyThrows
    public void priseAccelerateur(@NotNull ESide side) throws VentouseNotAvailableException {
        ventouses.preparePriseAccelerateur(side, side == ESide.GAUCHE ? ESide.DROITE : ESide.GAUCHE);
        ThreadUtils.sleep(5000);
        ventouses.priseAccelerateur(side);
        ventouses.stockageCarouselMaisResteEnHaut(side);
        ThreadUtils.sleep(5000);
        ventouses.finishDeposeAccelerateur(side, side == ESide.GAUCHE ? ESide.DROITE : ESide.GAUCHE);
    }

    @ShellMethod("Depose accelerateur")
    @SneakyThrows
    public void deposeAccelerateur(@NotNull ESide side) throws CarouselNotAvailableException {
        ventouses.prepareDeposeAccelerateur(side, side == ESide.GAUCHE ? ESide.DROITE : ESide.GAUCHE);
        ThreadUtils.sleep(5000);
        ventouses.deposeAccelerateur(CouleurPalet.ROUGE, side);
        ThreadUtils.sleep(5000);
        ventouses.finishDeposeAccelerateur(side, side == ESide.GAUCHE ? ESide.DROITE : ESide.GAUCHE);
    }

    @ShellMethod("Dépose sur la balance")
    @SneakyThrows
    public void deposeBalance(@NotNull ESide side) throws CarouselNotAvailableException {
        ventouses.deposeBalance(CouleurPalet.ROUGE, side);
        ThreadUtils.sleep(5000);
        ventouses.finishDepose(side);
    }

    @ShellMethod("Prise du goldenium")
    @SneakyThrows
    public void priseGoldenium(@NotNull ESide side) {
        ventouses.preparePriseGoldenium(side);
        ThreadUtils.sleep(5000);
        boolean ok = ventouses.priseGoldenium(side);
        ThreadUtils.sleep(5000);
        ventouses.finishPriseGoldenium(ok, side);
    }

    @ShellMethod("Dépose goldenium sur table")
    @SneakyThrows
    public void deposeGoldeniumTable(@NotNull ESide side) {
        ventouses.deposeGoldeniumTable(side);
        ThreadUtils.sleep(5000);
        ventouses.finishDepose(side);
    }
}

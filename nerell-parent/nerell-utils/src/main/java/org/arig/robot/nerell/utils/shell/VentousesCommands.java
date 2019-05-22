package org.arig.robot.nerell.utils.shell;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.exceptions.CarouselNotAvailableException;
import org.arig.robot.exceptions.VentouseNotAvailableException;
import org.arig.robot.model.ESide;
import org.arig.robot.model.enums.CouleurPalet;
import org.arig.robot.services.IIOService;
import org.arig.robot.services.VentousesService;
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
    private final VentousesService ventouses;

    @ShellMethodAvailability
    public Availability alimentationOk() {
        return ioService.auOk() && ioService.alimPuissance5VOk() && ioService.alimPuissance12VOk()
                ? Availability.available() : Availability.unavailable("Les alimentations ne sont pas bonnes");
    }

    @ShellMethod("Release côté")
    @SneakyThrows
    public void releaseSide(@NotNull ESide side) {
        ventouses.finishDepose(side).get();
    }

    @ShellMethod("Prise sur la table")
    @SneakyThrows
    public void priseTable(@NotNull ESide side) {
        if (ventouses.priseTable(CouleurPalet.INCONNU, side).get()) {
            log.info("Prise sur table côté {}", side.name());
            ventouses.stockageCarousel(side).get();
        } else {
            log.info("Echec de prise sur table côté {}", side.name());
            ventouses.finishDepose(side).get();
        }
    }

    @ShellMethod("Prise sur un distributeur")
    @SneakyThrows
    public void priseDistributeur(@NotNull ESide side) {
        ventouses.preparePriseDistributeur(side).get();
        ThreadUtils.sleep(5000);
        boolean ok = ventouses.priseDistributeur(CouleurPalet.ROUGE, side).get();
        ThreadUtils.sleep(5000);
        ventouses.finishPriseDistributeur(ok, side).get();
    }

    @ShellMethod("Prise du un accelerateur")
    @SneakyThrows
    public void priseAccelerateur(@NotNull ESide side) throws VentouseNotAvailableException {
        ventouses.preparePriseAccelerateur(side).get();
        ThreadUtils.sleep(5000);
        ventouses.priseAccelerateur(side).get();
        ventouses.stockageCarouselMaisResteEnHaut(side).get();
        ThreadUtils.sleep(5000);
        ventouses.finishDeposeAccelerateur(side).get();
    }

    @ShellMethod("Depose accelerateur")
    @SneakyThrows
    public void deposeAccelerateur(@NotNull ESide side) throws CarouselNotAvailableException {
        ventouses.prepareDeposeAccelerateur(side).get();
        ThreadUtils.sleep(5000);
        ventouses.deposeAccelerateur(CouleurPalet.ROUGE, side).get();
        ThreadUtils.sleep(5000);
        ventouses.finishDeposeAccelerateur(side).get();
    }

    @ShellMethod("Dépose sur la balance")
    @SneakyThrows
    public void deposeBalance(@NotNull ESide side) throws CarouselNotAvailableException {
        ventouses.deposeBalance1(CouleurPalet.ROUGE, side).get();
        ThreadUtils.sleep(5000);
        ventouses.deposeBalance2(side).get();
        ThreadUtils.sleep(5000);
        ventouses.finishDepose(side).get();
    }

    @ShellMethod("Prise du goldenium")
    @SneakyThrows
    public void priseGoldenium(@NotNull ESide side) {
        ventouses.preparePriseGoldenium(side).get();
        ThreadUtils.sleep(5000);
        boolean ok = ventouses.priseGoldenium(side).get();
        ThreadUtils.sleep(5000);
        ventouses.finishPriseGoldenium(ok, side).get();
    }

    @ShellMethod("Dépose goldenium sur table")
    @SneakyThrows
    public void deposeGoldeniumTable(@NotNull ESide side) {
        ventouses.deposeGoldeniumTable(side).get();
        ThreadUtils.sleep(5000);
        ventouses.finishDepose(side).get();
    }
}

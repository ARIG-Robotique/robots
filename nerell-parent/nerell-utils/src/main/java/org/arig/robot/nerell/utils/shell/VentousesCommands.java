package org.arig.robot.nerell.utils.shell;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.exceptions.CarouselNotAvailableException;
import org.arig.robot.exceptions.VentouseNotAvailableException;
import org.arig.robot.model.ESide;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.model.enums.CouleurPalet;
import org.arig.robot.services.IIOService;
import org.arig.robot.services.SerrageService;
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

    private final VentousesService ventouses;
    private final SerrageService serrageService;
    private final IIOService ioService;
    private final RobotStatus rs;

    @ShellMethodAvailability
    public Availability alimentationOk() {
        return ioService.auOk() && ioService.alimPuissance5VOk() && ioService.alimPuissance12VOk()
                ? Availability.available() : Availability.unavailable("Les alimentations ne sont pas bonnes");
    }

    @ShellMethod("Release côté")
    public void releaseSide(@NotNull ESide side) {
        ventouses.finishDeposeAsync(side);
    }

    @ShellMethod("Prise sur la table")
    public void priseTable(@NotNull ESide side) {
        if (ventouses.priseTable(CouleurPalet.INCONNU, side)) {
            log.info("Prise sur table côté {}", side.name());
            ventouses.stockageAsync(side);
        } else {
            log.info("Echec de prise sur table côté {}", side.name());
            ventouses.finishDeposeAsync(side);
        }
    }

    @ShellMethod("Prise sur un distributeur")
    public void priseDistributeur(@NotNull ESide side) {
        ventouses.preparePriseDistributeur(side);
        ThreadUtils.sleep(5000);
        boolean ok = ventouses.priseDistributeur(CouleurPalet.ROUGE, side);
        ThreadUtils.sleep(5000);
        ventouses.finishPriseDistributeurAsync(ok, side);
    }

    @ShellMethod("Prise du un accelerateur")
    public void priseAccelerateur(@NotNull ESide side) throws VentouseNotAvailableException {
        ventouses.preparePriseAccelerateur(side);
        ThreadUtils.sleep(5000);
        ventouses.priseAccelerateur(side);
        ventouses.stockageAsyncMaisResteEnHaut(side);
        ventouses.waitAvailable(side);
        ThreadUtils.sleep(5000);
        ventouses.finishDeposeAccelerateurAsync(side);
    }

    @ShellMethod("Depose accelerateur")
    public void deposeAccelerateur(@NotNull ESide side) throws CarouselNotAvailableException {
        ventouses.prepareDeposeAccelerateur(side);
        ThreadUtils.sleep(5000);
        ventouses.deposeAccelerateur(CouleurPalet.ROUGE, side);
        ThreadUtils.sleep(5000);
        ventouses.finishDeposeAccelerateurAsync(side);
    }

    @ShellMethod("Dépose sur la balance")
    public void deposeBalance(@NotNull ESide side) throws CarouselNotAvailableException {
        ventouses.deposeBalance1(CouleurPalet.ROUGE, side);
        ThreadUtils.sleep(5000);
        ventouses.deposeBalance2(side);
        ThreadUtils.sleep(5000);
        ventouses.finishDeposeAsync(side);
    }

    @ShellMethod("Prise du goldenium")
    public void priseGoldenium(@NotNull ESide side) {
        ventouses.preparePriseGoldenium(side);
        ThreadUtils.sleep(5000);
        boolean ok = ventouses.priseGoldenium(side);
        ThreadUtils.sleep(5000);
        ventouses.finishPriseGoldeniumAsync(ok, side);
    }

    @ShellMethod("Dépose goldenium sur table")
    public void deposeGoldeniumTable(@NotNull ESide side) {
        rs.disableSerrage();
        ventouses.deposeGoldenimTable(side);
        ThreadUtils.sleep(5000);
        rs.enableSerrage();
        ventouses.finishDeposeAsync(side);
    }
}

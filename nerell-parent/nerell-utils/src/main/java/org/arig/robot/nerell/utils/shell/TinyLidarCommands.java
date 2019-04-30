package org.arig.robot.nerell.utils.shell;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.ESide;
import org.arig.robot.model.lidar.Scan;
import org.arig.robot.model.lidar.ScanInfos;
import org.arig.robot.services.IIOService;
import org.arig.robot.system.capteurs.ILidarTelemeter;
import org.arig.robot.system.capteurs.TinyLidar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Slf4j
@ShellComponent
@ShellCommandGroup("tinyLidar")
@AllArgsConstructor
public class TinyLidarCommands {

    private final IIOService ioService;
    private final TinyLidar distanceAvantDroit;
    private final TinyLidar distanceAvantGauche;

    @ShellMethod("Contenu du magasin arrière")
    public void stockMagasin(@NotNull ESide side) {
        byte nb = side.equals(ESide.DROITE) ? ioService.nbPaletDansMagasinDroit() : ioService.nbPaletDansMagasinGauche();
        log.info("Nombre d'élément dans le magasin {} : {}", side.name(), nb);
    }

    @ShellMethod("Distance tinyLidar facade")
    public void distanceFacade(@NotNull ESide side) {
        Integer distance = side.equals(ESide.DROITE) ? distanceAvantDroit.readValue() : distanceAvantGauche.readValue();
        log.info("Distance avant {} : {}mm", side.name(), distance);
    }
}

package org.arig.robot.nerell.utils.shell.commands;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.services.NerellIOServiceRobot;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@Slf4j
@ShellComponent
@ShellCommandGroup("IO")
@RequiredArgsConstructor
public class NerellIOCommands {

  private final NerellIOServiceRobot nerellIOServiceRobot;

  @ShellMethod("Read all IOs")
  public void readAllIO() {
    log.info("AU = {}", nerellIOServiceRobot.auOk());
    log.info("Tirette = {}", nerellIOServiceRobot.tirette());
    log.info("================== FIXE ==================");
    log.info("Calage avant gauche = {}", nerellIOServiceRobot.calageAvantGauche());
    log.info("Calage avant droit = {}", nerellIOServiceRobot.calageAvantDroit());
    log.info("Calage arriere gauche = {}", nerellIOServiceRobot.calageArriereGauche());
    log.info("Calage arriere droit = {}", nerellIOServiceRobot.calageArriereDroit());
    log.info("Stock avant gauche = {}", nerellIOServiceRobot.solAvantGauche(false));
    log.info("Stock avant droit = {}", nerellIOServiceRobot.solAvantDroite(false));
    log.info("Stock arriere gauche = {}", nerellIOServiceRobot.solArriereGauche(false));
    log.info("Stock arriere droit = {}", nerellIOServiceRobot.solArriereDroite(false));
    log.info("================= MOBILE =================");
    log.info("Pince avant gauche = {}", nerellIOServiceRobot.pinceAvantGauche(false));
    log.info("Pince avant droite = {}", nerellIOServiceRobot.pinceAvantDroite(false));
    log.info("Pince arriere gauche = {}", nerellIOServiceRobot.pinceArriereGauche(false));
    log.info("Pince arriere droite = {}", nerellIOServiceRobot.pinceArriereDroite(false));
    log.info("Tiroir avant haut = {}", nerellIOServiceRobot.tiroirAvantHaut(false));
    log.info("Tiroir avant bas = {}", nerellIOServiceRobot.tiroirAvantBas(false));
    log.info("Tiroir arriere haut = {}", nerellIOServiceRobot.tiroirArriereHaut(false));
    log.info("Tiroir arriere bas = {}", nerellIOServiceRobot.tiroirArriereBas(false));
  }
}

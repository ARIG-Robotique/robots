package org.arig.robot.nerell.utils.shell;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;

@Slf4j
@ShellComponent
@ShellCommandGroup("Actions")
@AllArgsConstructor
public class ActionsCommands {

//    private IIOService ioService;
//    private Ordonanceur ordonanceur;
//
//    private PrendreAtomesDepart prendreAtomesDepart;
//    private PrendrePetitDistributeurEquipe prendrePetitDistributeurEquipe;
//    private PrendreTrouNoirEquipe prendreTrouNoirEquipe;
//    private DeposeAccelerateur deposeAccelerateur;
//    private DeposerBalance deposerBalance;
//    private PrendreGoldenium prendreGoldenium;
//    private PrendreGrandDistributeurEquipe1 prendreGrandDistributeurEquipe1;
//    private PrendreGrandDistributeurEquipe2 prendreGrandDistributeurEquipe2;
//    private PrendreGrandDistributeurEquipe3 prendreGrandDistributeurEquipe3;
//
//    @ShellMethodAvailability
//    public Availability alimentationOk() {
//        return ioService.auOk() && ioService.alimPuissance5VOk() && ioService.alimPuissance12VOk()
//                ? Availability.available() : Availability.unavailable("Les alimentations ne sont pas bonnes");
//    }
//
//    @ShellMethod
//    @SneakyThrows
//    public void callageBordure() {
//        ordonanceur.callageBordure();
//    }
//
//    @ShellMethod
//    public void priseAtomesDepart() {
//        execute(prendreAtomesDepart);
//    }
//
//    @ShellMethod
//    public void priseTrouNoirEquipe() {
//        execute(prendreTrouNoirEquipe);
//    }
//
//    @ShellMethod
//    public void prisePetitDistributeur() {
//        execute(prendrePetitDistributeurEquipe);
//    }
//
//    @ShellMethod
//    public void deposeAccelerateur() {
//        execute(deposeAccelerateur);
//    }
//
//    @ShellMethod
//    public void deposeBalance() {
//        execute(deposerBalance);
//    }
//
//    @ShellMethod
//    public void priseGoldenium() {
//        execute(prendreGoldenium);
//    }
//
//    @ShellMethod
//    public void priseGrandDistributeurEquipe(@NotNull Integer index) {
//        switch (index) {
//            case 1:
//                execute(prendreGrandDistributeurEquipe1);
//                break;
//            case 2:
//                execute(prendreGrandDistributeurEquipe2);
//                break;
//            case 3:
//                execute(prendreGrandDistributeurEquipe3);
//                break;
//            default:
//                log.warn("Index invalide");
//        }
//    }
//
//    private void execute(AbstractAction action) {
//        log.info("Execution de l'action {}, ordre {}, valide {}", action.name(), action.order(), action.isValid());
//        action.execute();
//    }

}

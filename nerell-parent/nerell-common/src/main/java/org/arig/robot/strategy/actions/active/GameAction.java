package org.arig.robot.strategy.actions.active;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.exception.RefreshPathFindingException;
import org.arig.robot.exceptions.EjectionModuleException;
import org.arig.robot.model.ModuleLunaire;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.model.Team;
import org.arig.robot.services.BrasService;
import org.arig.robot.services.EjectionModuleService;
import org.arig.robot.services.IIOService;
import org.arig.robot.services.ServosService;
import org.arig.robot.strategy.AbstractAction;
import org.arig.robot.system.ITrajectoryManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Random;

@Slf4j
@Component
public class GameAction extends AbstractAction {

    @Autowired
    private RobotStatus rs;

    @Autowired
    private IIOService ioService;

    @Autowired
    private EjectionModuleService ejectionModuleService;

    @Getter
    private boolean completed = false;

    private Random rand = new Random();

    @Override
    public String name() {
        return "Jeux Sogelink";
    }

    @Override
    public int order() {
        return 1;
    }

    @Override
    public boolean isValid() {
        return rs.nbModulesMagasin() == 3;
    }

    @Override
    public void execute() {
        try {
            rs.disableMagasin();

            while (rs.hasModuleDansMagasin()) {
                defineRandomTeam();
                ioService.teamColorLedRGB();
                ejectionModuleService.ejectionModule();
                Thread.sleep(10);
            }

        } catch (InterruptedException | EjectionModuleException e) {
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
            updateValidTime();

        } finally {
            rs.enableMagasin();
            ioService.clearColorLedRGB();
        }
    }

    private void defineRandomTeam() {
        int choix = rand.nextInt(3);
        Team t;
        switch (choix) {
            case 0: t = Team.BLEU;break;
            case 1: t = Team.JAUNE;break;
            default: t = Team.UNKNOWN;break;
        }

        log.info("Choix {} => Team {}", choix, t.name());
        rs.setTeam(t);
    }
}

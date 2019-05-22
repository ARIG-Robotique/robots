package org.arig.robot.strategy.actions.disabled.atomfactory;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.exception.RefreshPathFindingException;
import org.arig.robot.model.ESide;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.model.Team;
import org.arig.robot.services.IIOService;
import org.arig.robot.services.MagasinService;
import org.arig.robot.strategy.AbstractAction;
import org.arig.robot.system.TrajectoryManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DeposerMagasinTableau extends AbstractAction {

    @Autowired
    private IIOService io;

    @Autowired
    private RobotStatus rs;

    @Autowired
    private TrajectoryManager mv;

    @Autowired
    private MagasinService magasin;

    @Getter
    private boolean completed = false;

    @Override
    public String name() {
        return "Depose de palets dans le tableau depuis la magasin";
    }

    @Override
    public int order() {
        int points = io.nbPaletDansMagasinDroit() * 6 + io.nbPaletDansMagasinGauche() * 6;
        return points;
    }

    @Override
    public boolean isValid() {
        return isTimeValid() &&
                (io.nbPaletDansMagasinDroit() > 0 || io.nbPaletDansMagasinGauche() > 0);
    }

    @Override
    public void execute() {

        try {
            rs.enableAvoidance();

            // 30=marge de sécu
            double offset = IConstantesNerellConfig.dstArriere + rs.getNbDeposesTableau() * IConstantesNerellConfig.offsetTableau + 30;

            if (rs.getTeam() == Team.VIOLET) {
                mv.pathTo(2550 - offset, 1400);
                mv.gotoOrientationDeg(180);
            } else {
                mv.pathTo(offset, 1400);
                mv.gotoOrientationDeg(0);
            }

            magasin.startEjection(ESide.DROITE);
            magasin.startEjection(ESide.GAUCHE);

            mv.avanceMM(IConstantesNerellConfig.offsetTableau * 3);

            rs.transfertMagasinTableau();

        } catch (NoPathFoundException | AvoidingException | RefreshPathFindingException e) {
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
            updateValidTime();
        }
    }
}

package org.arig.robot.strategy.actions.active;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.model.Point;
import org.arig.robot.model.RobotName;
import org.arig.robot.model.StatutDistributeur;
import org.arig.robot.model.Strategy;
import org.arig.robot.model.Team;
import org.arig.robot.model.enums.GotoOption;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class DistributeurCommunAdverse extends AbstractDistributeurCommun {

    private boolean firstTry = true;

    @Override
    public String name() {
        return EurobotConfig.ACTION_PRISE_DISTRIB_COMMUN_ADVERSE;
    }

    @Override
    public int order() {
        if (rs.strategy() == Strategy.FINALE_1 && robotName.id() == RobotName.RobotIdentification.NERELL && firstTry) {
            return 1000;
        } else {
            return super.order();
        }
    }

    @Override
    public List<String> blockingActions() {
        return Collections.singletonList(EurobotConfig.ACTION_PRISE_DISTRIB_COMMUN_EQUIPE);
    }

    @Override
    public int executionTimeMs() {
        return super.executionTimeMs() + 2000; // il est plus loin
    }

    @Override
    protected boolean isDistributeurDispo() {
        return rs.distributeurCommunAdverseDispo();
    }

    @Override
    protected boolean isDistributeurTermine() {
        return rs.distributeurCommunAdverseTermine();
    }

    @Override
    protected void setDistributeurPris() {
        group.distributeurCommunAdverse(StatutDistributeur.PRIS_NOUS);
    }

    @Override
    protected void setDistributeurBloque() {
        group.distributeurCommunAdverse(StatutDistributeur.BLOQUE);
    }

    @Override
    public Point entryPoint() {
        return new Point(3000 - getX(ENTRY_X), ENTRY_Y);
    }

    @Override
    protected int angleCallageX() {
        return rs.team() == Team.JAUNE ? 180 : 0;
    }

    @Override
    protected int anglePrise() {
        return rs.team() == Team.JAUNE ? 95 : 85;
    }

    @Override
    public void execute() {
        if (rs.strategy() == Strategy.FINALE_1 && robotName.id() == RobotName.RobotIdentification.NERELL && firstTry) {
            try {
                mv.setVitesse(config.vitesse(), config.vitesseOrientation());
                rs.enableAvoidance();
                mv.gotoPoint(getX(750), 1550, GotoOption.SANS_ARRET, GotoOption.SANS_ORIENTATION);
                mv.gotoPoint(3000 - getX(ENTRY_X), 1480, GotoOption.SANS_ORIENTATION);
                mv.gotoPoint(entryPoint(), GotoOption.ARRIERE);
                doExecute();

            } catch (AvoidingException e) {
                log.error("Erreur d'approche FINALE de l'action : {}", e.toString());
                updateValidTime();
            } finally {
                firstTry = false;
            }

        } else {
            super.execute();
        }
    }
}

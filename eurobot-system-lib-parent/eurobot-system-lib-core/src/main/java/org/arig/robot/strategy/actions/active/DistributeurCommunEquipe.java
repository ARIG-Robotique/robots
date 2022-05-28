package org.arig.robot.strategy.actions.active;

import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.model.Galerie;
import org.arig.robot.model.Point;
import org.arig.robot.model.RobotName;
import org.arig.robot.model.StatutDistributeur;
import org.arig.robot.model.Strategy;
import org.arig.robot.model.Team;
import org.springframework.stereotype.Component;

import java.awt.geom.Rectangle2D;
import java.util.Collections;
import java.util.List;

@Component
public class DistributeurCommunEquipe extends AbstractDistributeurCommun {

    private boolean firstTry = true;

    @Override
    public String name() {
        return EurobotConfig.ACTION_PRISE_DISTRIB_COMMUN_EQUIPE;
    }

    @Override
    public List<String> blockingActions() {
        return Collections.singletonList(EurobotConfig.ACTION_PRISE_DISTRIB_COMMUN_ADVERSE);
    }

    @Override
    public boolean isValid() {
        return super.isValid() && rs.periodeGalerieAutreRobot() != Galerie.Periode.ROUGE;
    }

    @Override
    public int order() {
        if (rs.strategy() == Strategy.BASIC && (
                (robotName.id() == RobotName.RobotIdentification.NERELL) || (!rs.twoRobots() && robotName.id() == RobotName.RobotIdentification.ODIN)
        )) {
            // Si c'est Nerell et que la strat est la basique.
            // Ou si c'est Odin et qu'il n'y a qu'un seul robot en strat basique.
            // C'est la seconde action
            return 500;
        }
        // stragégie finale, deuxième action
        if (rs.strategy() == Strategy.FINALE_1 && robotName.id() == RobotName.RobotIdentification.NERELL && firstTry) {
            return 500;
        }
        return super.order() + 3; // 3 points par prise
    }

    @Override
    protected boolean isDistributeurDispo() {
        return rs.distributeurCommunEquipeDispo();
    }

    @Override
    protected boolean isDistributeurTermine() {
        return rs.distributeurCommunEquipeTermine();
    }

    @Override
    protected void setDistributeurPris() {
        group.distributeurCommunEquipe(StatutDistributeur.PRIS_NOUS);
    }

    @Override
    protected void setDistributeurBloque() {
        group.distributeurCommunEquipe(StatutDistributeur.BLOQUE);
    }

    @Override
    public Point entryPoint() {
        return new Point(getX(ENTRY_X), ENTRY_Y);
    }

    @Override
    protected int angleCallageX() {
        return rs.team() == Team.JAUNE ? 0 : 180;
    }

    @Override
    protected int anglePrise() {
        return rs.team() == Team.JAUNE ? 85 : 95;
    }

    @Override
    public void execute() {
        tableUtils.addDynamicDeadZone(new Rectangle2D.Double(
                rs.team() == Team.JAUNE ? 1500 : 1200, 1700,
                300, 300
        ));

        super.execute();
    }
}

package org.arig.robot.strategy.actions.disabled.atomfactory;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.exception.NoPathFoundException;
import org.arig.robot.model.*;
import org.arig.robot.model.enums.CouleurPalet;
import org.arig.robot.services.BaliseService;
import org.arig.robot.strategy.AbstractAction;
import org.arig.robot.system.CarouselManager;
import org.arig.robot.system.TrajectoryManager;
import org.arig.robot.utils.ConvertionRobotUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class PrendreTable extends AbstractAction {

    @Autowired
    private TrajectoryManager mv;

    @Autowired
    private RobotStatus rs;

    @Autowired
    private BaliseService balise;

    @Autowired
    private CarouselManager carousel;

    @Autowired
    @Qualifier("currentPosition")
    private Position currentPosition;

    @Autowired
    private ConvertionRobotUnit conv;

    @Override
    public String name() {
        return "Prise des palets sur la table";
    }

    @Getter
    private boolean completed = false;

    @Override
    public int order() {
        return (int) Math.min(carousel.count(null), balise.nbAtomes()) * 2;
    }

    @Override
    public boolean isValid() {
        return rs.strategyActive(EStrategy.PRIS_TABLE) && rs.isBaliseOk() && canStore();
    }

    private boolean canStore() {
        return balise.hasAtomesSurTable() && carousel.has(null);
    }

    @Override
    public void execute() {
        try {
            rs.enableAvoidance();

            mv.setVitesse(IConstantesNerellConfig.vitessePath, IConstantesNerellConfig.vitesseOrientation);

            while (canStore()) {
                Point atome = getClosest();

                mv.pathTo(atome.getX(), atome.getY());
            }

        } catch (NoPathFoundException | AvoidingException e) {
            log.error("Erreur d'éxécution de l'action : {}", e.toString());
            updateValidTime();
        }
    }

    private Point getClosest() {
        List<Pair<CouleurPalet, Point>> atomes = balise.getAtomes();

        Point position = new Point(
                conv.pulseToMm(currentPosition.getPt().getX()),
                conv.pulseToMm(currentPosition.getPt().getY())
        );

        return atomes.stream()
                .filter(pt -> {
                    // ignore les points "sous" le robot, si la balise n'a pas mis à jour
                    return pt.getRight().distance(position) > 200;
                })
                .min((a, b) -> {
                    double dstA = a.getRight().distance(position);
                    double dstB = b.getRight().distance(position);
                    double valA = dstA / a.getLeft().getImportance();
                    double valB = dstB / b.getLeft().getImportance();

                    if (rs.getTeam() == Team.JAUNE) {
                        if (a.getRight().getX() > 1500) {
                            valA /= 2;
                        }
                        if (b.getRight().getX() > 1500) {
                            valB /= 2;
                        }
                    } else {
                        if (a.getRight().getX() < 1500) {
                            valA /= 2;
                        }
                        if (b.getRight().getX() < 1500) {
                            valB /= 2;
                        }
                    }

                    return (int) Math.round(valB - valA);
                })
                .map(Pair::getRight)
                .get();
    }
}

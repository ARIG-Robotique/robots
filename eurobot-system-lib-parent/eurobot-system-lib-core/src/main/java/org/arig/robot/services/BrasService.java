package org.arig.robot.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Triple;
import org.arig.robot.model.Bras;
import org.arig.robot.model.EurobotStatus;
import org.arig.robot.model.RobotConfig;
import org.arig.robot.model.bras.AnglesBras;
import org.arig.robot.model.bras.ConfigBras;
import org.arig.robot.model.bras.CurrentBras;
import org.arig.robot.model.bras.PointBras;
import org.arig.robot.model.bras.PositionBras;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import static org.arig.robot.services.AbstractCommonRobotServosService.*;

@Slf4j
public class BrasService {

    private final RobotConfig config;
    private final CommonRobotIOService io;
    private final AbstractCommonRobotServosService servos;
    private final ThreadPoolExecutor executor;
    private final EurobotStatus rs;

    @AllArgsConstructor
    public static class FullConfigBras {
        public ConfigBras config;
        public Set<PositionBras> states;
    }

    private final Map<Bras, BrasInstance> bras;

    public BrasService(final AbstractCommonRobotServosService servos,
                       final ThreadPoolExecutor executor,
                       final RobotConfig config,
                       final EurobotStatus rs,
                       final CommonRobotIOService io) {
        this.servos = servos;
        this.config = config;
        this.io = io;
        this.executor = executor;
        this.rs = rs;

        this.bras = Map.of(
                Bras.AVANT_GAUCHE, new BrasInstance("Avant gauche", false, servos::brasAvantGauche),
                Bras.AVANT_CENTRE, new BrasInstance("Avant centre", false, servos::brasAvantCentre),
                Bras.AVANT_DROIT, new BrasInstance("Avant droit", false, servos::brasAvantDroit),
                Bras.ARRIERE_GAUCHE, new BrasInstance("Arrière gauche", true, servos::brasArriereGauche),
                Bras.ARRIERE_CENTRE, new BrasInstance("Arrière centre", true, servos::brasArriereCentre),
                Bras.ARRIERE_DROIT, new BrasInstance("Arrière droit", true, servos::brasArriereDroit)
        );
    }

    public Map<Bras, FullConfigBras> getConfig() {
        return bras.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> new FullConfigBras(e.getValue().config(), e.getValue().states())
                ));
    }

    public Map<Bras, CurrentBras> getCurrent() {
        return bras.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().current()
                ));
    }

    public AnglesBras calculerBras(Bras bras, PointBras pt) {
        return this.bras.get(bras).calculerAngles(pt.x, pt.y, pt.a, pt.invertA1, false);
    }

    public boolean setBras(Bras bras, PointBras pt, int speed, boolean wait) {
        return this.bras.get(bras).set(pt, speed, wait);
    }

    public void setBrasByName(Bras bras, PositionBras position, int speed, boolean wait) {
        this.bras.get(bras).setByName(position, speed, wait);
    }

    public void setBrasAvant(PositionBras positionBras) {
        setBrasByName(Bras.AVANT_GAUCHE, positionBras, 40, false);
        setBrasByName(Bras.AVANT_CENTRE, positionBras, 40, false);
        setBrasByName(Bras.AVANT_DROIT, positionBras, 40, true);
    }

    public void setBrasArriere(PositionBras positionBras) {
        setBrasByName(Bras.ARRIERE_GAUCHE, positionBras, 40, false);
        setBrasByName(Bras.ARRIERE_CENTRE, positionBras, 40, false);
        setBrasByName(Bras.ARRIERE_DROIT, positionBras, 40, true);
    }

    public void setBrasAvant(PointBras pointBras) {
        setBras(Bras.AVANT_GAUCHE, pointBras, 30, false);
        setBras(Bras.AVANT_CENTRE, pointBras, 30, false);
        setBras(Bras.AVANT_DROIT, pointBras, 30, true);
    }

    public void setBrasArriere(PointBras pointBras) {
        setBras(Bras.ARRIERE_GAUCHE, pointBras, 30, false);
        setBras(Bras.ARRIERE_CENTRE, pointBras, 30, false);
        setBras(Bras.ARRIERE_DROIT, pointBras, 30, true);
    }

    public void brasAvantStockage() {
        servos.setPositionsRaw(
                Map.of(
                        BRAS_AVANT_GAUCHE_EPAULE, 2365, BRAS_AVANT_GAUCHE_COUDE, 2445, BRAS_AVANT_GAUCHE_POIGNET, 1735,
                        BRAS_AVANT_CENTRE_EPAULE, 2380, BRAS_AVANT_CENTRE_COUDE, 545, BRAS_AVANT_CENTRE_POIGNET, 1015,
                        BRAS_AVANT_DROIT_EPAULE, 2390, BRAS_AVANT_DROIT_COUDE, 2510, BRAS_AVANT_DROIT_POIGNET, 1960
                ),
                50,
                true
        );

        servos.setPositionsRaw(
                Map.of(
                        BRAS_AVANT_GAUCHE_EPAULE, 1935, BRAS_AVANT_GAUCHE_COUDE, 2445, BRAS_AVANT_GAUCHE_POIGNET, 1735,
                        BRAS_AVANT_CENTRE_EPAULE, 1910, BRAS_AVANT_CENTRE_COUDE, 545, BRAS_AVANT_CENTRE_POIGNET, 1015,
                        BRAS_AVANT_DROIT_EPAULE, 1940, BRAS_AVANT_DROIT_COUDE, 2510, BRAS_AVANT_DROIT_POIGNET, 1960
                ),
                20,
                true
        );

        servos.setPositionsRaw(
                Map.of(
                        BRAS_AVANT_GAUCHE_EPAULE, 1815, BRAS_AVANT_GAUCHE_COUDE, 2415, BRAS_AVANT_GAUCHE_POIGNET, 1595,
                        BRAS_AVANT_CENTRE_EPAULE, 1790, BRAS_AVANT_CENTRE_COUDE, 575, BRAS_AVANT_CENTRE_POIGNET, 1125,
                        BRAS_AVANT_DROIT_EPAULE, 1820, BRAS_AVANT_DROIT_COUDE, 2480, BRAS_AVANT_DROIT_POIGNET, 1780
                ),
                20,
                true
        );

        servos.groupePinceAvantOuvert(true);

        servos.setPositionsRaw(
                Map.of(
                        BRAS_AVANT_GAUCHE_EPAULE, 2215, BRAS_AVANT_GAUCHE_COUDE, 2415, BRAS_AVANT_GAUCHE_POIGNET, 1595,
                        BRAS_AVANT_CENTRE_EPAULE, 2190, BRAS_AVANT_CENTRE_COUDE, 575, BRAS_AVANT_CENTRE_POIGNET, 1125,
                        BRAS_AVANT_DROIT_EPAULE, 2220, BRAS_AVANT_DROIT_COUDE, 2480, BRAS_AVANT_DROIT_POIGNET, 1780
                ), 50,
                true
        );

        servos.groupePinceAvantFerme(false);
    }

}

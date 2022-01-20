package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.arig.robot.model.servos.Servo;
import org.arig.robot.model.servos.ServoGroup;
import org.arig.robot.model.servos.ServoPosition;
import org.arig.robot.system.servos.SD21Servos;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public abstract class AbstractServosService {

    @Autowired
    private SD21Servos ctrl;

    private Map<String, Servo> servos = new HashMap<>();
    private Map<String, ServoGroup> groups = new HashMap<>();

    protected Servo servo(int id, String name) {
        Servo servo = new Servo().id((byte) id).name(name);
        this.servos.put(name, servo);
        return servo;
    }

    protected Servo servo(String name) {
        return servos.get(name);
    }

    protected ServoGroup group(int id, String name) {
        ServoGroup group = new ServoGroup().id((byte) id).name(name);
        this.groups.put(name, group);
        return group;
    }

    protected ServoGroup group(String name) {
        return groups.get(name);
    }

    protected void logPositionServo(final String servoName, final String positionName, final boolean wait) {
        log.info("{} -> {}{}", servoName, positionName, wait ? " avec attente" : StringUtils.EMPTY);
    }

    public List<ServoGroup> getGroups() {
        servos.forEach((name, s) -> {
            s.currentPosition(ctrl.getPosition(s.id()));
            s.currentSpeed(ctrl.getSpeed(s.id()));
        });

        return groups.values().stream()
                .sorted(Comparator.comparing(ServoGroup::id))
                .collect(Collectors.toList());
    }

    /* **************************************** */
    /* Méthode pour le positionnement d'origine */
    /* **************************************** */

    public void cyclePreparation() {
        log.info("Servos enregistrés : ");
        servos.values().forEach(s -> {
            log.info(" * {} - {} ({} positions)", s.id(), s.name(), s.positions().size());
            s.positions().forEach((name, p) -> log.info("   * {} - {} (speed {})", name, p.value(), p.speed()));
        });

        log.info("Servos groupés : ");
        groups.values().forEach(g -> {
            log.info(" * {} - {} ({} servos)", g.id(), g.name(), g.servos().size());
            g.servos().forEach(s -> log.info("   * {}", s.name()));
        });

        log.info("Servos en position initiale");
        homes();
    }

    public abstract void homes();

    //*******************************************//
    //* Déplacements                            *//
    //*******************************************//

    public boolean isInPosition(String servoName, String positionName) {
        Servo servo = servos.get(servoName);
        assert servo != null;
        ServoPosition position = servo.positions().get(positionName);
        assert position != null;

        return position.value() == ctrl.getPosition(servo.id());
    }

    public void setPosition(String servoName, String positionName, boolean wait) {
        Servo servo = servos.get(servoName);
        assert servo != null;
        ServoPosition position = servo.positions().get(positionName);
        assert position != null;

        logPositionServo(servoName, positionName, wait);

        int currentPosition = ctrl.getPosition(servo.id());

        ctrl.setPositionAndSpeed(servo.id(), position.value(), position.speed());

        if (wait && currentPosition != position.value()) {
            if (position.speed() != 0) {
                ThreadUtils.sleep(servo.time()); // FIXME calcul intelligent du temps de mouvement avec vitesse
            } else {
                ThreadUtils.sleep(computeWaitTime(servo, currentPosition, position.value()));
            }
        }
    }

    public void setPositionBatch(String groupName, String positionName, boolean wait) {
        ServoGroup group = groups.get(groupName);
        assert group != null;
        assert group.batch().contains(positionName);

        logPositionServo(groupName, positionName, wait);

        int waitTime = 0;
        for (Servo servo : group.servos()) {
            ServoPosition position = servo.positions().get(positionName);
            if (position == null) {
                continue;
            }

            int currentPosition = ctrl.getPosition(servo.id());

            ctrl.setPositionAndSpeed(servo.id(), position.value(), position.speed());

            waitTime = Math.max(waitTime, computeWaitTime(servo, currentPosition, position.value()));
        }
    }

    protected int computeWaitTime(Servo servo, int currentPosition, int position) {
        double wait = servo.time() * Math.abs(position - currentPosition) / (servo.max() * 1. - servo.min());
        return (int) Math.round(wait);
    }
}

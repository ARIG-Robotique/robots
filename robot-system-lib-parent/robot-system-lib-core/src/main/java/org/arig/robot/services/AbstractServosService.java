package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.arig.robot.model.servos.Servo;
import org.arig.robot.model.servos.ServoGroup;
import org.arig.robot.model.servos.ServoPosition;
import org.arig.robot.system.servos.AbstractServos;
import org.arig.robot.utils.ThreadUtils;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public abstract class AbstractServosService {

    public record OffsetedDevice(AbstractServos servo, int offset) {}

    private final Map<Integer, OffsetedDevice> servoDevices = new HashMap<>();

    private final Map<String, Servo> servos = new HashMap<>();
    private final Map<String, ServoGroup> groups = new HashMap<>();

    protected AbstractServosService(AbstractServos servoDevice, AbstractServos ... servoDevices) {
        int nbServos = addServosDeviceMapping(servoDevice, (byte) 0);
        for (AbstractServos sd : servoDevices) {
            if (sd == servoDevice) {
                continue;
            }
            nbServos = addServosDeviceMapping(sd, nbServos);
        }
    }

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

    protected void logPositionGroupeServo(final String servoName,
                                          final String positionName,
                                          final boolean wait) {
        log.info("Groupe servo {} -> {}{}", servoName, positionName, wait ? " avec attente" : StringUtils.EMPTY);
    }

    protected void logPositionServo(final String servoName,
                                    final String positionName,
                                    final int value,
                                    final int speed,
                                    final boolean wait) {
        log.info("Servo {} -> {} ({}@{}){}", servoName, positionName, value, speed, wait ? " avec attente" : StringUtils.EMPTY);
    }

    public List<ServoGroup> getGroups() {
        servos.forEach((name, s) -> {
            OffsetedDevice device = getDevice(s.id());
            s.currentPosition(device.servo().getPosition((byte) (s.id() - device.offset())));
            s.currentSpeed(device.servo().getSpeed((byte) (s.id() - device.offset())));
        });

        return groups.values().stream()
                .sorted(Comparator.comparing(ServoGroup::id))
                .collect(Collectors.toList());
    }

    public OffsetedDevice getDevice(int id) {
        return servoDevices.get(id);
    }

    private int addServosDeviceMapping(AbstractServos sd, int nbServos) {
        log.info("Enregistrement des servos {} -> {} sur le controlleur {}", nbServos + 1, nbServos + sd.getNbServos(), sd.deviceName());
        for (byte i = 1; i <= sd.getNbServos(); i++) {
            this.servoDevices.put(nbServos + i, new OffsetedDevice(sd, nbServos));
        }
        return nbServos + sd.getNbServos();
    }

    /* **************************************** */
    /* Méthode pour le positionnement d'origine */
    /* **************************************** */

    public void cyclePreparation() {
        log.info("Servos enregistrés : ");
        servos.values().stream()
                .sorted(Comparator.comparing(Servo::name))
                .forEach(s -> {
                    log.info(" * {} - {} ({} positions)", s.id(), s.name(), s.positions().size());
                    s.positions().forEach((name, p) -> log.info("   * {} - {} (speed {})", name, p.value(), p.speed()));
                });

        log.info("Servos groupés : ");
        groups.values().stream()
                .sorted(Comparator.comparing(ServoGroup::name))
                .forEach(g -> {
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

    /**
     * Accès direct au device servo
     */
    public void setPositionById(byte id, int position, byte speed) {
        OffsetedDevice device = getDevice(id);
        device.servo().setPositionAndSpeed((byte) (id - device.offset()), position, speed);
    }

    public boolean isInPosition(String servoName, String positionName) {
        Servo servo = servos.get(servoName);
        assert servo != null;
        ServoPosition position = servo.positions().get(positionName);
        assert position != null;

        OffsetedDevice device = getDevice(servo.id());
        return position.value() == device.servo().getPosition((byte) (servo.id() - device.offset()));
    }

    /**
     * Déplace un servo à une position nommée
     */
    public void setPosition(String servoName, String positionName, boolean wait) {
        Servo servo = servos.get(servoName);
        assert servo != null;
        ServoPosition position = servo.positions().get(positionName);
        assert position != null;

        logPositionServo(servoName, positionName, position.value(), position.speed(), wait);
        setPosition(servo, position.value(), position.speed(), wait);
    }

    /**
     * Déplace un servo à un certain angle
     */
    public void setAngle(String servoName, int angle, int speed, boolean wait) {
        Servo servo = servos.get(servoName);
        assert servo != null;

        int targetPosition = servo.angleToPosition(angle);

        logPositionServo(servoName, angle + "°", targetPosition, speed, wait);
        setPosition(servo, targetPosition, (byte) speed, wait);
    }

    private void setPosition(Servo servo, int position, byte speed, boolean wait) {
        OffsetedDevice device = getDevice(servo.id());
        int currentPosition = device.servo().getPosition((byte) (servo.id() - device.offset()));

        device.servo().setPositionAndSpeed((byte) (servo.id() - device.offset()), position, speed);

        if (wait && currentPosition != position) {
            ThreadUtils.sleep(computeWaitTime(servo, currentPosition, position, speed));
        }
    }

    /**
     * Déplace plusieurs servos à différents angles, toujours avec attente
     */
    public void setAngles(Map<String, Double> servosAngles, int speed) {
        // calcul la distance maximal à parcourir
        int maxDst = 0;
        for (Map.Entry<String, Double> servoAngle : servosAngles.entrySet()) {
            Servo servo = servos.get(servoAngle.getKey());
            assert servo != null;

            OffsetedDevice device = getDevice(servo.id());
            double angle = servoAngle.getValue();
            int targetPosition = servo.angleToPosition(angle);
            int currentPosition = device.servo().getPosition((byte) (servo.id() - device.offset()));
            int dst = Math.abs(targetPosition - currentPosition);

            maxDst = Math.max(maxDst, dst);
        }

        int waitTime = 0;
        for (Map.Entry<String, Double> servoAngle : servosAngles.entrySet()) {
            Servo servo = servos.get(servoAngle.getKey());
            assert servo != null;

            OffsetedDevice device = getDevice(servo.id());
            double angle = servoAngle.getValue();
            int targetPosition = servo.angleToPosition(angle);
            int currentPosition = device.servo().getPosition((byte) (servo.id() - device.offset()));
            int dst = Math.abs(targetPosition - currentPosition);

            // la vitesse de chaque servo dépend de la distance à parcourir
            int finalSpeed = (int) Math.ceil(dst * 1.0 / maxDst * speed);

            //logPositionServo(servo.name(), angle + "°", targetPosition, finalSpeed, true);
            device.servo().setPositionAndSpeed((byte) (servo.id() - device.offset()), targetPosition, (byte) finalSpeed);

            if (currentPosition != targetPosition && finalSpeed > 0) {
                // finalSpeed peu tomber à zéro même si un (petit) mouvement est necessaire
                waitTime = Math.max(waitTime, computeWaitTime(servo, currentPosition, targetPosition, finalSpeed));
            }
        }

        ThreadUtils.sleep(waitTime);
    }

    /**
     * Déplace un groupe de servos à une position nommée
     */
    public void setPositionBatch(String groupName, String positionName, boolean wait) {
        ServoGroup group = groups.get(groupName);
        assert group != null;
        assert group.batch().contains(positionName);

        logPositionGroupeServo(groupName, positionName, wait);

        int waitTime = 0;
        for (Servo servo : group.servos()) {
            ServoPosition position = servo.positions().get(positionName);
            if (position == null) {
                continue;
            }

            OffsetedDevice device = getDevice(servo.id());
            int currentPosition = device.servo().getPosition((byte) (servo.id() - device.offset()));

            device.servo().setPositionAndSpeed((byte) (servo.id() - device.offset()), position.value(), position.speed());

            if (currentPosition != position.value()) {
                waitTime = Math.max(waitTime, computeWaitTime(servo, currentPosition, position.value(), position.speed()));
            }
        }

        if (wait) {
            ThreadUtils.sleep(waitTime);
        }
    }

    protected int computeWaitTime(Servo servo, int currentPosition, int position, int speed) {
        double wait;
        if (speed != 0) {
            // calcul d'après la datasheet SD21
            // FIXME rajouter un délai fixe ?
            wait = Math.abs(position - currentPosition) / (speed * 1.) * 25;
        } else {
            // calcul empirique
            wait = servo.time() * Math.abs(position - currentPosition) / (servo.max() * 1. - servo.min());
        }
        return (int) Math.round(wait);
    }
}

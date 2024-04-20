package org.arig.robot.model.servos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.arig.robot.utils.ArigUtils.lerp;

@Data
@ToString(doNotUseGetters = true)
@EqualsAndHashCode(doNotUseGetters = true)
@Accessors(chain = true, fluent = true)
public class Servo {

    @JsonProperty("id")
    private byte id;
    @JsonProperty("name")
    private String name;
    private int time;

    @JsonProperty("positions")
    private Map<String, ServoPosition> positions = new HashMap<>();
    private int min = 3000;
    private int max = 0;

    // pour les servos commandés en angle
    private boolean angular = false;
    private List<Pair<Integer, Integer>> angles;

    public Integer getAngleMin() {
        return angular ? angles.get(0).getKey() : null;
    }

    public Integer getAngleMax() {
        return angular ? angles.get(angles.size() - 1).getKey() : null;
    }

    // monitoring
    @JsonProperty("currentSpeed")
    private int currentSpeed;
    @JsonProperty("currentPosition")
    private int currentPosition;

    public int angleToPosition(double angle) {
        assert angular;

        final int l = angles.size();

        // find the correct range
        int index = -1;
        for (int i = 0; i < l - 1; i++) {
            if (angle >= angles.get(i).getKey() && angle <= angles.get(i+1).getKey()) {
                index = i;
                break;
            }
        }

        // out of bounds
        if (index == -1) {
            if (angle < angles.get(0).getKey()) {
                return angles.get(0).getValue();
            } else {
                return angles.get(l - 1).getValue();
            }
        }

        // apply lerp on range
        return (int) Math.round(
                lerp(angle,
                        angles.get(index).getKey(), angles.get(index + 1).getKey(),
                        angles.get(index).getValue(), angles.get(index + 1).getValue()
                )
        );
    }

    public Servo position(String name, int value) {
        return position(name, value, 0);
    }

    public Servo position(String name, int value, int speed) {
        positions.put(name, new ServoPosition().name(name).value(value).speed((byte) speed));
        min = Math.min(min, value);
        max = Math.max(max, value);
        return this;
    }

    public ServoAngular angular() {
        this.angular = true;
        this.angles = new ArrayList<>();
        return new ServoAngular();
    }

    public class ServoAngular {
        private boolean ordered;

        private void order() {
            if (!ordered) {
                assert Servo.this.angles.size() > 1;
                Servo.this.angles.sort(Comparator.comparingInt(Pair::getKey));
                ordered = true;
            }
        }

        public ServoAngular angle(int a, int pulses) {
            assert !ordered;
            Servo.this.angles.add(Pair.of(a, pulses));
            return this;
        }

        public ServoAngular position(String name, int angle, int speed) {
            order();
            Servo.this.position(name, Servo.this.angleToPosition(angle), speed);
            return this;
        }

        public Servo build() {
            order();
            return Servo.this;
        }
    }

}

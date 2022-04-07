package org.arig.robot.model.servos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;

@Data
@Accessors(chain = true, fluent = true)
public class Servo {

    public static final String POS_0DEG = "0°";
    public static final String POS_90DEG = "90°";
    public static final String POS_MIN90DEG = "-90°";

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
    private int center = 1500; // position du "0 degré"
    private double mult = 1; // direction de angles positifs / correction de dérive
    private int angleMin = -90;
    private int angleMax = 90;

    // monitoring
    @JsonProperty("currentSpeed")
    private int currentSpeed;
    @JsonProperty("currentPosition")
    private int currentPosition;

    public int angleToPosition(int angle) {
        return (int) Math.round(center + mult * angle * 10); // 0.1°/µsec
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
        return new ServoAngular();
    }

    public class ServoAngular {
        public ServoAngular center(int center) {
            Servo.this.center = center;
            return this;
        }

        public ServoAngular mult(double mult) {
            Servo.this.mult = mult;
            return this;
        }

        public ServoAngular angleMin(int angleMin) {
            Servo.this.angleMin = angleMin;
            return this;
        }

        public ServoAngular angleMax(int angleMax) {
            Servo.this.angleMax = angleMax;
            return this;
        }

        public ServoAngular position(String name, int angle, int speed) {
            Servo.this.position(name, Servo.this.angleToPosition(angle), speed);
            return this;
        }

        public Servo build() {
            Servo.this.position(POS_0DEG, Servo.this.center);
            if (Servo.this.angleMax >= 90) {
                this.position(POS_90DEG, 90, 0);
            }
            if (Servo.this.angleMin <= -90) {
                this.position(POS_MIN90DEG, -90, 0);
            }
            return Servo.this;
        }
    }

}

package org.arig.robot.model.balise;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.arig.robot.model.Point;
import org.arig.robot.model.TypePlante;

import java.io.Serializable;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class StatutBalise implements Serializable {

    public enum PresenceDistrib {
        PRESENT,
        ABSENT
    }

    @Getter
    @NoArgsConstructor
    public static class Echantillon implements Serializable {
        private TypePlante c;
        private int x;
        private int y;

        public Point getPoint() {
            double correctedY;
            if (y < 555) {
                correctedY = lerp(y, 190, 555, 170, 555);
            } else if (y > 795) {
                correctedY = lerp(y, 795, 1885, 795, 1925);
            } else {
                correctedY = y;
            }
            return new Point(x, 2000 - correctedY);
        }

        public double lerp(double val, double start_1, double end_1, double start_2, double end_2) {
            double lambda = (val - start_1) / (end_1 - start_1);
            return start_2 + lambda * (end_2 - start_2);
        }
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DetectionResult implements Serializable {
        private List<PresenceDistrib> distribs;
        private List<Echantillon> echantillons;
    }

    boolean etalonnageDone;

    DetectionResult detection;

    public boolean detectionOk() {
        return detection != null;
    }

}

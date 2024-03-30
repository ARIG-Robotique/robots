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
    public static class Plante implements Serializable {
        private TypePlante c;
        private int x;
        private int y;

        public Point getPoint() {
            return new Point(x, y);
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
        private List<Plante> plantes;
    }

    boolean etalonnageDone;

    DetectionResult detection;

    public boolean detectionOk() {
        return detection != null;
    }

}

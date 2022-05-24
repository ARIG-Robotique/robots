package org.arig.robot.model.balise;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.arig.robot.model.CouleurEchantillon;
import org.arig.robot.model.Point;

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
        private CouleurEchantillon c;
        private int x;
        private int y;

        public Point getPoint() {
            return new Point(x, 2000 - y);
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

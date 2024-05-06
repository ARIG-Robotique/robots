package org.arig.robot.model.ecran;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class AbstractEcranConfig implements Serializable {
    private Enum<?> team;
    private Enum<?> strategy;
    private boolean exit;
    private boolean twoRobots;
    private boolean safeAvoidance;
    private boolean startCalibration;
    private boolean modeManuel;
    private boolean skipCalageBordure;
    private boolean updatePhoto;
    private Map<String, Boolean> options;

    public boolean hasOption(String name) {
        return options.getOrDefault(name, false);
    }
}

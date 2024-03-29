package org.arig.robot.model.ecran;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class AbstractEcranState implements Serializable {
    private Enum<?> team;
    private Enum<?> strategy;
    private String message = "";
    private boolean au = false;
    private boolean alimMoteurs = false;
    private boolean alimServos = false;
    private boolean tirette = false;
    private boolean i2c = false;
    private boolean lidar = false;
    private boolean balise = false;
    private boolean otherRobot = false;
    private Map<String, Boolean> options;
}

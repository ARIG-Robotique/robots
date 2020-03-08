package org.arig.robot.model.ecran;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateStateInfos implements Serializable {
    String message = "";
    boolean au = false;
    boolean alim12v = false;
    boolean alim5vp = false;
    boolean tirette = false;
    boolean i2c = false;
    boolean lidar = false;
    boolean phare = false;
    boolean balise = false;
}

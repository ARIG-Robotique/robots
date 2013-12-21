package org.arig.prehistobot.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * Created by mythril on 21/12/13.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Capteur {

    private Integer id;

    private Boolean value;
}

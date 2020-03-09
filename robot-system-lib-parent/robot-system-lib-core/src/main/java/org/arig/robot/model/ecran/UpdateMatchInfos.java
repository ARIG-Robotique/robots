package org.arig.robot.model.ecran;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateMatchInfos implements Serializable {
    int score = 0;
    String message;
}

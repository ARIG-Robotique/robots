package org.arig.robot.model.ecran;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * Informations qui sont à la fois lues et écrites sur l'écran
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReadWriteInfo {
    int team = 0;
    int strategy = 0;
}

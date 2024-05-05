package org.arig.robot.model.ecran;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class EcranParams implements Serializable {

    private String name;
    private boolean primary;
    private boolean pami;
    private Map<String, String> teams;
    private List<String> strategies;
    private List<String> options;

}

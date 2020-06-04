package org.arig.robot.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(fluent = true)
@AllArgsConstructor
@RequiredArgsConstructor
public class Bouee implements Serializable {

    @JsonProperty("couleur")
    @Setter(value = AccessLevel.NONE)
    private final ECouleurBouee couleur;

    @JsonProperty("prise")
    private boolean prise = false;

    private final Point pt;
}

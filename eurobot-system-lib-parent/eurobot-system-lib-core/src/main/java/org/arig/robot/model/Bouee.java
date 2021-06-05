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

    public enum EState {
        PRESENTE, // etat initial + presente selon la balise
        ABSENTE, // absente selon la balise
        PRISE // prise par le robot
    }

    private final int numero;

    @JsonProperty("couleur")
    @Setter(value = AccessLevel.NONE)
    private final ECouleurBouee couleur;

    private final Point pt;

    @Setter(value = AccessLevel.NONE)
    private boolean presente = true;

    /**
     * Marque la bouée comme prise
     */
    public void setPrise() {
        presente = false;
    }

}

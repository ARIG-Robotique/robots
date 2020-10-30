package org.arig.robot.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
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

    @JsonProperty("couleur")
    @Setter(value = AccessLevel.NONE)
    private final ECouleurBouee couleur;

    private final Point pt;

    @Setter(value = AccessLevel.NONE)
    @Getter(value = AccessLevel.NONE)
    private EState state = EState.PRESENTE;

    /**
     * Marque la bouée comme prise
     */
    public void setPrise() {
        state = EState.PRISE;
    }

    /**
     * Change le statut de la bouée si pas marquée comme prise
     */
    public void setPresente(boolean present) {
        if (state != EState.PRISE) {
            state = present ? EState.PRESENTE : EState.ABSENTE;
        }
    }

    @JsonProperty("presente")
    public boolean presente() {
        return state == EState.PRESENTE;
    }

    /**
     * @deprecated retrocompat superviser
     */
    @Deprecated
    @JsonProperty("prise")
    public boolean prise() {
        return !presente();
    }
}

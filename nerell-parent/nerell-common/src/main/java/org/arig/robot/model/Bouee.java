package org.arig.robot.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true)
@AllArgsConstructor
@RequiredArgsConstructor
public class Bouee {

    @Setter(value = AccessLevel.NONE)
    private final ECouleurBouee couleur;

    private boolean prise = false;
}

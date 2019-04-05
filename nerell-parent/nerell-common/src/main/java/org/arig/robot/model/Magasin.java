package org.arig.robot.model;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesNerellConfig;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class Magasin {

    private List<Palet.Couleur> droit = new ArrayList<>();
    private List<Palet.Couleur> gauche = new ArrayList<>();

    void addDroit(Palet.Couleur couleur) {
        if (droit.size() >= IConstantesNerellConfig.nbPaletsMagasinMax) {
            log.warn("Le magasin droit est plein");
        } else {
            droit.add(couleur);
        }
    }

    void addGauche(Palet.Couleur couleur) {
        if (gauche.size() >= IConstantesNerellConfig.nbPaletsMagasinMax) {
            log.warn("Le magasin gauche est plein");
        } else {
            gauche.add(couleur);
        }
    }

    void emptyDroit() {
        droit.clear();
    }

    void emptyGauche() {
        gauche.clear();
    }

}

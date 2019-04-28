package org.arig.robot.model;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.model.enums.CouleurPalet;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class Magasin {

    @Getter
    private List<CouleurPalet> droit = new ArrayList<>();

    @Getter
    private List<CouleurPalet> gauche = new ArrayList<>();

    void addDroit(CouleurPalet couleur) {
        if (droit.size() >= IConstantesNerellConfig.nbPaletsMagasinMax) {
            log.warn("Le magasin droit est plein");
        } else {
            droit.add(couleur);
        }
    }

    void addGauche(CouleurPalet couleur) {
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

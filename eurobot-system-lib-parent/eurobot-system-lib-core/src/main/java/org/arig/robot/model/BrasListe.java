package org.arig.robot.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class BrasListe {

    @RequiredArgsConstructor
    @Getter
    public enum Contenu {
        VIDE(null),
        PLANTE_INCONNU(TypePlante.INCONNU),
        PLANTE_RESISTANTE(TypePlante.RESISTANTE),
        PLANTE_FRAGILE(TypePlante.FRAGILE),
        PLANTE_DANS_POT(TypePlante.INCONNU),
        POT(null),
        DEUX_POTS(null);

        private final TypePlante typePlante;

        public Contenu withPot() {
            switch (this) {
                case VIDE: return POT;
                case PLANTE_INCONNU: return PLANTE_DANS_POT;
                case PLANTE_RESISTANTE: return PLANTE_DANS_POT;
                case PLANTE_FRAGILE: return PLANTE_DANS_POT;
                case POT: return DEUX_POTS;
                default: throw new IllegalArgumentException("oula");
            }
        }
    }

    private Map<Bras, Contenu> contenu = new HashMap<>();

    public BrasListe() {
        contenu.put(Bras.AVANT_GAUCHE, Contenu.VIDE);
        contenu.put(Bras.AVANT_CENTRE, Contenu.VIDE);
        contenu.put(Bras.AVANT_DROIT, Contenu.VIDE);
        contenu.put(Bras.ARRIERE_GAUCHE, Contenu.VIDE);
        contenu.put(Bras.ARRIERE_CENTRE, Contenu.VIDE);
        contenu.put(Bras.ARRIERE_DROIT, Contenu.VIDE);
    }

    public boolean avantLibre() {
        return contenu.get(Bras.AVANT_DROIT) == Contenu.VIDE
                && contenu.get(Bras.AVANT_CENTRE) == Contenu.VIDE
                && contenu.get(Bras.AVANT_GAUCHE) == Contenu.VIDE;
    }

    public boolean arriereLibre() {
        return contenu.get(Bras.ARRIERE_DROIT) == Contenu.VIDE
                && contenu.get(Bras.ARRIERE_CENTRE) == Contenu.VIDE
                && contenu.get(Bras.ARRIERE_GAUCHE) == Contenu.VIDE;
    }

    public void setAvant(Contenu gauche, Contenu centre, Contenu droite) {
        log.info("[RS] Bras avant {} {} {}", gauche, centre, droite);
        contenu.put(Bras.AVANT_GAUCHE, gauche == null ? Contenu.VIDE : gauche);
        contenu.put(Bras.AVANT_CENTRE, centre == null ? Contenu.VIDE : centre);
        contenu.put(Bras.AVANT_DROIT, droite == null ? Contenu.VIDE : droite);
    }

    public void setArriere(Contenu gauche, Contenu centre, Contenu droite) {
        log.info("[RS] Bras arrière {} {} {}", gauche, centre, droite);
        contenu.put(Bras.ARRIERE_GAUCHE, gauche == null ? Contenu.VIDE : gauche);
        contenu.put(Bras.ARRIERE_CENTRE, centre == null ? Contenu.VIDE : centre);
        contenu.put(Bras.ARRIERE_DROIT, droite == null ? Contenu.VIDE : droite);
    }

    public Contenu[] getAvant() {
        return new Contenu[]{
                contenu.get(Bras.AVANT_GAUCHE),
                contenu.get(Bras.AVANT_CENTRE),
                contenu.get(Bras.AVANT_DROIT)
        };
    }

}

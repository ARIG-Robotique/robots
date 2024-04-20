package org.arig.robot.model;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class BrasListe {

    public enum Contenu {
        PLANTE_INCONNU,
        PLANTE_RESISTANTE,
        PLANTE_FRAGILE,
        POT,
        DEUX_POTS
    }

    private Map<Bras, Contenu> contenu = new HashMap<>();

    public BrasListe() {
        contenu.put(Bras.AVANT_GAUCHE, null);
        contenu.put(Bras.AVANT_CENTRE, null);
        contenu.put(Bras.AVANT_DROIT, null);
        contenu.put(Bras.ARRIERE_GAUCHE, null);
        contenu.put(Bras.ARRIERE_CENTRE, null);
        contenu.put(Bras.ARRIERE_DROIT, null);
    }

    public boolean avantLibre() {
        return contenu.get(Bras.AVANT_DROIT) == null && contenu.get(Bras.AVANT_CENTRE) == null && contenu.get(Bras.AVANT_GAUCHE) == null;
    }

    public boolean arriereLibre() {
        return contenu.get(Bras.ARRIERE_DROIT) == null && contenu.get(Bras.ARRIERE_CENTRE) == null && contenu.get(Bras.ARRIERE_GAUCHE) == null;
    }

    public void setAvant(Contenu gauche, Contenu centre, Contenu droite) {
        log.info("[RS] Bras avant {} {} {}", gauche, centre, droite);
        contenu.put(Bras.AVANT_GAUCHE, gauche);
        contenu.put(Bras.AVANT_CENTRE, centre);
        contenu.put(Bras.AVANT_DROIT, droite);
    }

    public void setArriere(Contenu gauche, Contenu centre, Contenu droite) {
        log.info("[RS] Bras arrière {} {} {}", gauche, centre, droite);
        contenu.put(Bras.ARRIERE_GAUCHE, gauche);
        contenu.put(Bras.ARRIERE_CENTRE, centre);
        contenu.put(Bras.ARRIERE_DROIT, droite);
    }

}

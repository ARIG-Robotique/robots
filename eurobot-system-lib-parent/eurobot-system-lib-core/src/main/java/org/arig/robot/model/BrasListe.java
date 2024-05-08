package org.arig.robot.model;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class BrasListe {

    private Map<Bras, Plante> contenu = new HashMap<>();

    public BrasListe() {
        contenu.put(Bras.AVANT_GAUCHE, new Plante(TypePlante.AUCUNE));
        contenu.put(Bras.AVANT_CENTRE, new Plante(TypePlante.AUCUNE));
        contenu.put(Bras.AVANT_DROIT, new Plante(TypePlante.AUCUNE));
        contenu.put(Bras.ARRIERE_GAUCHE, new Plante(TypePlante.AUCUNE));
        contenu.put(Bras.ARRIERE_CENTRE, new Plante(TypePlante.AUCUNE));
        contenu.put(Bras.ARRIERE_DROIT, new Plante(TypePlante.AUCUNE));
    }

    public boolean avantLibre() {
        return contenu.get(Bras.AVANT_DROIT).getType() == TypePlante.AUCUNE
                && contenu.get(Bras.AVANT_CENTRE).getType() == TypePlante.AUCUNE
                && contenu.get(Bras.AVANT_GAUCHE).getType() == TypePlante.AUCUNE;
    }

    public boolean arriereLibre() {
        return contenu.get(Bras.ARRIERE_DROIT).getType() == TypePlante.AUCUNE
                && contenu.get(Bras.ARRIERE_CENTRE).getType() == TypePlante.AUCUNE
                && contenu.get(Bras.ARRIERE_GAUCHE).getType() == TypePlante.AUCUNE;
    }

    public void setAvant(Plante gauche, Plante centre, Plante droite) {
        contenu.put(Bras.AVANT_GAUCHE, gauche == null ? new Plante(TypePlante.AUCUNE) : gauche);
        contenu.put(Bras.AVANT_CENTRE, centre == null ? new Plante(TypePlante.AUCUNE) : centre);
        contenu.put(Bras.AVANT_DROIT, droite == null ? new Plante(TypePlante.AUCUNE) : droite);
        log.info("[RS] Bras avant {} {} {}",
                contenu.get(Bras.AVANT_GAUCHE).getType(),
                contenu.get(Bras.AVANT_CENTRE).getType(),
                contenu.get(Bras.AVANT_DROIT).getType()
        );
    }

    public void setArriere(Plante gauche, Plante centre, Plante droite) {
        contenu.put(Bras.ARRIERE_GAUCHE, gauche == null ? new Plante(TypePlante.AUCUNE) : gauche);
        contenu.put(Bras.ARRIERE_CENTRE, centre == null ? new Plante(TypePlante.AUCUNE) : centre);
        contenu.put(Bras.ARRIERE_DROIT, droite == null ? new Plante(TypePlante.AUCUNE) : droite);
        log.info("[RS] Bras arrière {} {} {}",
                contenu.get(Bras.ARRIERE_GAUCHE).getType(),
                contenu.get(Bras.ARRIERE_CENTRE).getType(),
                contenu.get(Bras.ARRIERE_DROIT).getType()
        );
    }

    public Plante[] getAvant() {
        return new Plante[]{
                contenu.get(Bras.AVANT_GAUCHE),
                contenu.get(Bras.AVANT_CENTRE),
                contenu.get(Bras.AVANT_DROIT)
        };
    }

    public Plante[] getArriere() {
        return new Plante[]{
                contenu.get(Bras.ARRIERE_GAUCHE),
                contenu.get(Bras.ARRIERE_CENTRE),
                contenu.get(Bras.ARRIERE_DROIT)
        };
    }

}

package org.arig.robot.model;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.utils.SimpleCircularList;

import java.util.List;

@Slf4j
public class Carousel {

    public static final int PINCE_GAUCHE = 0;
    public static final int PINCE_DROITE = 1;
    public static final int MAGASIN_DROIT = 3;
    public static final int MAGASIN_GAUCHE = 4;
    public static final int LECTEUR = 5; // TODO à valider

    private final SimpleCircularList<Palet> list = new SimpleCircularList<>(6, null);

    /**
     * Vérifie si un emplacement est libre
     */
    public boolean isFree(int index) {
        return list.get(index) == null;
    }

    /**
     * Retourne le palet à un emplacement
     */
    public Palet get(int index) {
        return list.get(index);
    }

    /**
     * Vérifie s'il y a au moins un palet de la couleur
     */
    public boolean has(Palet.Couleur couleur) {
        return list.stream()
                .anyMatch(p -> {
                    if (p == null && couleur == null) {
                        return true;
                    }
                    if (p != null && couleur != null && couleur == Palet.Couleur.ANY) {
                        return true;
                    }
                    if (p != null && couleur != null && couleur == p.couleur()) {
                        return true;
                    }
                    return false;
                });
    }

    /**
     * Renvoie la premier position de la couleur, la plus proche d'une autre position
     */
    public int firstIndexOf(Palet.Couleur couleur, int ref) {
        for (int i = ref; i < ref + 6; i++) {
            int realIndex = i < 6 ? i : i - 6;
            Palet palet = get(realIndex);

            if (couleur == null && palet == null) {
                return realIndex;
            }
            if (couleur != null && palet != null && palet.couleur().equals(couleur)) {
                return realIndex;
            }
        }

        return -1;
    }

    public int firstIndexOf(List<Palet.Couleur> couleurs, int ref) {
        for (int i = ref; i < ref + 6; i++) {
            int realIndex = i < 6 ? i : i - 6;
            Palet palet = get(realIndex);

            if (palet != null && couleurs.contains(palet.couleur())) {
                return realIndex;
            }
        }

        return -1;
    }

    /**
     * Chnage/valide la couleur d'un palet
     */
    public void setColor(int index, Palet.Couleur couleur) {
        if (isFree(index)) {
            log.warn("L'emplacement {} était vide", index);
        } else {
            list.get(index).couleur(couleur);
        }
    }

    /**
     * Stocke un palet à l'emplacement
     */
    public boolean store(int index, Palet palet) {
        if (!isFree(index)) {
            return false;
        }

        list.set(index, palet);
        return true;
    }

    /**
     * Enlève le palet à l'emplacement
     */
    public void unstore(int index) {
        if (isFree(index)) {
            log.warn("L'emplacement {} était déjà vide", index);
        } else {
            list.set(index, null);
        }
    }

    public void rotate(int nb) {
        list.rotate(nb);
    }

}

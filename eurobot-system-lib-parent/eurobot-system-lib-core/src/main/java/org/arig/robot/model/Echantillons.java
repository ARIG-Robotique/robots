package org.arig.robot.model;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Echantillons implements Iterable<Echantillon> {

    @Getter
    private List<Echantillon> echantillons = new ArrayList<>();

    public Echantillons() {
        echantillons.add(new Echantillon(Echantillon.ID.SITE_ECHANTILLONS_JAUNE, CouleurEchantillon.ROCHER_BLEU, 900, 2000 - 555, true));
        echantillons.add(new Echantillon(Echantillon.ID.SITE_ECHANTILLONS_JAUNE, CouleurEchantillon.ROCHER_VERT, 830, 2000 - 675, true));
        echantillons.add(new Echantillon(Echantillon.ID.SITE_ECHANTILLONS_JAUNE, CouleurEchantillon.ROCHER_ROUGE, 900, 2000 - 795, true));
        echantillons.add(new Echantillon(Echantillon.ID.SITE_ECHANTILLONS_VIOLET, CouleurEchantillon.ROCHER_BLEU, 2100, 2000 - 555, true));
        echantillons.add(new Echantillon(Echantillon.ID.SITE_ECHANTILLONS_VIOLET, CouleurEchantillon.ROCHER_VERT, 2170, 2000 - 675, true));
        echantillons.add(new Echantillon(Echantillon.ID.SITE_ECHANTILLONS_VIOLET, CouleurEchantillon.ROCHER_ROUGE, 2100, 2000 - 795, true));
    }

    @Override
    public Iterator<Echantillon> iterator() {
        return echantillons.iterator();
    }

    public void priseSiteEchantillons(Team team) {
        echantillons.removeIf(echantillon -> {
            return echantillon.getId() == (team == Team.JAUNE ? Echantillon.ID.SITE_ECHANTILLONS_JAUNE : Echantillon.ID.SITE_ECHANTILLONS_VIOLET);
        });
    }

    public void priseSiteEchantillonsAdverse(Team team) {
        echantillons.removeIf(echantillon -> {
            return echantillon.getId() == (team == Team.JAUNE ? Echantillon.ID.SITE_ECHANTILLONS_VIOLET : Echantillon.ID.SITE_ECHANTILLONS_JAUNE);
        });
    }

}

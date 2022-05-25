package org.arig.robot.model;

import lombok.Getter;

import java.awt.Polygon;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Echantillons implements Iterable<Echantillon> {

    // delta en mm pour considérer que deux échantillons sont les mêmes
    private static final int DELTA = 50;

    private Rectangle siteFouilleJauneRect = new Rectangle(800, 2000 - 1550, 350, 350);
    private Rectangle siteFouilleVioletRect = new Rectangle(1850, 2000 - 1550, 350, 350);
    private Rectangle siteEchantillonsJauneRect = new Rectangle(780, 2000 - 850, 170, 350);
    private Rectangle siteEchantillonsVioletRect = new Rectangle(2050, 2000 - 850, 170, 350);

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

    public void priseSiteFouille(Team team) {
        echantillons.removeIf(echantillon -> {
            return echantillon.getId() == (team == Team.JAUNE ? Echantillon.ID.SITE_FOUILLE_JAUNE : Echantillon.ID.SITE_FOUILLE_VIOLET);
        });
    }

    public void priseSiteFouilleAdverse(Team team) {
        echantillons.removeIf(echantillon -> {
            return echantillon.getId() == (team == Team.JAUNE ? Echantillon.ID.SITE_FOUILLE_VIOLET : Echantillon.ID.SITE_FOUILLE_JAUNE);
        });
    }

    public boolean isInSiteFouilleJaune(final Point pt) {
        return siteFouilleJauneRect.contains(pt);
    }

    public boolean isInSiteFouilleViolet(final Point pt) {
        return siteFouilleVioletRect.contains(pt);
    }

    public boolean isInSiteEchantillonsJaune(final Point pt) {
        return siteEchantillonsJauneRect.contains(pt);
    }

    public boolean isInSiteEchantillonsViolet(final Point pt) {
        return siteEchantillonsVioletRect.contains(pt);
    }

    public Optional<Echantillon> findEchantillon(final Point pt, final CouleurEchantillon c) {
        return echantillons.stream()
                .filter(e -> Echantillons.match(e, pt, c))
                .findFirst();
    }

    public List<Echantillon> findEchantillon(final Polygon polygon) {
        return echantillons.stream()
                .filter(e -> polygon.contains(e.getX(), e.getY()))
                .collect(Collectors.toList());
    }

    public void addEchantillon(final Point point, final CouleurEchantillon c) {
        final Echantillon.ID id;
        if (isInSiteFouilleJaune(point)) {
            id = Echantillon.ID.SITE_FOUILLE_JAUNE;
        } else if (isInSiteFouilleViolet(point)) {
            id = Echantillon.ID.SITE_FOUILLE_VIOLET;
        } else {
            id = null;
        }
        echantillons.add(new Echantillon(id, c, point.getX(), point.getY(), System.currentTimeMillis()));
    }

    public static boolean match(final Echantillon e, final Point pt, final CouleurEchantillon c) {
        return Math.abs(e.getX() - pt.getX()) < DELTA && Math.abs(e.getY() - pt.getY()) < DELTA;
    }

}

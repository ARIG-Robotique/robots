package org.arig.robot.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.EurobotConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Data
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper = false)
public abstract class EurobotStatus extends AbstractRobotStatus {

    protected EurobotStatus(boolean mainRobot) {
        super(EurobotConfig.matchTimeMs, mainRobot);
    }

    @Setter(AccessLevel.NONE)
    private Team team;

    private Strategy strategy = Strategy.BASIC;

    public void setTeam(Team team) {
        this.team = team;
    }

    private boolean statuettePresente = true;
    private boolean vitrinePresente = true;

    /**
     * STATUT
     */

    private boolean distributeurEquipePris = false;
    private boolean distributeurCommunEquipePris = false;
    private boolean distributeurCommunAdversePris = false;
    private boolean siteEchantillonPris = false;
    private boolean siteDeFouillePris = false;
    private boolean vitrineActive = false;
    private boolean statuettePris = false;
    private boolean statuetteDansVitrine = false;
    private boolean repliqueDepose = false;
    private boolean echantillonAbriChantierDistributeurPris = false;
    private boolean echantillonAbriChantierCarreFouillePris = false;
    private boolean echantillonCampementPris = false;

    @Setter(AccessLevel.NONE)
    private List<CouleurEchantillon> abriChantier = new ArrayList<>();

    @Setter(AccessLevel.NONE)
    private Campement campement = new Campement();

    @Setter(AccessLevel.NONE)
    private Galerie galerie = new Galerie();

    @Setter(AccessLevel.NONE)
    private ZoneDeFouille zoneDeFouille = new ZoneDeFouille();

    public int calculerPoints() {
        int points = 0;

        // le robot secondaire ne compte pas les points si la comm est ok
        if (groupOk() && !mainRobot()) {
            return points;
        }

        if (vitrinePresente) points += 2;
        if (vitrineActive) points += 5;
        if (statuettePresente) points += 2;
        if (statuettePris) points += 5;
        if (statuetteDansVitrine) points += 15;
        if (repliqueDepose) points += 10;

        return points;
    }

    @Override
    public Map<String, Integer> scoreStatus() {
        Map<String, Integer> r = new HashMap<>();
        r.put("Vitrine", (vitrinePresente ? 2 : 0) + (vitrineActive ? 5 : 0));
        r.put("Statuette", (statuettePresente ? 2 : 0) + (statuettePris ? 5 : 0) + (statuetteDansVitrine ? 15 : 0));
        r.put("Replique", repliqueDepose ? 10 : 0);
        r.put("Carree fouille", zoneDeFouille.score());
        r.put("Campement", campement.score());
        r.put("Galerie", galerie.score());
        r.put("Retour sur site", 0);
        return r;
    }

    @Override
    public Map<String, Object> gameStatus() {
        Map<String, Object> r = new HashMap<>();
        r.put("distributeurEquipePris", distributeurEquipePris);
        r.put("distributeurCommunEquipePris", distributeurCommunEquipePris);
        r.put("distributeurCommunAdversePris", distributeurCommunAdversePris);
        r.put("siteEchantillonPris", siteEchantillonPris);
        r.put("siteDeFouillePris", siteDeFouillePris);
        r.put("vitrineActive", vitrineActive);
        r.put("statuettePris", statuettePris);
        r.put("statuetteDansVitrine", statuetteDansVitrine);
        r.put("repliqueDepose", repliqueDepose);
        r.put("echantillonAbriChantierDistributeurPris", echantillonAbriChantierDistributeurPris);
        r.put("echantillonAbriChantierCarreFouillePris", echantillonAbriChantierCarreFouillePris);
        r.put("echantillonCampementPris", echantillonCampementPris);
        return r;
    }
}

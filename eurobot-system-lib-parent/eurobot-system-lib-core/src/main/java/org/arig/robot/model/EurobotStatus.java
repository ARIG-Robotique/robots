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
        zoneDeFouille.team(team);
    }

    private boolean statuettePresente = true;
    private boolean vitrinePresente = true;

    /**
     * STATUT
     */

    private boolean distributeurEquipePris = false;
    public void distributeurEquipePris(boolean distributeurEquipePris) {
        log.info("[RS] distributeur equipe pris : {}", distributeurEquipePris);
        this.distributeurEquipePris = distributeurEquipePris;
    }
    private boolean distributeurCommunEquipePris = false;
    public void distributeurCommunEquipePris(boolean distributeurCommunEquipePris) {
        log.info("[RS] distributeur commun equipe pris : {}", distributeurCommunEquipePris);
        this.distributeurCommunEquipePris = distributeurCommunEquipePris;
    }
    private boolean distributeurCommunAdversePris = false;
    public void distributeurCommunAdversePris(boolean distributeurCommunAdversePris) {
        log.info("[RS] distributeur commun adverse pris : {}", distributeurCommunAdversePris);
        this.distributeurCommunAdversePris = distributeurCommunAdversePris;
    }
    private boolean siteEchantillonPris = false;
    public void siteEchantillonPris(boolean siteEchantillonPris) {
        log.info("[RS] site echantillon pris : {}", siteEchantillonPris);
        this.siteEchantillonPris = siteEchantillonPris;
    }
    private boolean siteDeFouillePris = false;
    public void siteDeFouillePris(boolean siteDeFouillePris) {
        log.info("[RS] site de fouille pris : {}", siteDeFouillePris);
        this.siteDeFouillePris = siteDeFouillePris;
    }
    private boolean vitrineActive = false;
    public void vitrineActive(boolean vitrineActive) {
        log.info("[RS] vitrine active : {}", vitrineActive);
        this.vitrineActive = vitrineActive;
    }
    private boolean statuettePris = false;
    public void statuettePris(boolean statuettePris) {
        log.info("[RS] statuette pris : {}", statuettePris);
        this.statuettePris = statuettePris;
    }
    private boolean statuetteDansVitrine = false;
    public void statuetteDansVitrine(boolean statuetteDansVitrine) {
        log.info("[RS] statuette dans vitrine : {}", statuetteDansVitrine);
        this.statuetteDansVitrine = statuetteDansVitrine;
    }
    private boolean repliqueDepose = false;
    public void repliqueDepose(boolean repliqueDepose) {
        log.info("[RS] replique depose : {}", repliqueDepose);
        this.repliqueDepose = repliqueDepose;
    }
    private boolean echantillonAbriChantierDistributeurPris = false;
    public void echantillonAbriChantierDistributeurPris(boolean echantillonAbriChantierDistributeurPris) {
        log.info("[RS] echantillon abri chantier distributeur pris : {}", echantillonAbriChantierDistributeurPris);
        this.echantillonAbriChantierDistributeurPris = echantillonAbriChantierDistributeurPris;
    }
    private boolean echantillonAbriChantierCarreFouillePris = false;
    public void echantillonAbriChantierCarreFouillePris(boolean echantillonAbriChantierCarreFouillePris) {
        log.info("[RS] echantillon abri chantier carre fouille pris : {}", echantillonAbriChantierCarreFouillePris);
        this.echantillonAbriChantierCarreFouillePris = echantillonAbriChantierCarreFouillePris;
    }
    private boolean echantillonCampementPris = false;
    public void echantillonCampementPris(boolean echantillonCampementPris) {
        log.info("[RS] echantillon campement pris : {}", echantillonCampementPris);
        this.echantillonCampementPris = echantillonCampementPris;
    }
    private SiteDeRetour siteDeRetour = SiteDeRetour.AUCUN;
    public void siteDeRetour(SiteDeRetour siteDeRetour) {
        log.info("[RS] site de retour : {}", siteDeRetour);
        this.siteDeRetour = siteDeRetour;
    }
    private SiteDeRetour siteDeRetourAutreRobot = SiteDeRetour.AUCUN;
    public void siteDeRetourAutreRobot(SiteDeRetour siteDeRetourAutreRobot) {
        log.info("[RS] site de retour autre robot : {}", siteDeRetourAutreRobot);
        this.siteDeRetourAutreRobot = siteDeRetourAutreRobot;
    }

    @Setter(AccessLevel.NONE)
    private List<CouleurEchantillon> abriChantier = new ArrayList<>();

    public EurobotStatus addAbriChantier(CouleurEchantillon couleur) {
        log.info("[RS] add abri chantier : {}", couleur);
        abriChantier.add(couleur);
        return this;
    }

    @Setter(AccessLevel.NONE)
    private Campement campement = new Campement();

    public EurobotStatus addCampementRouge(CouleurEchantillon echantillon) {
        log.info("[RS] add campement rouge : {}", echantillon);
        campement.addRouge(echantillon);
        return this;
    }
    public EurobotStatus addCampementVert(CouleurEchantillon echantillon) {
        log.info("[RS] add campement vert : {}", echantillon);
        campement.addVert(echantillon);
        return this;
    }
    public EurobotStatus addCampementBleu(CouleurEchantillon echantillon) {
        log.info("[RS] add campement bleu : {}", echantillon);
        campement.addBleu(echantillon);
        return this;
    }

    @Setter(AccessLevel.NONE)
    private Galerie galerie = new Galerie();

    public EurobotStatus addGalerieRouge(CouleurEchantillon echantillon) {
        log.info("[RS] add galerie rouge : {}", echantillon);
        galerie.addRouge(echantillon);
        return this;
    }
    public EurobotStatus addGalerieRougeVert(CouleurEchantillon echantillon) {
        log.info("[RS] add galerie rouge vert : {}", echantillon);
        galerie.addRougeVert(echantillon);
        return this;
    }
    public EurobotStatus addGalerieVert(CouleurEchantillon echantillon) {
        log.info("[RS] add galerie vert : {}", echantillon);
        galerie.addVert(echantillon);
        return this;
    }
    public EurobotStatus addGalerieVertBleu(CouleurEchantillon echantillon) {
        log.info("[RS] add galerie vert bleu : {}", echantillon);
        galerie.addVertBleu(echantillon);
        return this;
    }
    public EurobotStatus addGalerieBleu(CouleurEchantillon echantillon) {
        log.info("[RS] add galerie bleu : {}", echantillon);
        galerie.addBleu(echantillon);
        return this;
    }

    @Setter(AccessLevel.NONE)
    private ZoneDeFouille zoneDeFouille = new ZoneDeFouille();

    public EurobotStatus couleurCarreFouille(int numero, CouleurCarreFouille couleur) {
        log.info("[RS] couleur carre fouille {} : {}", numero, couleur);
        zoneDeFouille.get(numero).couleur(couleur);
        zoneDeFouille.refreshProcessing();
        return this;
    }

    public EurobotStatus basculeCarreFouille(int numero) {
        log.info("[RS] bascule carre fouille : {}", numero);
        zoneDeFouille.get(numero).bascule(true);
        return this;
    }

    private int scoreAbriChantier() {
        return abriChantier.size() * 5;
    }

    private int scoreRetourAuSite() {
        if ((twoRobots() && siteDeRetour.isInSite() && siteDeRetourAutreRobot.isInSite() && siteDeRetour == siteDeRetourAutreRobot)
                || (!twoRobots() && siteDeRetour.isInSite())) {
            return 20;
        }

        return 0;
    }

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
        if (distributeurEquipePris) points += 3; // 3 échantillons
        if (distributeurCommunEquipePris) points += 3; // 3 échantillons
        points += campement.score();
        points += galerie.score();
        points += zoneDeFouille.score();
        points += scoreAbriChantier();
        points += scoreRetourAuSite();

        return points;
    }

    @Override
    public Map<String, Integer> scoreStatus() {
        Map<String, Integer> r = new HashMap<>();
        r.put("Vitrine", (vitrinePresente ? 2 : 0) + (vitrineActive ? 5 : 0));
        r.put("Statuette", (statuettePresente ? 2 : 0) + (statuettePris ? 5 : 0) + (statuetteDansVitrine ? 15 : 0));
        r.put("Replique", repliqueDepose ? 10 : 0);
        r.put("Zone de fouille", zoneDeFouille.score());
        r.put("Campement", campement.score());
        r.put("Galerie", galerie.score());
        r.put("Abri de chantier", scoreAbriChantier());
        r.put("Retour sur site", scoreRetourAuSite());
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
        r.put("siteDeRetour", siteDeRetour);
        r.put("siteDeRetourAutreRobot", siteDeRetourAutreRobot);
        return r;
    }
}

package org.arig.robot.model;

import lombok.*;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.EurobotConfig;

import java.util.*;

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

    /**
     * CONFIGURATION
     */

    private boolean troisDeposeAbriChantier = false;

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

    private boolean siteEchantillonAdversePris = false;

    public void siteEchantillonAdversePris(boolean siteEchantillonAdversePris) {
        log.info("[RS] site echantillon adverse pris : {}", siteEchantillonAdversePris);
        this.siteEchantillonAdversePris = siteEchantillonAdversePris;
    }

    private boolean siteDeFouillePris = false;

    public void siteDeFouillePris(boolean siteDeFouillePris) {
        log.info("[RS] site de fouille pris : {}", siteDeFouillePris);
        this.siteDeFouillePris = siteDeFouillePris;
    }

    private boolean siteDeFouilleAdversePris = false;

    public void siteDeFouilleAdversePris(boolean siteDeFouilleAdversePris) {
        log.info("[RS] site de fouille adverse pris : {}", siteDeFouilleAdversePris);
        this.siteDeFouilleAdversePris = siteDeFouilleAdversePris;
    }

    private boolean vitrineActive = false;

    public void vitrineActive(boolean vitrineActive) {
        log.info("[RS] vitrine active : {}", vitrineActive);
        this.vitrineActive = vitrineActive;
    }

    @Setter(AccessLevel.NONE)
    private boolean statuettePris = false;

    @Setter(AccessLevel.NONE)
    private boolean statuettePrisDansCeRobot = false;

    public void statuettePris(boolean statuettePris, boolean statuettePrisDansCeRobot) {
        log.info("[RS] statuette pris : {} / Dans ce robot : {}", statuettePris, statuettePrisDansCeRobot);
        this.statuettePris = statuettePris;
        this.statuettePrisDansCeRobot = statuettePrisDansCeRobot;
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

    public void deposeAbriChantier(CouleurEchantillon... echantillons) {
        for (CouleurEchantillon echantillon : echantillons) {
            log.info("[RS] depose abri chantier : {}", echantillon);
            abriChantier.add(echantillon);
        }
    }

    @Setter(AccessLevel.NONE)
    private Campement campement = new Campement();

    public void deposeCampementRouge(CouleurEchantillon... echantillons) {
        for (CouleurEchantillon echantillon : echantillons) {
            log.info("[RS] depose campement rouge : {}", echantillon);
            campement.addRouge(echantillon);
        }
    }

    public void deposeCampementVert(CouleurEchantillon... echantillons) {
        for (CouleurEchantillon echantillon : echantillons) {
            log.info("[RS] depose campement vert : {}", echantillon);
            campement.addVert(echantillon);
        }
    }

    public void deposeCampementBleu(CouleurEchantillon... echantillons) {
        for (CouleurEchantillon echantillon : echantillons) {
            log.info("[RS] depose campement bleu : {}", echantillon);
            campement.addBleu(echantillon);
        }
    }

    @Setter(AccessLevel.NONE)
    private Galerie galerie = new Galerie();

    public void deposeGalerieRouge(CouleurEchantillon... echantillons) {
        for (CouleurEchantillon echantillon : echantillons) {
            log.info("[RS] depose galerie rouge : {}", echantillon);
            galerie.addRouge(echantillon);
        }
    }

    public void deposeGalerieRougeVert(CouleurEchantillon... echantillons) {
        for (CouleurEchantillon echantillon : echantillons) {
            log.info("[RS] depose galerie rouge vert : {}", echantillon);
            galerie.addRougeVert(echantillon);
        }
    }

    public void deposeGalerieVert(CouleurEchantillon... echantillons) {
        for (CouleurEchantillon echantillon : echantillons) {
            log.info("[RS] depose galerie vert : {}", echantillon);
            galerie.addVert(echantillon);
        }
    }

    public void deposeGalerieVertBleu(CouleurEchantillon... echantillons) {
        for (CouleurEchantillon echantillon : echantillons) {
            log.info("[RS] depose galerie vert bleu : {}", echantillon);
            galerie.addVertBleu(echantillon);
        }
    }

    public void deposeGalerieBleu(CouleurEchantillon... echantillons) {
        for (CouleurEchantillon echantillon : echantillons) {
            log.info("[RS] depose galerie bleu : {}", echantillon);
            galerie.addBleu(echantillon);
        }
    }

    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private ZoneDeFouille zoneDeFouille = new ZoneDeFouille();

    public int zoneDeFouillePointRestant() {
        return 25 - zoneDeFouille.score();
    }

    public boolean zoneDeFouilleComplete(){
        return zoneDeFouille.isComplete();
    }

    public CarreFouille carreFouille(int numero) {
        return zoneDeFouille.get(numero);
    }

    public CarreFouille nextCarreDeFouille(int nbTry, boolean reverse) {
        return zoneDeFouille.nextCarreFouilleToProcess(nbTry, reverse);
    }

    public void couleurCarreFouille(int numero, CouleurCarreFouille couleur) {
        log.info("[RS] couleur carre fouille {} : {}", numero, couleur);
        zoneDeFouille.get(numero).couleur(couleur);
        zoneDeFouille.refreshProcessing();
    }

    public void basculeCarreFouille(int numero) {
        log.info("[RS] bascule carre fouille : {}", numero);
        zoneDeFouille.get(numero).bascule(true);
    }

    private CouleurEchantillon[] stock = new CouleurEchantillon[]{null, null, null, null, null, null};

    public long stockDisponible() {
        return Arrays.stream(stock).filter(Objects::isNull).count();
    }

    public void stockage(CouleurEchantillon couleur) {
        stock[indexStockage()] = couleur;
    }

    public CouleurEchantillon destockage() {
        int idx = indexDestockage();
        CouleurEchantillon couleur = stock[idx];
        stock[idx] = null;
        return couleur;
    }

    public int indexStockage() {
        for (int i = 0; i < stock.length; i++) {
            if (stock[i] == null) {
                return i;
            }
        }
        return -1;
    }

    public int indexDestockage() {
        for (int i = stock.length - 1; i >= 0; i--) {
            if (stock[i] != null) {
                return i;
            }
        }
        return -1;
    }

    private int scoreAbriChantier() {
        return abriChantier.size() * 5;
    }

    private int scoreRetourAuSite() {
        if ((twoRobots() && siteDeRetour.isInSite() && siteDeRetourAutreRobot.isInSite())
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

        points += 2; // Vitrine présente
        points += 2; // Statuette présente
        if (vitrineActive) points += 5;
        if (statuettePris) points += 5;
        if (statuetteDansVitrine) points += 15;
        if (repliqueDepose) points += 10;
        if (distributeurEquipePris) points += 3; // 3 échantillons
        if (distributeurCommunEquipePris) points += 3; // 3 échantillons
        if (echantillonAbriChantierCarreFouillePris) points += 1;
        if (echantillonAbriChantierDistributeurPris) points += 1;
        if (echantillonCampementPris) points += 1;
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
        r.put("Vitrine", 2 + (vitrineActive ? 5 : 0));
        r.put("Statuette", 2 + (statuettePris ? 5 : 0) + (statuetteDansVitrine ? 15 : 0));
        r.put("Replique", repliqueDepose ? 10 : 0);
        r.put("Distributeurs", (distributeurEquipePris ? 3 : 0) + (distributeurCommunEquipePris ? 3 : 0) + (echantillonAbriChantierCarreFouillePris ? 1 : 0) + (echantillonAbriChantierCarreFouillePris ? 1 : 0) + (echantillonCampementPris ? 1 : 0));
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
        r.put("stock", stock);
        r.put("carresFouille", zoneDeFouille.carresFouille);
        r.put("distributeurEquipePris", distributeurEquipePris);
        r.put("distributeurCommunEquipePris", distributeurCommunEquipePris);
        r.put("distributeurCommunAdversePris", distributeurCommunAdversePris);
        r.put("siteEchantillonPris", siteEchantillonPris);
        r.put("siteEchantillonAdversePris", siteEchantillonAdversePris);
        r.put("siteDeFouillePris", siteDeFouillePris);
        r.put("siteDeFouilleAdversePris", siteDeFouilleAdversePris);
        r.put("carresDeFouilleTermines", zoneDeFouille.isComplete());
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

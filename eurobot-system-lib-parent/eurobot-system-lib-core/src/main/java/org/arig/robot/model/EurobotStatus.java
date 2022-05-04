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

    private boolean reverseCarreDeFouille = false;
    private boolean stockageAbriChantier = false;

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

    private boolean distributeurEquipeBloque = false;

    private boolean distributeurCommunEquipeBloque = false;

    private boolean distributeurCommunAdverseBloque = false;

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
    private boolean statuettePrise = false;

    @Setter(AccessLevel.NONE)
    private boolean statuettePriseDansCeRobot = false;

    public void statuettePrise(boolean statuettePrise, boolean statuettePriseDansCeRobot) {
        log.info("[RS] statuette prise : {} / Dans ce robot : {}", statuettePrise, statuettePriseDansCeRobot);
        this.statuettePrise = statuettePrise;
        this.statuettePriseDansCeRobot = statuettePriseDansCeRobot;
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
    @Getter(AccessLevel.NONE)
    private List<CouleurEchantillon> abriChantier = new ArrayList<>();

    public void deposeAbriChantier(CouleurEchantillon... echantillons) {
        for (CouleurEchantillon echantillon : echantillons) {
            log.info("[RS] depose abri chantier : {}", echantillon);
            abriChantier.add(echantillon);
        }
    }

    public CouleurEchantillon abriChantierEchantillon(int index) {
        return abriChantier.get(index);
    }

    public int abriChantierSize() {
        return abriChantier.size();
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

    public boolean zoneDeFouilleComplete() {
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

    public int stockDisponible() {
        int i = indexStockage();
        return i == -1 ? 0 : stock.length - i;
    }

    public int stockTaille() {
        int i = indexDestockage();
        return i == -1 ? 0 : i + 1;
    }

    public CouleurEchantillon stockFirst() {
        int i = indexDestockage();
        return i == -1 ? null : stock[i];
    }

    public void stockage(CouleurEchantillon couleur) {
        int i = indexStockage();
        if (i != -1) {
            stock[i] = couleur;
        } else {
            log.error("[RS] demande de stockage invalide, pas de place");
        }
    }

    public CouleurEchantillon destockage() {
        int idx = indexDestockage();
        CouleurEchantillon couleur = stock[idx];
        stock[idx] = null;
        return couleur;
    }

    /**
     * Retourne le premier emplacement vide (en partant de la fin=sortie)
     */
    public int indexStockage() {
        for (int i = stock.length - 1; i > 0; i--) {
            if (stock[i] == null && stock[i-1] != null) {
                return i;
            }
        }
        if (stock[0] == null) {
            return 0;
        }
        return -1;
    }

    /**
     * Retourne le premier emplacement non vide (en partant de la fin=sortie)
     */
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
        if (statuettePrise) points += 5;
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
        r.put("Statuette", 2 + (statuettePrise ? 5 : 0) + (statuetteDansVitrine ? 15 : 0));
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
        r.put("siteDeRetour", siteDeRetour);
        r.put("siteDeRetourAutreRobot", siteDeRetourAutreRobot);
        return r;
    }

    @Override
    public Map<String, Boolean> gameFlags() {
        Map<String, Boolean> r = new HashMap<>();
        r.put("Distributeur équipe pris", distributeurEquipePris);
        r.put("Distributeur commun équipe pris", distributeurCommunEquipePris);
        r.put("Distributeur commun adverse pris", distributeurCommunAdversePris);
        r.put("Site echantillons pris", siteEchantillonPris);
        r.put("Site echantillons adverse pris", siteEchantillonAdversePris);
        r.put("Site de fouille pris", siteDeFouillePris);
        r.put("Site de fouille adverse pris", siteDeFouilleAdversePris);
        r.put("Carrés de fouille terminés", zoneDeFouille.isComplete());
        r.put("Vitrine activée", vitrineActive);
        r.put("Statuette prise", statuettePrise);
        r.put("Statuette dans vitrine", statuetteDansVitrine);
        r.put("Replique déposée", repliqueDepose);
        r.put("Echantillon chantier (coté distrib.) pris", echantillonAbriChantierDistributeurPris);
        r.put("Echantillon chantier (coté fouille) pris", echantillonAbriChantierCarreFouillePris);
        r.put("Echantillon campement pris", echantillonCampementPris);
        return r;
    }
}

package org.arig.robot.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.EurobotConfig;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Data
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper = false)
public abstract class EurobotStatus extends AbstractRobotStatus {

    protected EurobotStatus(boolean mainRobot) {
        super(EurobotConfig.matchTimeMs, mainRobot);
    }

    @Override
    public void startMatch() {
        super.startMatch();

        stocksPots.get(team == Team.BLEU ? StockPots.ID.JAUNE_NORD : StockPots.ID.BLEU_NORD).pris();
    }

    private boolean etalonageBaliseOk = false;

    @Setter(AccessLevel.NONE)
    private boolean baliseEnabled = false;

    public void enableBalise() {
        log.info("[RS] activation de la balise");
        baliseEnabled = true;
    }

    public void disableBalise() {
        log.info("[RS] désactivation de la balise");
        baliseEnabled = false;
    }

    @Setter(AccessLevel.NONE)
    private Team team;

    private Strategy strategy = Strategy.BASIC;

    public void setTeam(Team team) {
        this.team = team;
        panneauxSolaire.team(team);
        plantes.team(team);
    }

    /**
     * CONFIGURATION
     */

    private boolean preferePanneaux = false;
    private boolean activeVolAuSol = false;
    private boolean activeVolJardinieres = false;

    /**
     * STATUT
     */

    private Plantes plantes = new Plantes();

    @Setter(AccessLevel.NONE)
    private ZoneDepose aireDeDeposeNord = new ZoneDepose();
    @Setter(AccessLevel.NONE)
    private ZoneDepose aireDeDeposeMilieu = new ZoneDepose();
    @Setter(AccessLevel.NONE)
    private ZoneDepose aireDeDeposeSud = new ZoneDepose();

    @Setter(AccessLevel.NONE)
    private Jardiniere jardiniereNord = new Jardiniere();
    @Setter(AccessLevel.NONE)
    private Jardiniere jardiniereMilieu = new Jardiniere();
    @Setter(AccessLevel.NONE)
    private Jardiniere jardiniereSud = new Jardiniere();

    private SiteDeCharge siteDeCharge = SiteDeCharge.AUCUN;
    private SiteDeCharge siteDeDepart = SiteDeCharge.AUCUN;

    public void siteDeCharge(SiteDeCharge siteDeCharge) {
        log.info("[RS] site de charge : {}", siteDeCharge);
        this.siteDeCharge = siteDeCharge;
    }

    public void setSiteDeDepart(SiteDeCharge siteDeCharge) {
        log.info("[RS] site de départ : {}", siteDeCharge);
        this.siteDeDepart = siteDeCharge;
    }

    @Setter(AccessLevel.NONE)
    private PanneauxSolaire panneauxSolaire = new PanneauxSolaire();

    public int panneauxSolairePointRestant() {
        return Math.max(0, 30 - panneauxSolaire.score());
    }

    public void couleurPanneauSolaire(int numero, CouleurPanneauSolaire couleur) {
        log.info("[RS] couleur panneau solaire {} : {}", numero, couleur);
        panneauxSolaire.get(numero).couleur(couleur);
    }

    @Setter(AccessLevel.NONE)
    private StocksPots stocksPots = new StocksPots();

    @Setter(AccessLevel.NONE)
    private BrasListe bras = new BrasListe();

    @Setter(AccessLevel.NONE)
    private Plante[] stock = new Plante[]{
            new Plante(TypePlante.AUCUNE),
            new Plante(TypePlante.AUCUNE),
            new Plante(TypePlante.AUCUNE)
    };

    public void setStock(TypePlante gauche, TypePlante centre, TypePlante droite) {
        stock[0] = new Plante(gauche);
        stock[1] = new Plante(centre);
        stock[2] = new Plante(droite);
    }

    public boolean stockLibre() {
        return stock[0].getType() == TypePlante.AUCUNE
                && stock[1].getType() == TypePlante.AUCUNE
                && stock[2].getType() == TypePlante.AUCUNE;
    }

    /* *************************** SCORE *************************** */
    /* ************************************************************* */

    private int scoreJardinieres() {
        return jardiniereSud.score() + jardiniereMilieu.score() + jardiniereNord.score();
    }

    private int scoreDeposeAuSol() {
        return aireDeDeposeNord.score() + aireDeDeposeMilieu.score() + aireDeDeposeSud().score();
    }

    private int scorePanneauxSolaire() {
        return panneauxSolaire.score();
    }

    private int scoreSiteDeCharge() {
        return siteDeCharge.isEnCharge() ? 10 : 0;
    }

    public int calculerPoints() {
        int points = 0;

        // le robot secondaire ne compte pas les points si la comm est ok
        if (groupOk() && !mainRobot()) {
            return points;
        }

        points += scoreJardinieres();
        points += scoreDeposeAuSol();
        points += scorePanneauxSolaire();
        points += scoreSiteDeCharge();

        return points;
    }

    @Override
    public Map<String, Integer> scoreStatus() {
        Map<String, Integer> r = new HashMap<>();
        r.put("Jardinières", scoreJardinieres());
        r.put("Aires de dépose", scoreDeposeAuSol());
        r.put("Panneaux solaire", scorePanneauxSolaire());
        r.put("Site de charge", scoreSiteDeCharge());
        return r;
    }

    @Override
    public Map<String, Object> gameStatus() {
        Map<String, Object> r = new HashMap<>();
        r.put("siteDeDepart", siteDeDepart);
        r.put("siteDeCharge", siteDeCharge);
        r.put("panneaux", panneauxSolaire.data);
        r.put("stocksPots", stocksPots.data);
        r.put("plantes", plantes.getPlantes());
        r.put("airesDepose", Map.of(
                "NORD", aireDeDeposeNord.data,
                "MILIEU", aireDeDeposeMilieu.data,
                "SUD", aireDeDeposeSud.data
        ));
        r.put("jardinieres", Map.of(
                "NORD", jardiniereNord.data,
                "MILIEU", jardiniereMilieu.data,
                "SUD", jardiniereSud.data
        ));
        r.put("brasAvant", bras.getAvant());
        r.put("brasArriere", bras.getArriere());
        r.put("stock", stock);
        return r;
    }

    @Override
    public Map<String, Boolean> gameFlags() {
        Map<String, Boolean> r = new HashMap<>();
        r.put("Panneaux solaire terminés", panneauxSolaire.isComplete());
        return r;
    }

    @Override
    public Map<String, String> deposesStatus() {
        Map<String, String> r = new HashMap<>();
        r.put("Jardinière Nord", jardiniereNord.toString());
        r.put("Jardinière Milieu", jardiniereMilieu.toString());
        r.put("Jardinière Sud", jardiniereSud.toString());
        r.put("Aire de dépose Nord", aireDeDeposeNord.toString());
        r.put("Aire de dépose Milieu", aireDeDeposeMilieu.toString());
        r.put("Aire de dépose Sud", aireDeDeposeSud.toString());
        return r;
    }
}

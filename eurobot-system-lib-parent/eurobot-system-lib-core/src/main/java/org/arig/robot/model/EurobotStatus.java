package org.arig.robot.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.EurobotConfig;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Data
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper = false)
public abstract class EurobotStatus extends AbstractRobotStatus {

    protected EurobotStatus(boolean mainRobot) {
        super(EurobotConfig.matchTimeMs, mainRobot);
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

    private StockPlantes stockPlantes = new StockPlantes();

    private AireDeDepose aireDeDeposeNord = new AireDeDepose();
    private AireDeDepose aireDeDeposeMilieu = new AireDeDepose();
    private AireDeDepose aireDeDeposeSud = new AireDeDepose();

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
    @Getter(AccessLevel.NONE)
    private PanneauxSolaire panneauxSolaire = new PanneauxSolaire();

    public int panneauxSolairePointRestant() {
        return Math.max(0, 30 - panneauxSolaire.score());
    }

    public boolean panneauxSolaireEquipeDone() {
        return panneauxSolaire.equipeDone();
    }

    public void panneauxSolaireEquipeDone(int nb) {
        panneauxSolaire.equipeDone(nb);
    }

    public PanneauSolaire panneauSolaire(int numero) {
        return panneauxSolaire.get(numero);
    }

    public PanneauSolaire nextPanneauSolaire(int nbTry, boolean reverse) {
        return panneauxSolaire.nextPanneauSolaireToProcess(nbTry, reverse);
    }

    public void couleurPanneauSolaire(int numero, CouleurPanneauSolaire couleur) {
        log.info("[RS] couleur panneau solaire {} : {}", numero, couleur);
        panneauxSolaire.get(numero).couleur(couleur);
    }

    /* *************************** SCORE *************************** */
    /* ************************************************************* */

    private int scoreJardinieres() {
        return 0;
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
        r.put("Dépose au sol", scoreDeposeAuSol());
        r.put("Panneaux Solaire", scorePanneauxSolaire());
        r.put("Site de charge", scoreSiteDeCharge());
        return r;
    }

    @Override
    public Map<String, Object> gameStatus() {
        Map<String, Object> r = new HashMap<>();
        r.put("panneauxSolaire", panneauxSolaire.panneauxSolaire);
        r.put("siteDeRetour", siteDeCharge);
        return r;
    }

    @Override
    public Map<String, Boolean> gameFlags() {
        Map<String, Boolean> r = new LinkedHashMap<>();
        r.put("Panneaux solaire terminés", panneauxSolaire.isComplete());
        return r;
    }

    @Override
    public Map<String, String> deposesStatus() {
        Map<String, String> r = new LinkedHashMap<>();
        r.put("Jardinière Gauche", "EMPTY");
        r.put("Jardinière Centre", "EMPTY");
        r.put("Jardinière Droite", "EMPTY");

        Function<Plante, String> mapper = p -> p != null ? p.getType().name() + " (" + (p.isDansPot() ? "dans pot" : "sans pot") + ")" : "EMPTY";

        r.put("Dépose au sol Nord 1/2", Arrays.stream(aireDeDeposeNord.rang1).map(mapper).collect(Collectors.joining(",")));
        r.put("Dépose au sol Nord 2/2", Arrays.stream(aireDeDeposeNord.rang2).map(mapper).collect(Collectors.joining(",")));
        r.put("Dépose au sol Milieu 1/2", Arrays.stream(aireDeDeposeMilieu.rang1).map(mapper).collect(Collectors.joining(",")));
        r.put("Dépose au sol Milieu 2/2", Arrays.stream(aireDeDeposeMilieu.rang2).map(mapper).collect(Collectors.joining(",")));
        r.put("Dépose au sol Sud 1/2", Arrays.stream(aireDeDeposeSud.rang1).map(mapper).collect(Collectors.joining(",")));
        r.put("Dépose au sol Sud 2/2", Arrays.stream(aireDeDeposeSud.rang2).map(mapper).collect(Collectors.joining(",")));
        return r;
    }
}

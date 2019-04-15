package org.arig.robot.model;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.model.balise.StatutBalise;
import org.springframework.beans.factory.InitializingBean;

import java.util.*;

@Slf4j
@Data
@EqualsAndHashCode(callSuper = true)
public class RobotStatus extends AbstractRobotStatus implements InitializingBean {

    @Setter(AccessLevel.NONE)
    private boolean simulateur = false;

    public void setSimulateur() {
        simulateur = true;
    }

    private boolean finale = false;

    private Team team = Team.UNKNOWN;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private StopWatch matchTime = new StopWatch();

    public void startMatch() {
        matchTime.start();
    }

    public void stopMatch() {
        matchTime.stop();

        // Arrêt de l'asservissement et des moteurs, et tout et tout
        this.disableAsserv();
        this.disableAsservCarousel();
        this.disableAvoidance();
        this.disableMatch();
        this.disableCalageBordure();
    }

    public long getElapsedTime() {
        return matchTime.getTime();
    }

    @Setter(AccessLevel.NONE)
    private boolean calageBordureEnabled = false;

    public void enableCalageBordure() {
        log.info("Activation calage bordure");
        calageBordureEnabled = true;
    }

    public void disableCalageBordure() {
        log.info("Désactivation calage bordure");
        calageBordureEnabled = false;
    }

    private boolean baliseOk = false;
    private StatutBalise statutBalise = null;

    @Setter(AccessLevel.NONE)
    private Carousel carousel = new Carousel();

    @Setter(AccessLevel.NONE)
    private Magasin magasin = new Magasin();

    private ESide goldeniumInPince = null;

    private boolean trouNoirVioletVisite = false;
    private boolean trouNoirJauneVisite = false;

    @Setter(AccessLevel.NONE)
    private List<Palet.Couleur> paletsInAccelerateur = new ArrayList<>();

    @Setter(AccessLevel.NONE)
    private List<Palet.Couleur> paletsInBalance = new ArrayList<>();

    @Setter(AccessLevel.NONE)
    private Map<Integer, Palet.Couleur> paletsPetitDistributeur = new HashMap<>();
    @Setter(AccessLevel.NONE)
    private Map<Integer, Palet.Couleur> paletsGrandDistributeurEquipe = new HashMap<>();
    @Setter(AccessLevel.NONE)
    private Map<Integer, Palet.Couleur> paletsGrandDistributeurAdverse = new HashMap<>();

    @Setter(AccessLevel.NONE)
    private List<Palet.Couleur> paletsInTableauRouge = new ArrayList<>();
    @Setter(AccessLevel.NONE)
    private List<Palet.Couleur> paletsInTableauVert = new ArrayList<>();
    @Setter(AccessLevel.NONE)
    private List<Palet.Couleur> paletsInTableauBleu = new ArrayList<>();

    private boolean experienceActivee = false;

    private boolean accelerateurOuvert = false;

    private boolean goldeniumPrit = false;

    /**
     * INIT
     */
    @Override
    public void afterPropertiesSet() {
        paletsGrandDistributeurAdverse.put(0, Palet.Couleur.ROUGE);
        paletsGrandDistributeurAdverse.put(1, Palet.Couleur.VERT);
        paletsGrandDistributeurAdverse.put(2, Palet.Couleur.ROUGE);
        paletsGrandDistributeurAdverse.put(3, Palet.Couleur.BLEU);
        paletsGrandDistributeurAdverse.put(4, Palet.Couleur.ROUGE);
        paletsGrandDistributeurAdverse.put(5, Palet.Couleur.VERT);

        paletsGrandDistributeurEquipe.put(0, Palet.Couleur.ROUGE);
        paletsGrandDistributeurEquipe.put(1, Palet.Couleur.VERT);
        paletsGrandDistributeurEquipe.put(2, Palet.Couleur.ROUGE);
        paletsGrandDistributeurEquipe.put(3, Palet.Couleur.BLEU);
        paletsGrandDistributeurEquipe.put(4, Palet.Couleur.ROUGE);
        paletsGrandDistributeurEquipe.put(5, Palet.Couleur.VERT);

        paletsPetitDistributeur.put(0, Palet.Couleur.BLEU);
        paletsPetitDistributeur.put(1, Palet.Couleur.VERT);
        paletsPetitDistributeur.put(2, Palet.Couleur.ROUGE);
    }

    /**
     * Dernière préparations avant le départ
     */
    public void init() {
        // le palet extrème côté adverse ne peut pas être prit
        // sinon on rentre dans la zone de départ adverse
        if (team == Team.VIOLET) {
            paletsGrandDistributeurEquipe.put(0, null);
        } else {
            paletsGrandDistributeurAdverse.put(0, null);
        }
    }

    public int calculerPoints() {
        int points = 5; // experience placée
        points += pointsTableau(paletsInTableauRouge, Palet.Couleur.ROUGE);
        points += pointsTableau(paletsInTableauVert, Palet.Couleur.VERT);
        points += pointsTableau(paletsInTableauBleu, Palet.Couleur.BLEU);
        points += pointsBalance();
        points += 10 * paletsInAccelerateur.size();
        points += accelerateurOuvert ? 10 : 0;
        points += goldeniumPrit ? 20 : 0;
        points += experienceActivee ? 15 + 20 : 0;
        return points;
    }

    public double scoreFinal() {
        int points = calculerPoints();
        double score = points;
        score += 0.3 * points; // evaluation
        score += 10; // bonus;
        score += finale ? 30 : 0;
        return score;
    }

    private int pointsTableau(final List<Palet.Couleur> tableau, Palet.Couleur couleur) {
        return tableau.stream()
                .filter(Objects::nonNull)
                .mapToInt(c -> 1 + (Palet.Couleur.GOLD == c ? 6 : couleur == c ? 5 : 0))
                .sum();
    }

    private int pointsBalance() {
        return paletsInBalance.stream()
                .filter(Objects::nonNull)
                .sorted(Comparator.comparingInt(Palet.Couleur::importance))
                .limit(IConstantesNerellConfig.nbPaletsBalanceMax)
                .mapToInt(c -> {
                    switch (c) {
                        case GOLD:
                            return 24;
                        case BLEU:
                            return 12;
                        case VERT:
                            return 8;
                        case ROUGE:
                            return 4;
                        default:
                            return 0;
                    }
                })
                .sum();
    }
}

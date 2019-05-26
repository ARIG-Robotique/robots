package org.arig.robot.model;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.model.balise.StatutBalise;
import org.arig.robot.model.enums.CouleurPalet;
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

    private List<EStrategy> strategies = new ArrayList<>();

    public boolean strategyActive(EStrategy strategy) {
        return strategies.contains(strategy);
    }

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private StopWatch matchTime = new StopWatch();

    public void startMatch() {
        matchTime.start();

        log.info("Démarrage du match");

        // Activation
        this.enableMatch();
        this.enableSerrage();
        this.enableCarousel();
        this.enableVentouses();
        this.enableMagasin();
    }

    public void stopMatch() {
        matchTime.stop();

        // Arrêt de l'asservissement et des moteurs, et tout et tout
        this.disableAsserv();
        this.disableAsservCarousel();
        this.disableAvoidance();
        this.disableMatch();
        this.disableCalageBordure();

        this.disableCarousel();
        this.disableSerrage();
        this.disableMagasin();
        this.disableVentouses();
    }

    public long getElapsedTime() {
        return matchTime.getTime();
    }

    public long getRemainingTime() {
        return Math.max(0, IConstantesNerellConfig.matchTimeMs - matchTime.getTime());
    }

    @Setter(AccessLevel.NONE)
    private EModeCalage calageBordure = null;

    @Setter(AccessLevel.NONE)
    private double calageBordureDistance = 0;

    public void enableCalageBordureArriere() {
        log.info("Activation calage bordure arrière");
        calageBordure = EModeCalage.ARRIERE;
    }

    public void disableCalageBordure() {
        log.info("Désactivation calage bordure");
        calageBordure = null;
    }

    public void enableCalageBordureAvant(double dst) {
        log.info("Activation calage bordure avant, {}mm", dst);
        calageBordure = EModeCalage.AVANT;
        calageBordureDistance = dst;
    }

    @Setter(AccessLevel.NONE)
    private boolean carouselEnabled = false;

    public void enableCarousel() {
        log.info("Activation du carousel");
        carouselEnabled = true;
    }

    public void disableCarousel() {
        log.info("Désactivation du carousel");
        carouselEnabled = false;
    }

    @Setter(AccessLevel.NONE)
    private boolean magasinEnabled = false;

    public void enableMagasin() {
        log.info("Activation du magasin");
        magasinEnabled = true;
    }

    public void disableMagasin() {
        log.info("Désactivation du magasin");
        magasinEnabled = false;
    }

    @Setter(AccessLevel.NONE)
    private boolean ventousesEnabled = false;

    public void enableVentouses() {
        log.info("Activation des ventouses");
        ventousesEnabled = true;
    }

    public void disableVentouses() {
        log.info("Désactivation des ventouses");
        ventousesEnabled = false;
    }

    @Setter(AccessLevel.NONE)
    private boolean serrageEnabled = false;

    public void enableSerrage() {
        log.info("Activation du serrage");
        serrageEnabled = true;
    }

    public void disableSerrage() {
        log.info("Désactivation du serrage");
        serrageEnabled = false;
    }

    private boolean baliseOk = false;
    private StatutBalise statutBalise = null;

    @Setter(AccessLevel.NONE)
    private Map<ESide, List<CouleurPalet>> magasin = new EnumMap<>(ESide.class);

    private boolean trouNoirVioletVisite = false;
    private boolean trouNoirJauneVisite = false;

    @Setter(AccessLevel.NONE)
    private List<CouleurPalet> paletsInAccelerateur = new ArrayList<>();

    @Setter(AccessLevel.NONE)
    private List<CouleurPalet> paletsInBalance = new ArrayList<>();

    @Setter(AccessLevel.NONE)
    private Map<Integer, CouleurPalet> paletsPetitDistributeur = new HashMap<>();
    @Setter(AccessLevel.NONE)
    private Map<Integer, CouleurPalet> paletsGrandDistributeurEquipe = new HashMap<>();
    @Setter(AccessLevel.NONE)
    private Map<Integer, CouleurPalet> paletsGrandDistributeurAdverse = new HashMap<>();

    @Setter(AccessLevel.NONE)
    private List<CouleurPalet> paletsInTableauRouge = new ArrayList<>();
    @Setter(AccessLevel.NONE)
    private List<CouleurPalet> paletsInTableauVert = new ArrayList<>();
    @Setter(AccessLevel.NONE)
    private List<CouleurPalet> paletsInTableauBleu = new ArrayList<>();

    private boolean accelerateurOuvert = false;

    private boolean accelerateurPrit = false;

    private boolean goldeniumPrit = false;

    /**
     * Coté pour l'accelerateur, le distributeur et la balance
     */
    public ESide mainSide() {
        return team == Team.VIOLET ? ESide.DROITE : ESide.GAUCHE;
    }

    public int getNbDeposesTableau() {
        return Math.max(paletsInTableauRouge.size(), paletsInTableauVert.size());
    }

    public void transfertMagasinTableau(boolean onlyRed) {
        if (onlyRed) {
            paletsInTableauRouge.addAll(magasin.get(ESide.DROITE));
            paletsInTableauRouge.addAll(magasin.get(ESide.GAUCHE));
        }
        else if (team == Team.VIOLET) {
            paletsInTableauRouge.addAll(magasin.get(ESide.DROITE));
            paletsInTableauVert.addAll(magasin.get(ESide.GAUCHE));
        } else {
            paletsInTableauVert.addAll(magasin.get(ESide.DROITE));
            paletsInTableauRouge.addAll(magasin.get(ESide.GAUCHE));
        }

        magasin.get(ESide.DROITE).clear();
        magasin.get(ESide.GAUCHE).clear();
    }

    public void transfertPinceTableau(boolean paletPinceDroit, boolean paletPinceGauche) {
        if (getTeam() == Team.JAUNE) {
            if (paletPinceDroit) {
                paletsInTableauVert.add(CouleurPalet.INCONNU);
            }
            if (paletPinceGauche) {
                paletsInTableauRouge.add(CouleurPalet.INCONNU);
            }
        } else {
            if (paletPinceDroit) {
                paletsInTableauRouge.add(CouleurPalet.INCONNU);
            }
            if (paletPinceGauche) {
                paletsInTableauVert.add(CouleurPalet.INCONNU);
            }
        }
    }

    public void transfertVentouseTableau(CouleurPalet paletDroite, CouleurPalet paletGauche) {
        if (getTeam() == Team.JAUNE) {
            if (paletDroite != null) {
                paletsInTableauVert.add(paletDroite);
            }
            if (paletGauche != null) {
                paletsInTableauRouge.add(paletGauche);
            }
        } else {
            if (paletDroite != null) {
                paletsInTableauRouge.add(paletDroite);
            }
            if (paletGauche != null) {
                paletsInTableauVert.add(paletGauche);
            }
        }
    }

    /**
     * Retourne le meilleur côté pour stocker un palet dans le magasin
     * null si tout est plein
     */
    public ESide getSideMagasin(CouleurPalet couleur) {
        if (team == Team.VIOLET) {
            if (couleur == CouleurPalet.ROUGE && magasin.get(ESide.DROITE).size() < IConstantesNerellConfig.nbPaletsMagasinMax) {
                return ESide.DROITE;
            } else if (magasin.get(ESide.GAUCHE).size() < IConstantesNerellConfig.nbPaletsMagasinMax) {
                return ESide.GAUCHE;
            }
        } else {
            if (couleur == CouleurPalet.ROUGE && magasin.get(ESide.GAUCHE).size() < IConstantesNerellConfig.nbPaletsMagasinMax) {
                return ESide.GAUCHE;
            } else if (magasin.get(ESide.DROITE).size() < IConstantesNerellConfig.nbPaletsMagasinMax) {
                return ESide.DROITE;
            }
        }
        return null;
    }

    /**
     * INIT
     */
    @Override
    public void afterPropertiesSet() {
        // le palet extrème côté adverse ne peut pas être prit
        // sinon on rentre dans la zone de départ adverse
        paletsGrandDistributeurAdverse.put(0, null);
        paletsGrandDistributeurAdverse.put(1, CouleurPalet.VERT);
        paletsGrandDistributeurAdverse.put(2, CouleurPalet.ROUGE);
        paletsGrandDistributeurAdverse.put(3, CouleurPalet.BLEU);
        paletsGrandDistributeurAdverse.put(4, CouleurPalet.ROUGE);
        paletsGrandDistributeurAdverse.put(5, CouleurPalet.VERT);

        paletsGrandDistributeurEquipe.put(0, CouleurPalet.ROUGE);
        paletsGrandDistributeurEquipe.put(1, CouleurPalet.VERT);
        paletsGrandDistributeurEquipe.put(2, CouleurPalet.ROUGE);
        paletsGrandDistributeurEquipe.put(3, CouleurPalet.BLEU);
        paletsGrandDistributeurEquipe.put(4, CouleurPalet.ROUGE);
        paletsGrandDistributeurEquipe.put(5, CouleurPalet.VERT);

        paletsPetitDistributeur.put(0, CouleurPalet.BLEU);
        paletsPetitDistributeur.put(1, CouleurPalet.VERT);
        paletsPetitDistributeur.put(2, CouleurPalet.ROUGE);

        magasin.put(ESide.DROITE, new ArrayList<>());
        magasin.put(ESide.GAUCHE, new ArrayList<>());
    }

    public int calculerPoints() {
        int points = 40; // experience
        points += pointsTableau(paletsInTableauRouge, CouleurPalet.ROUGE);
        points += pointsTableau(paletsInTableauVert, CouleurPalet.VERT);
        points += pointsTableau(paletsInTableauBleu, CouleurPalet.BLEU);
        points += pointsBalance();
        points += 10 * paletsInAccelerateur.size();
        points += accelerateurOuvert ? 10 : 0;
        points += goldeniumPrit ? 20 : 0;
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

    private int pointsTableau(final List<CouleurPalet> tableau, CouleurPalet couleur) {
        return tableau.stream()
                .filter(Objects::nonNull)
                .mapToInt(c -> 1 + ((CouleurPalet.GOLD == c || couleur == c) ? 5 : 0))
                .sum();
    }

    private int pointsBalance() {
        return paletsInBalance.stream()
                .filter(Objects::nonNull)
                .sorted(Comparator.comparingInt(CouleurPalet::getImportance))
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

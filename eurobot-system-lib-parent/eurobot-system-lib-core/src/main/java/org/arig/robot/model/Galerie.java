package org.arig.robot.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class Galerie {

    public enum Periode {
        BLEU, BLEU_VERT, VERT, ROUGE_VERT, ROUGE
    }

    public enum Etage {
        BAS, HAUT, CENTRE, DOUBLE
    }

    @Getter
    @Accessors(fluent = true)
    @RequiredArgsConstructor
    public class GaleriePosition {
        private final Periode periode;
        private final Etage etage;
    }

    private static final int MAX_DEPOSE = 2;
    private static final int MAX_DEPOSE_VERT = 1;

    private final List<CouleurEchantillon> bleu = new ArrayList<>(MAX_DEPOSE);
    private final List<CouleurEchantillon> bleuVert = new ArrayList<>(MAX_DEPOSE);
    private final List<CouleurEchantillon> vert = new ArrayList<>(MAX_DEPOSE_VERT);
    private final List<CouleurEchantillon> rougeVert = new ArrayList<>(MAX_DEPOSE);
    private final List<CouleurEchantillon> rouge = new ArrayList<>(MAX_DEPOSE);

    public void addRouge(final CouleurEchantillon echantillon) {
        rouge.add(echantillon);
    }

    public void addRougeVert(final CouleurEchantillon echantillon) {
        rougeVert.add(echantillon);
    }

    public void addVert(final CouleurEchantillon echantillon) {
        vert.add(echantillon);
    }

    public void addBleuVert(final CouleurEchantillon echantillon) {
        bleuVert.add(echantillon);
    }

    public void addBleu(final CouleurEchantillon echantillon) {
        bleu.add(echantillon);
    }

    boolean complete() {
        return emplacementDisponible() == 0;
    }

    boolean bleuComplete() {
        return bleu.size() == MAX_DEPOSE;
    }

    int emplacementDisponible() {
        return 9 - (bleu.size() + bleuVert.size() + vert.size() + rougeVert.size() + rouge.size());
    }

    private final List<Pair<CouleurEchantillon, CouleurEchantillon>> PERMUTATIONS_ROUGE = Arrays.asList(
            Pair.of(CouleurEchantillon.ROUGE, CouleurEchantillon.ROUGE),
            Pair.of(CouleurEchantillon.ROCHER_ROUGE, CouleurEchantillon.ROUGE)
    );

    private final List<Pair<CouleurEchantillon, CouleurEchantillon>> PERMUTATIONS_BLEU = Arrays.asList(
            Pair.of(CouleurEchantillon.BLEU, CouleurEchantillon.BLEU),
            Pair.of(CouleurEchantillon.ROCHER_BLEU, CouleurEchantillon.BLEU)
    );

    private final List<Pair<CouleurEchantillon, CouleurEchantillon>> PERMUTATIONS_ROUGE_VERT = Arrays.asList(
            Pair.of(CouleurEchantillon.ROUGE, CouleurEchantillon.VERT),
            Pair.of(CouleurEchantillon.VERT, CouleurEchantillon.ROUGE),
            Pair.of(CouleurEchantillon.ROCHER_VERT, CouleurEchantillon.ROUGE),
            Pair.of(CouleurEchantillon.ROCHER_ROUGE, CouleurEchantillon.VERT)
    );

    private final List<Pair<CouleurEchantillon, CouleurEchantillon>> PERMUTATIONS_BLEU_VERT = Arrays.asList(
            Pair.of(CouleurEchantillon.BLEU, CouleurEchantillon.VERT),
            Pair.of(CouleurEchantillon.VERT, CouleurEchantillon.BLEU),
            Pair.of(CouleurEchantillon.ROCHER_VERT, CouleurEchantillon.BLEU),
            Pair.of(CouleurEchantillon.ROCHER_BLEU, CouleurEchantillon.VERT)
    );

    GaleriePosition bestPositionDoubleDepose(CouleurEchantillon couleur1, CouleurEchantillon couleur2, Periode currentPeriode) {
        if (couleur2 == null) {
            return bestPosition(couleur1, currentPeriode);
        }

        log.info("Demande de stockage galerie pour échantillons {} {} depuis la période {}", couleur1, couleur2, currentPeriode);

        // on est capable d'en poser deux de la bonne couleur
        final Pair<CouleurEchantillon, CouleurEchantillon> couleurs = Pair.of(couleur1, couleur2);
        if (PERMUTATIONS_ROUGE.contains(couleurs) && rouge.isEmpty()) {
            return new GaleriePosition(Periode.ROUGE, Etage.DOUBLE);
        }
        if (PERMUTATIONS_BLEU.contains(couleurs) && bleu.isEmpty()) {
            return new GaleriePosition(Periode.BLEU, Etage.DOUBLE);
        }
        if ((PERMUTATIONS_ROUGE.contains(couleurs) || PERMUTATIONS_ROUGE_VERT.contains(couleurs)) && rougeVert.isEmpty()) {
            return new GaleriePosition(Periode.ROUGE_VERT, Etage.DOUBLE);
        }
        if ((PERMUTATIONS_BLEU.contains(couleurs) || PERMUTATIONS_BLEU_VERT.contains(couleurs)) && bleuVert.isEmpty()) {
            return new GaleriePosition(Periode.BLEU_VERT, Etage.DOUBLE);
        }

        // on est capable d'en poser au moins un de la bonne couleur
        if ((couleur1.isRouge() || couleur2 == CouleurEchantillon.ROUGE) && rouge.isEmpty()) {
            return new GaleriePosition(Periode.ROUGE, Etage.DOUBLE);
        }
        if ((couleur1.isBleu() || couleur2 == CouleurEchantillon.BLEU) && bleu.isEmpty()) {
            return new GaleriePosition(Periode.BLEU, Etage.DOUBLE);
        }
        if ((couleur1.isRouge() || couleur1.isVert() || couleur2 == CouleurEchantillon.ROUGE || couleur2 == CouleurEchantillon.VERT) && rougeVert.isEmpty()) {
            return new GaleriePosition(Periode.ROUGE_VERT, Etage.DOUBLE);
        }
        if ((couleur1.isBleu() || couleur1.isVert() || couleur2 == CouleurEchantillon.BLEU || couleur2 == CouleurEchantillon.VERT) && bleuVert.isEmpty()) {
            return new GaleriePosition(Periode.BLEU_VERT, Etage.DOUBLE);
        }

        log.info("Pas de combinaison double trouvée");

        // on en pose qu'un
        return bestPosition(couleur1, currentPeriode);
    }

    GaleriePosition bestPosition(CouleurEchantillon couleur, Periode currentPeriode) {
        log.info("Demande de stockage galerie pour échantillon {} depuis la période {}", couleur, currentPeriode);

        // On commence dans le cas ou, on est sur une période déja en place, ou pas de période pour une couleur donné.
        if (couleur == CouleurEchantillon.BLEU || couleur == CouleurEchantillon.ROCHER_BLEU) {
            if (bleu.size() < MAX_DEPOSE && (currentPeriode == Periode.BLEU || currentPeriode == null)) {
                return new GaleriePosition(Periode.BLEU, bleu.isEmpty() ? Etage.BAS : Etage.HAUT);
            } else if (bleuVert.size() < MAX_DEPOSE && (currentPeriode == Periode.BLEU_VERT || currentPeriode == null)) {
                return new GaleriePosition(Periode.BLEU_VERT, bleuVert.isEmpty() ? Etage.BAS : Etage.HAUT);
            } else if (currentPeriode != null) {
                return bestPosition(couleur, null); // Retry sans période préférée
            }

        } else if (couleur == CouleurEchantillon.ROUGE || couleur == CouleurEchantillon.ROCHER_ROUGE) {
            if (rouge.size() < MAX_DEPOSE && (currentPeriode == Periode.ROUGE || currentPeriode == null)) {
                return new GaleriePosition(Periode.ROUGE, rouge.isEmpty() ? Etage.BAS : Etage.HAUT);
            } else if (rougeVert.size() < MAX_DEPOSE && (currentPeriode == Periode.ROUGE_VERT || currentPeriode == null)) {
                return new GaleriePosition(Periode.ROUGE_VERT, rougeVert.isEmpty() ? Etage.BAS : Etage.HAUT);
            } else if (currentPeriode != null) {
                return bestPosition(couleur, null); // Retry sans période préférée
            }

        } else if (couleur == CouleurEchantillon.VERT || couleur == CouleurEchantillon.ROCHER_VERT) {
            if (vert.isEmpty() && (currentPeriode == Periode.VERT || currentPeriode == null)
                    && !bleuVert.isEmpty() && !rougeVert.isEmpty()) {
                return new GaleriePosition(Periode.VERT, Etage.CENTRE);
            } else if (bleuVert.size() < MAX_DEPOSE && (currentPeriode == Periode.BLEU_VERT || currentPeriode == null)) {
                return new GaleriePosition(Periode.BLEU_VERT, bleuVert.isEmpty() ? Etage.BAS : Etage.HAUT);
            } else if (rougeVert.size() < MAX_DEPOSE && (currentPeriode == Periode.ROUGE_VERT || currentPeriode == null)) {
                return new GaleriePosition(Periode.ROUGE_VERT, rougeVert.isEmpty() ? Etage.BAS : Etage.HAUT);
            } else if (currentPeriode != null) {
                return bestPosition(couleur, null); // Retry sans période préférée
            }
        }

        // A partir d'ici, pour la couleur demandé et eventuellement une période préféré, on a rien qui match.
        // 1. Si il reste de la place a notre position, on répond celle-ci.
        if (currentPeriode == Periode.BLEU && bleu.size() < MAX_DEPOSE) {
            return new GaleriePosition(Periode.BLEU, bleu.isEmpty() ? Etage.BAS : Etage.HAUT);
        } else if (currentPeriode == Periode.BLEU_VERT && bleuVert.size() < MAX_DEPOSE) {
            return new GaleriePosition(Periode.BLEU_VERT, bleu.isEmpty() ? Etage.BAS : Etage.HAUT);
        } else if (currentPeriode == Periode.ROUGE && rouge.size() < MAX_DEPOSE) {
            return new GaleriePosition(Periode.ROUGE, bleu.isEmpty() ? Etage.BAS : Etage.HAUT);
        } else if (currentPeriode == Periode.ROUGE_VERT && rougeVert.size() < MAX_DEPOSE) {
            return new GaleriePosition(Periode.ROUGE_VERT, bleu.isEmpty() ? Etage.BAS : Etage.HAUT);
        }

        // 2. Sinon, on cherche une période qui n'est pas pleine.
        if (bleu.size() < MAX_DEPOSE) {
            return new GaleriePosition(Periode.BLEU, bleu.isEmpty() ? Etage.BAS : Etage.HAUT);
        } else if (bleuVert.size() < MAX_DEPOSE) {
            return new GaleriePosition(Periode.BLEU_VERT, bleuVert.isEmpty() ? Etage.BAS : Etage.HAUT);
        } else if (rouge.size() < MAX_DEPOSE) {
            return new GaleriePosition(Periode.ROUGE, rouge.isEmpty() ? Etage.BAS : Etage.HAUT);
        } else if (rougeVert.size() < MAX_DEPOSE) {
            return new GaleriePosition(Periode.ROUGE_VERT, rougeVert.isEmpty() ? Etage.BAS : Etage.HAUT);
        } else if (vert.size() < MAX_DEPOSE_VERT) {
            return new GaleriePosition(Periode.VERT, Etage.CENTRE);
        } else {
            // Ne peut pas arriver normalement
            return null;
        }
    }

    int score() {
        AtomicInteger points = new AtomicInteger(0);
        rouge.forEach(couleurEchantillon -> points.addAndGet(couleurEchantillon == CouleurEchantillon.ROUGE ? 6 : 3));
        rougeVert.forEach(couleurEchantillon -> points.addAndGet(couleurEchantillon == CouleurEchantillon.ROUGE || couleurEchantillon == CouleurEchantillon.VERT ? 6 : 3));
        vert.forEach(couleurEchantillon -> points.addAndGet(couleurEchantillon == CouleurEchantillon.VERT ? 6 : 3));
        bleuVert.forEach(couleurEchantillon -> points.addAndGet(couleurEchantillon == CouleurEchantillon.VERT || couleurEchantillon == CouleurEchantillon.BLEU ? 6 : 3));
        bleu.forEach(couleurEchantillon -> points.addAndGet(couleurEchantillon == CouleurEchantillon.BLEU ? 6 : 3));
        return points.get();
    }
}

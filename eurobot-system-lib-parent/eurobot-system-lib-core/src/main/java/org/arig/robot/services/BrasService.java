package org.arig.robot.services;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.EurobotConfig;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.model.CouleurEchantillon;
import org.arig.robot.model.EurobotStatus;
import org.arig.robot.model.RobotConfig;
import org.arig.robot.model.bras.PositionBras;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * API de haut niveau pour les bras
 */
@Slf4j
@Service
public class BrasService extends BrasServiceInternal {

    private static final int DST_BORDURE = EurobotConfig.ECHANTILLON_SIZE;

    private final RobotConfig config;
    private final CommonIOService io;
    private final TrajectoryManager mv;

    private CouleurEchantillon couleurPrecedente = null;

    public BrasService(final AbstractCommonServosService servos,
                       final ThreadPoolExecutor executor,
                       final RobotConfig config,
                       final EurobotStatus rs,
                       final CommonIOService io,
                       final TrajectoryManager mv) {
        super(servos, executor, rs);
        this.config = config;
        this.io = io;
        this.mv = mv;
    }

    public enum TypePrise {
        SOL,
        BORDURE,
        DISTRIBUTEUR
    }

    public CompletableFuture<Boolean> initPrise(TypePrise typePrise) {
        return initPrise(typePrise, false);
    }

    public CompletableFuture<Boolean> initPrise(@NonNull final TypePrise typePrise, boolean skipCheck) {
        return CompletableFuture.supplyAsync(() -> {
            log.info("Init prise d'échantillon @ {}", typePrise);

            if (!skipCheck && typePrise == TypePrise.SOL && !io.presencePriseBras()) {
                log.warn("Prise impossible, rien à prendre");
                return false;
            }

            setBrasHaut(PositionBras.HORIZONTAL);

            switch (typePrise) {
                case SOL:
                    setBrasBas(PositionBras.SOL_PRISE);
                    break;
                case BORDURE:
                    setBrasBas(PositionBras.BORDURE_APPROCHE);
                    break;
                case DISTRIBUTEUR:
                    setBrasBas(PositionBras.DISTRIBUTEUR_PRISE);
                    break;
            }

            return true;
        }, executor);
    }

    public CompletableFuture<Boolean> processPrise(@NonNull final TypePrise typePrise) {
        return processPrise(typePrise, DST_BORDURE);
    }

    public CompletableFuture<Boolean> processPrise(@NonNull final TypePrise typePrise, int distanceReculBordure) {
        return CompletableFuture.supplyAsync(() -> {
            log.info("Process prise d'échantillon @ {}", typePrise);

            io.enablePompeVentouseBas();
            if (typePrise == TypePrise.BORDURE) {
                setBrasBas(PositionBras.BORDURE_PRISE);
            }

            boolean pompeOk = ThreadUtils.waitUntil(io::presenceVentouseBas, config.i2cReadTimeMs(), config.timeoutPompe());
            if (typePrise == TypePrise.BORDURE) {
                setBrasBas(PositionBras.BORDURE_APPROCHE);
                try {
                    mv.reculeMM(distanceReculBordure);
                } catch (AvoidingException e) {
                    log.warn("Erreur lors de déplacement depuis la prise bordure", e);
                }
            }

            if (!pompeOk) {
                log.warn("Pas de présence ventouse bas");
                io.releasePompeVentouseBas();
                return false;
            }

            return true;
        }, executor);
    }

    public CompletableFuture<Boolean> stockagePrise(@NonNull final TypePrise typePrise, final CouleurEchantillon c) {
        final CouleurEchantillon.Atomic couleur = new CouleurEchantillon.Atomic(c != null ? c : CouleurEchantillon.INCONNU);

        // Lecture de la couleur
        io.enableLedCapteurCouleur();
        if (couleur.isNeedsLecture()) {
            ThreadUtils.sleep(config.waitLed());
            couleur.set(ThreadUtils.waitUntil(io::couleurVentouseBas, CouleurEchantillon.INCONNU, config.i2cReadTimeMs(), config.timeoutColor()));
        }

        // premier mouvement synchrone
        if (couleur.isNeedsEchange()) {
            setBrasBas(typePrise == TypePrise.BORDURE ? PositionBras.ECHANGE_2 : PositionBras.ECHANGE);
        } else {
            setBrasBas(PositionBras.STOCK_ENTREE);
        }

        return CompletableFuture.supplyAsync(() -> {
            int indexStock = rs.indexStockage();

            if (couleur.isNeedsEchange()) {
                // echange
                setBrasHaut(PositionBras.ECHANGE);

                io.enablePompeVentouseHaut();
                io.releasePompeVentouseBas();

                if (!ThreadUtils.waitUntil(io::presenceVentouseHaut, config.i2cReadTimeMs(), config.timeoutPompe())) {
                    log.warn("Pas de présence ventouse haut");
                    io.releasePompeVentouseHaut();
                    setBrasHaut(PositionBras.HORIZONTAL);
                    // pour que le truc se détache
                    setBrasBas(typePrise == TypePrise.DISTRIBUTEUR ? PositionBras.DISTRIBUTEUR_PRISE : PositionBras.HORIZONTAL);
                    setBrasBas(typePrise == TypePrise.DISTRIBUTEUR ? PositionBras.DISTRIBUTEUR_PRISE : PositionBras.STOCK_ENTREE);
                    return false;
                }

                couleur.reverseColor();

                // 2nd lecture de la couleur
                if (couleur.isNeedsLecture()) {
                    couleur.set(ThreadUtils.waitUntil(io::couleurVentouseHaut, CouleurEchantillon.INCONNU, config.i2cReadTimeMs(), config.timeoutColor()));
                    if (couleur.get() == CouleurEchantillon.ROCHER) {
                        log.warn("Après échange la couleur est toujours un rocher ?!");
                    }
                }

                setBrasBas(typePrise == TypePrise.DISTRIBUTEUR ? PositionBras.DISTRIBUTEUR_PRISE : PositionBras.HORIZONTAL);

                // stockage
                setBrasHaut(PositionBras.STOCK_ENTREE);
                setBrasHaut(PositionBras.stockDepose(indexStock));

                io.releasePompeVentouseHaut();
                if (!ThreadUtils.waitUntil(() -> !io.presenceVentouseHaut(), config.i2cReadTimeMs(), config.timeoutPompe())) {
                    log.warn("Echec de libération ventouse haut ?");
                }

                setBrasHaut(PositionBras.STOCK_ENTREE);
                setBrasHaut(PositionBras.HORIZONTAL);

                setBrasBas(typePrise == TypePrise.DISTRIBUTEUR ? PositionBras.DISTRIBUTEUR_PRISE : PositionBras.STOCK_ENTREE);

            } else {
                // stockage
                setBrasBas(PositionBras.stockDepose(indexStock));

                io.releasePompeVentouseBas();
                if (!ThreadUtils.waitUntil(() -> !io.presenceVentouseBas(), config.i2cReadTimeMs(), config.timeoutPompe())) {
                    log.warn("Echec de libération ventouse bas ?");
                }

                setBrasBas(PositionBras.STOCK_ENTREE);

                if (typePrise == TypePrise.DISTRIBUTEUR) {
                    setBrasBas(PositionBras.DISTRIBUTEUR_PRISE);
                }
            }

            boolean ok = false;
            if (io.presenceStock(indexStock)) {
                // cas pourri ou le précédent stockage c'est mal passé
                // grace a ce stockage, l'échantillon d'avant c'est remis en place
                // il faut donc le compter
                if (indexStock < 5 && io.presenceStock(indexStock + 1)) {
                    log.warn("Prise en compte de l'échantillon précédent mal stocké");
                    rs.stockage(couleurPrecedente != null ? couleurPrecedente : CouleurEchantillon.INCONNU);
                    indexStock++;
                }

                log.info("Stockage d'un {} à l'emplacement {}", couleur.get(), indexStock);
                rs.stockage(couleur.get());
                couleurPrecedente = couleur.get();
                ok = true;

            } else {
                log.warn("Aucun echantillon posé dans le stock");
            }

            return ok;
        }, executor);
    }

    public CompletableFuture<Void> finalizePrise() {
        return CompletableFuture.runAsync(() -> {
            io.disableLedCapteurCouleur();
            setBrasHaut(PositionBras.HORIZONTAL);
            setBrasBas(PositionBras.STOCK_ENTREE);
            updateStock();
            setBrasBas(PositionBras.repos(rs.stockTaille()));
            setBrasHaut(PositionBras.repos(rs.stockTaille()));
        });
    }

    public enum TypeDepose {
        SOL,
        GALERIE_BAS,
        GALERIE_MILIEU,
        GALERIE_HAUT
    }

    public boolean initDepose(@NonNull final TypeDepose typeDepose) {
        CouleurEchantillon couleur = rs.stockFirst();
        if (couleur == null) {
            log.warn("Dépose impossible, le stock est vide");
            return false;
        }

        log.info("Dépose d'échantillon {} @ {}", couleur, typeDepose);

        // préparation
        switch (typeDepose) {
            case SOL:
            case GALERIE_BAS:
            case GALERIE_MILIEU:
                setBrasHaut(PositionBras.HORIZONTAL);
                setBrasBas(PositionBras.STOCK_ENTREE);
                break;

            case GALERIE_HAUT:
                setBrasHaut(PositionBras.HORIZONTAL);
                setBrasBas(PositionBras.STOCK_ENTREE);
                setBrasBas(PositionBras.SOL_DEPOSE); // FIXME bonne position à déterminer
                setBrasHaut(PositionBras.STOCK_ENTREE);
                break;
        }

        return true;
    }

    public CouleurEchantillon processDepose(@NonNull final TypeDepose typeDepose) {
        int indexStock = rs.indexDestockage();
        CouleurEchantillon couleur = rs.stockFirst();

        switch (typeDepose) {
            case SOL:
            case GALERIE_BAS:
            case GALERIE_MILIEU:
                // prise
                setBrasBas(PositionBras.stockPrise(indexStock));

                io.enablePompeVentouseBas();
                if (!ThreadUtils.waitUntil(io::presenceVentouseBas, config.i2cReadTimeMs(), config.timeoutPompe())) {
                    log.warn("Pas de présence ventouse bas");
                    io.releasePompeVentouseBas();
                    setBrasBas(PositionBras.STOCK_ENTREE);
                    return null;
                }

                if (couleur.isNeedsLecture()) {
                    couleur = ThreadUtils.waitUntil(io::couleurVentouseBas, CouleurEchantillon.INCONNU, config.i2cReadTimeMs(), config.timeoutColor());
                    rs.stock()[indexStock] = couleur;
                    log.info("Dernière lecture de la couleur : {}", couleur);
                }

                // depose
                setBrasBas(PositionBras.STOCK_ENTREE);
                setBrasBas(typeDepose == TypeDepose.SOL ? PositionBras.SOL_DEPOSE :
                        typeDepose == TypeDepose.GALERIE_BAS ? PositionBras.GALERIE_DEPOSE :
                                PositionBras.GALERIE_DEPOSE_MILIEU);

                io.releasePompeVentouseBas();
                if (!ThreadUtils.waitUntil(() -> !io.presenceVentouseBas(), config.i2cReadTimeMs(), config.timeoutPompe())) {
                    log.warn("Echec de libération ventouse bas ?");
                }

                setBrasBas(PositionBras.STOCK_ENTREE);
                break;

            case GALERIE_HAUT:
                // prise
                setBrasHaut(PositionBras.stockPrise(indexStock));

                io.enablePompeVentouseHaut();
                if (!ThreadUtils.waitUntil(io::presenceVentouseHaut, config.i2cReadTimeMs(), config.timeoutPompe())) {
                    log.warn("Pas de présence ventouse haut");
                    io.releasePompeVentouseBas();
                    setBrasHaut(PositionBras.STOCK_ENTREE);
                    return null;
                }

                if (couleur.isNeedsLecture()) {
                    couleur = ThreadUtils.waitUntil(io::couleurVentouseHaut, CouleurEchantillon.INCONNU, config.i2cReadTimeMs(), config.timeoutColor());
                    rs.stock()[indexStock] = couleur;
                    log.info("Dernière lecture de la couleur : {}", couleur);
                }

                // depose
                setBrasHaut(PositionBras.STOCK_ENTREE);
                setBrasHaut(PositionBras.GALERIE_DEPOSE);

                io.releasePompeVentouseHaut();
                if (!ThreadUtils.waitUntil(() -> !io.presenceVentouseHaut(), config.i2cReadTimeMs(), config.timeoutPompe())) {
                    log.warn("Echec de libération ventouse haut ?");
                }

                setBrasHaut(PositionBras.STOCK_ENTREE);
                break;
        }

        couleur = rs.destockage();

        log.info("Echantillon {} retiré du stock", couleur);

        return couleur;
    }

    public void finalizeDepose() {
        setBrasHaut(PositionBras.HORIZONTAL);
        setBrasBas(PositionBras.STOCK_ENTREE);
        updateStock();
        setBrasBas(PositionBras.repos(rs.stockTaille()));
        setBrasHaut(PositionBras.repos(rs.stockTaille()));
    }

    /**
     * Force la mise à jour du stock en fonction des capteurs
     * Pour gérer des cas de mauvaise détection
     */
    public void updateStock() {
        for (int i = 0; i < rs.stock().length; i++) {
            if (io.presenceStock(i) && rs.stock()[i] == null) {
                log.warn("Nouvel échantillon détecté dans le stock {}", (i + 1));
                rs.stock()[i] = CouleurEchantillon.INCONNU; // FIXME : Réordonner le stock
            } else if (!io.presenceStock(i) && rs.stock()[i] != null) {
                log.warn("échantillon perdu dans le stock {}", (i + 1));
                rs.stock()[i] = null;
            }
        }
        for (int i = rs.stock().length - 1; i > 0; i--) {
            if (rs.stock()[i] != null && rs.stock()[i - 1] == null) {
                log.warn("Trou dans le stock à la position {}", i);
            }
        }
    }

}

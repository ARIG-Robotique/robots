package org.arig.robot.services;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.exception.AvoidingException;
import org.arig.robot.model.CouleurEchantillon;
import org.arig.robot.model.EurobotStatus;
import org.arig.robot.model.RobotConfig;
import org.arig.robot.model.bras.PositionBras;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.stereotype.Service;

/**
 * API de haut niveau pour les bras
 */
@Slf4j
@Service
public class BrasService extends BrasServiceInternal {

    private static final int DST_BORDURE = 50; // TODO

    private final RobotConfig config;
    private final EurobotStatus rs;
    private final CommonIOService io;
    private final TrajectoryManager mv;

    public BrasService(final AbstractCommonServosService servos,
                       final RobotConfig config,
                       final EurobotStatus rs,
                       final CommonIOService io,
                       final TrajectoryManager mv) {
        super(servos);
        this.config = config;
        this.rs = rs;
        this.io = io;
        this.mv = mv;
    }

    public enum TypePrise {
        SOL,
        BORDURE,
        DISTRIBUTEUR
    }

    public boolean prise(@NonNull final TypePrise typePrise, CouleurEchantillon couleur) {
        if (couleur == null) {
            couleur = CouleurEchantillon.INCONNU;
        }

        log.info("Prise d'échantillon {} @ {}", couleur, typePrise);

        int indexStock = rs.indexStockage();
        if (indexStock == -1) {
            log.warn("Prise impossible, le stock est plein");
            return false;
        }

        if (typePrise == TypePrise.SOL && !io.presencePriseBras()) {
            log.warn("Prise impossible, rien à prendre");
            return false;
        }

        // préparation
        setBrasHaut(PositionBras.HORIZONTAL);
        setBrasBas(PositionBras.STOCK_ENTREE);

        // prise
        switch (typePrise) {
            case SOL:
                setBrasBas(PositionBras.SOL_PRISE);
                break;
            case BORDURE:
                setBrasBas(PositionBras.BORDURE_APPROCHE);
                try {
                    mv.avanceMM(DST_BORDURE);
                } catch (AvoidingException e) {
                    e.printStackTrace();
                }
                setBrasBas(PositionBras.BORDURE_PRISE);
                break;
            case DISTRIBUTEUR:
                // TODO
                return false;
        }

        io.enableLedCapteurCouleur();
        io.enablePompeVentouseBas();
        boolean pompeOk = ThreadUtils.waitUntil(io::presenceVentouseBas, config.i2cReadTimeMs(), config.timeoutPompe());

        if (typePrise == TypePrise.BORDURE) {
            setBrasBas(PositionBras.BORDURE_APPROCHE);
            try {
                mv.reculeMM(DST_BORDURE);
            } catch (AvoidingException e) {
                e.printStackTrace();
            }
        }

        if (!pompeOk) {
            log.warn("Pas de présence ventouse bas");
            io.releasePompeVentouseBas();
            setBrasBas(PositionBras.STOCK_ENTREE);
            return false;
        }

        // lecture de la couleur
        if (couleur.isNeedsLecture()) {
            couleur = ThreadUtils.waitUntil(io::couleurVentouseBas, CouleurEchantillon.INCONNU, config.i2cReadTimeMs(), config.timeoutColor());
        }

        if (couleur.isNeedsEchange()) {
            // echange
            setBrasBas(typePrise == TypePrise.BORDURE ? PositionBras.ECHANGE_2 : PositionBras.ECHANGE);
            setBrasHaut(PositionBras.ECHANGE);

            io.enablePompeVentouseHaut();
            io.releasePompeVentouseBas();

            if (!ThreadUtils.waitUntil(io::presenceVentouseHaut, config.i2cReadTimeMs(), config.timeoutPompe())) {
                log.warn("Pas de présence ventouse haut");
                io.releasePompeVentouseHaut();
                setBrasHaut(PositionBras.HORIZONTAL);
                setBrasBas(PositionBras.HORIZONTAL); // pour que le truc se détache
                setBrasBas(PositionBras.STOCK_ENTREE);
                return false;
            }

            couleur = couleur.getReverseColor();

            // 2nd lecture de la couleur
            if (couleur.isNeedsLecture()) {
                couleur = ThreadUtils.waitUntil(io::couleurVentouseHaut, CouleurEchantillon.INCONNU, config.i2cReadTimeMs(), config.timeoutColor());
                if (couleur == CouleurEchantillon.ROCHER) {
                    log.warn("Après échange la couleur est toujours un rocher ?!");
                }
            }

            setBrasBas(PositionBras.HORIZONTAL);

            // stockage
            setBrasHaut(PositionBras.STOCK_ENTREE);
            setBrasHaut(PositionBras.stockDepose(indexStock));

            io.releasePompeVentouseHaut();
            if (!ThreadUtils.waitUntil(() -> !io.presenceVentouseHaut(), config.i2cReadTimeMs(), config.timeoutPompe())) {
                log.warn("Echec de libération ventouse haut ?");
            }

            setBrasHaut(PositionBras.STOCK_ENTREE);
            setBrasHaut(PositionBras.HORIZONTAL);
            setBrasBas(PositionBras.STOCK_ENTREE);

        } else {
            // stockage
            setBrasBas(PositionBras.STOCK_ENTREE);
            setBrasBas(PositionBras.stockDepose(indexStock));

            io.releasePompeVentouseBas();
            if (!ThreadUtils.waitUntil(() -> !io.presenceVentouseBas(), config.i2cReadTimeMs(), config.timeoutPompe())) {
                log.warn("Echec de libération ventouse bas ?");
            }

            setBrasBas(PositionBras.STOCK_ENTREE);
        }

        boolean ok = false;
        if (io.presenceStock(indexStock)) {
            // cas pourri ou le précédent stockage c'est mal passé
            // grace a ce stockage, l'échantillon d'avant c'est remis en place
            // il faut donc le compter
            if (indexStock < 5 && io.presenceStock(indexStock + 1)) {
                log.warn("Prise en compte de l'échantillon précédent mal stocké");
                rs.stockage(CouleurEchantillon.INCONNU);
                indexStock++;
            }

            log.info("Stockage d'un {} à l'emplacement {}", couleur, indexStock);
            rs.stockage(couleur);
            ok = true;
        } else {
            log.warn("Aucun echantillon posé dans le stock");
        }

        return ok;
    }

    public void finalizePrise() {
        io.disableLedCapteurCouleur();
        setBrasHaut(PositionBras.HORIZONTAL);
        setBrasBas(PositionBras.REPOS);
        setBrasHaut(PositionBras.REPOS);
        updateStock();
    }

    public enum TypeDepose {
        SOL,
        GALERIE
    }

    public CouleurEchantillon depose(@NonNull final TypeDepose typeDepose) {
        log.info("Dépose d'échantillon @ {}", typeDepose);

        int indexStock = rs.indexDestockage();
        if (indexStock == -1) {
            log.warn("Dépose impossible, le stock est vide");
            return null;
        }

        switch (typeDepose) {
            case SOL:
                // préparation
                setBrasHaut(PositionBras.HORIZONTAL);
                setBrasBas(PositionBras.STOCK_ENTREE);

                // prise
                setBrasBas(PositionBras.stockPrise(indexStock));

                io.enablePompeVentouseBas();
                if (!ThreadUtils.waitUntil(io::presenceVentouseBas, config.i2cReadTimeMs(), config.timeoutPompe())) {
                    log.warn("Pas de présence ventouse bas");
                    io.releasePompeVentouseBas();
                    setBrasBas(PositionBras.STOCK_ENTREE);
                    return null;
                }

                // depose
                setBrasBas(PositionBras.STOCK_ENTREE);
                setBrasBas(PositionBras.SOL_DEPOSE);

                io.releasePompeVentouseBas();
                if (!ThreadUtils.waitUntil(() -> !io.presenceVentouseBas(), config.i2cReadTimeMs(), config.timeoutPompe())) {
                    log.warn("Echec de libération ventouse bas ?");
                }

                setBrasBas(PositionBras.STOCK_ENTREE);

                break;

            case GALERIE:
                // TODO
                return null;
        }

        CouleurEchantillon couleur = rs.destockage();
        log.info("Echantillon {} retiré du stock", couleur);

        return couleur;
    }

    public void finalizeDepose() {
        setBrasHaut(PositionBras.HORIZONTAL);
        setBrasBas(PositionBras.REPOS);
        setBrasHaut(PositionBras.REPOS);
        updateStock();
    }

    /**
     * Force la mise à jour du stock en fonction des capteurs
     * Pour gérer des cas de mauvaise détection
     */
    public void updateStock() {
        for (int i = 0; i < 6; i++) {
            if (io.presenceStock(i) && rs.stock()[i] == null) {
                log.warn("Nouvel échantillon détecté dans le stock {}", (i + 1));
                rs.stock()[i] = CouleurEchantillon.INCONNU;
            } else if (!io.presenceStock(i) && rs.stock()[i] != null) {
                log.warn("échantillon perdu dans le stock {}", (i + 1));
                rs.stock()[i] = null;
            }
        }
    }

}

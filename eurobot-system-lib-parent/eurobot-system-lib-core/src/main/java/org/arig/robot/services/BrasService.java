package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.arig.robot.model.CouleurEchantillon;
import org.arig.robot.model.EurobotStatus;
import org.arig.robot.model.RobotConfig;
import org.arig.robot.model.bras.OptionBras;
import org.arig.robot.model.bras.PositionBras;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * API de haut niveau pour les bras
 */
@Slf4j
@Service
public class BrasService extends BrasServiceInternal {

    private final RobotConfig config;
    private final CommonIOService io;

    public BrasService(final AbstractCommonServosService servos,
                       final ThreadPoolExecutor executor,
                       final RobotConfig config,
                       final EurobotStatus rs,
                       final CommonIOService io) {
        super(servos, executor, rs);
        this.config = config;
        this.io = io;
    }

    /**
     * Activation de la ventouse bas + lecture couleur si besoin
     */
    public boolean waitEnableVentouseBas(CouleurEchantillon couleur) {
        log.info("Prise d'un {} dans la ventouse bas", couleur);

        io.enablePompeVentouseBas();
        boolean ok = ThreadUtils.waitUntil(io::presenceVentouseBas, config.i2cReadTimeMs(), config.timeoutPompe());

        if (ok) {
            rs.ventouseBas(couleur);
            if (couleur.isNeedsLecture()) {
                lectureCouleurBas();
            }
            return true;

        } else {
            log.warn("Pas de présence ventouse bas");
            io.releasePompeVentouseBas();
            return false;
        }
    }

    /**
     * Activation de la ventouse bas + lecture couleur si besoin
     */
    public boolean waitEnableVentouseHaut(CouleurEchantillon couleur) {
        log.info("Prise d'un {} dans la ventouse haut", couleur);

        io.enablePompeVentouseHaut();
        boolean ok = ThreadUtils.waitUntil(io::presenceVentouseHaut, config.i2cReadTimeMs(), config.timeoutPompe());

        if (ok) {
            rs.ventouseHaut(couleur);
            if (couleur.isNeedsLecture()) {
                lectureCouleurBas();
            }
            return true;

        } else {
            log.warn("Pas de présence ventouse haut");
            io.releasePompeVentouseHaut();
            return false;
        }
    }

    /**
     * Libération de la ventouse bas
     */
    public void waitReleaseVentouseBas() {
        log.info("Libération ventouse bas");
        io.releasePompeVentouseBas();
        rs.ventouseBas(null);
        ThreadUtils.waitUntil(() -> !io.presenceVentouseBas(), config.i2cReadTimeMs(), config.timeoutPompe());
    }

    /**
     * Libération de la ventouse haut
     */
    public void waitReleaseVentouseHaut() {
        log.info("Libération ventouse haut");
        io.releasePompeVentouseHaut();
        rs.ventouseHaut(null);
        ThreadUtils.waitUntil(() -> !io.presenceVentouseHaut(), config.i2cReadTimeMs(), config.timeoutPompe());
    }

    /**
     * Lecture de la couleur ventouse bas
     */
    public void lectureCouleurBas() {
        CouleurEchantillon couleur = ThreadUtils.waitUntil(io::couleurVentouseBas, CouleurEchantillon.INCONNU, config.i2cReadTimeMs(), config.timeoutColor());
        log.info("Couleur ventouse bas : {}", couleur);
        rs.ventouseBas(couleur);
    }

    /**
     * Lecture de la couleur ventouse haut
     */
    public void lectureCouleurHaut() {
        CouleurEchantillon couleur = ThreadUtils.waitUntil(io::couleurVentouseHaut, CouleurEchantillon.INCONNU, config.i2cReadTimeMs(), config.timeoutColor());
        log.info("Couleur ventouse haut : {}", couleur);
        rs.ventouseHaut(couleur);
    }

    /**
     * Echange de bas en haut + lecture couleur si besoin
     * Les bras restent en position d'échange
     */
    public boolean echangeBasHaut() {
        return echangeBasHaut(false);
    }

    public boolean echangeBasHaut(boolean fromBordure) {
        CouleurEchantillon couleur = ObjectUtils.firstNonNull(rs.ventouseBas(), CouleurEchantillon.INCONNU);
        log.info("Echange d'un {} du bas vers le haut", couleur);

        setBrasBas(fromBordure ? PositionBras.ECHANGE_2 : PositionBras.ECHANGE, OptionBras.SLOW);
        setBrasHaut(PositionBras.ECHANGE);

        io.enablePompeVentouseHaut();
        io.releasePompeVentouseBas();
        rs.ventouseBas(null);

        if (!ThreadUtils.waitUntil(io::presenceVentouseHaut, config.i2cReadTimeMs(), config.timeoutPompe())) {
            log.warn("Pas de présence ventouse haut");
            io.releasePompeVentouseHaut();
            return false;

        } else {
            couleur = couleur.getReverseColor();
            rs.ventouseHaut(couleur);
            if (couleur.isNeedsLecture()) {
                lectureCouleurHaut();
            }
            return true;
        }
    }

    /**
     * Echange de haut en bas + lecture couleur si besoin
     * Les bras restent en position d'échange
     */
    public boolean echangeHautBas() {
        CouleurEchantillon couleur = ObjectUtils.firstNonNull(rs.ventouseHaut(), CouleurEchantillon.INCONNU);
        log.info("Echange d'un {} du bas vers le haut", couleur);

        setBrasBas(PositionBras.HORIZONTAL);
        setBrasHaut(PositionBras.ECHANGE);
        setBrasBas(PositionBras.ECHANGE);

        io.enablePompeVentouseBas();
        io.releasePompeVentouseHaut();
        rs.ventouseHaut(null);

        if (!ThreadUtils.waitUntil(io::presenceVentouseBas, config.i2cReadTimeMs(), config.timeoutPompe())) {
            log.warn("Pas de présence ventouse bas");
            io.releasePompeVentouseBas();
            return false;

        } else {
            couleur = couleur.getReverseColor();
            rs.ventouseBas(couleur);
            if (couleur.isNeedsLecture()) {
                lectureCouleurBas();
            }
            return true;
        }
    }

    /**
     * Stockage par le bas
     */
    public void stockageBas() {
        CouleurEchantillon couleur = ObjectUtils.firstNonNull(rs.ventouseBas(), CouleurEchantillon.INCONNU);
        log.info("Stockage d'un {} par le bas", couleur);

        setBrasBas(PositionBras.STOCK_ENTREE, OptionBras.SLOW);
        setBrasBas(PositionBras.stockDepose(rs.indexStockage()), OptionBras.SLOW);

        waitReleaseVentouseBas();
        rs.stockage(couleur);

        setBrasBas(PositionBras.STOCK_ENTREE);
    }

    /**
     * Stockage par le haut
     */
    public void stockageHaut() {
        CouleurEchantillon couleur = ObjectUtils.firstNonNull(rs.ventouseHaut(), CouleurEchantillon.INCONNU);
        log.info("Stockage d'un {} par le haut", couleur);

        setBrasHaut(PositionBras.STOCK_ENTREE, OptionBras.SLOW);
        setBrasHaut(PositionBras.stockDepose(rs.indexStockage()), OptionBras.SLOW);

        waitReleaseVentouseHaut();
        rs.stockage(couleur);

        setBrasHaut(PositionBras.STOCK_ENTREE);
    }

    /**
     * Déstockage par le bas
     */
    public boolean destockageBas() {
        int indexStock = rs.indexDestockage();
        CouleurEchantillon couleur = rs.stockFirst();
        log.info("Récupération de l'échantillon {} {} par le bas", indexStock, couleur);

        setBrasBas(PositionBras.STOCK_ENTREE);
        setBrasBas(PositionBras.stockPrise(indexStock));

        boolean ok = waitEnableVentouseBas(couleur);
        setBrasBas(PositionBras.STOCK_ENTREE);
        rs.destockage(); // dans tous les cas on enlève du stock
        return ok;
    }

    /**
     * Déstockage par le haut
     */
    public boolean destockageHaut() {
        int indexStock = rs.indexDestockage();
        CouleurEchantillon couleur = rs.stockFirst();
        log.info("Récupération de l'échantillon {} {} par le haut", indexStock, couleur);

        setBrasHaut(PositionBras.STOCK_ENTREE);
        setBrasHaut(PositionBras.stockPrise(indexStock));

        boolean ok = waitEnableVentouseHaut(couleur);
        setBrasHaut(PositionBras.STOCK_ENTREE);
        rs.destockage(); // dans tous les cas on enlève du stock
        return ok;
    }

    /**
     * Mise au repos + désactivation des pompes
     */
    public void repos() {
        log.info("Positions de repos");
        io.disableLedCapteurCouleur();
        io.releasePompeVentouseHaut();
        io.releasePompeVentouseBas();
        rs.ventouseBas(null);
        rs.ventouseHaut(null);
        setBrasBas(PositionBras.repos(rs.stockTaille()));
        setBrasHaut(PositionBras.repos(rs.stockTaille()));
    }

    /**
     * Force la mise à jour du stock en fonction des capteurs
     * Pour gérer des cas de mauvaise détection
     */
    public void updateStock() {
//        for (int i = 0; i < rs.stock().length; i++) {
//            if (io.presenceStock(i) && rs.stock()[i] == null) {
//                log.warn("Nouvel échantillon détecté dans le stock {}", (i + 1));
//                rs.stock()[i] = CouleurEchantillon.INCONNU; // FIXME : Réordonner le stock
//            } else if (!io.presenceStock(i) && rs.stock()[i] != null) {
//                log.warn("échantillon perdu dans le stock {}", (i + 1));
//                rs.stock()[i] = null;
//            }
//        }
//        for (int i = rs.stock().length - 1; i > 0; i--) {
//            if (rs.stock()[i] != null && rs.stock()[i - 1] == null) {
//                log.warn("Trou dans le stock à la position {}", i);
//            }
//        }
    }

}

package org.arig.robot.model;

import lombok.Data;

/**
 * The Class CommandeRobot.
 *
 * @author gdepuille
 */
@Data
public class CommandeAsservissementPosition {

    /**
     * Position a atteindre pour cette commande
     */
    private long position;

    /**
     * Commande moteur a appliquer
     */
    private CommandeMoteur moteur;

    /**
     * Vitesse a appliquer
     */
    private VitesseAsservissementPosition vitesse;

    /**
     * Consigne de déplacement pour l'asservissement
     */
    private ConsigneAsservissementPosition consigne;

    private boolean frein;

    /**
     * Instantiates a new robot consigne.
     */
    public CommandeAsservissementPosition() {
        position = 0;
        moteur = new CommandeMoteur();
        vitesse = new VitesseAsservissementPosition(50);
        consigne = new ConsigneAsservissementPosition();
        frein = true;
    }
}

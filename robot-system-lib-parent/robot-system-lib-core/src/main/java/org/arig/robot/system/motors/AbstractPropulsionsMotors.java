package org.arig.robot.system.motors;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

/**
 * The Class AbstractMotors.
 *
 * @author gdepuille
 */
@Slf4j
public abstract class AbstractPropulsionsMotors {

    public static final int UNDEF_MOTOR = 0;
    public static final int MOTOR_1 = 1;
    public static final int MOTOR_2 = 2;

    @Getter
    @Accessors(fluent = true)
    private int numMoteurGauche;
    @Getter
    @Accessors(fluent = true)
    private int numMoteurDroit;
    private boolean alternate;

    protected int offsetValue;
    protected int minVal;
    protected int maxVal;
    protected int prevM1;
    protected int prevM2;

    public AbstractPropulsionsMotors(int offsetValue) {
        assignMotors(AbstractPropulsionsMotors.UNDEF_MOTOR, AbstractPropulsionsMotors.UNDEF_MOTOR);
        alternate = false;
        this.offsetValue = offsetValue;
    }

    /**
     * Méthode pour assigner le numéro du moteur pour la commande gauche / droite
     *
     * @param numMoteurGauche the num moteur gauche
     * @param numMoteurDroit  the num moteur droit
     */
    public final void assignMotors(final int numMoteurGauche, final int numMoteurDroit) {
        this.numMoteurGauche = numMoteurGauche;
        this.numMoteurDroit = numMoteurDroit;
    }

    /**
     * Méthode de génération groupé de la commande droite / gauche
     *
     * @param gauche the gauche
     * @param droit  the droit
     */
    public void generateMouvement(final int gauche, final int droit) {
        alternate = !alternate;
        if (alternate) {
            moteurGauche(gauche);
            moteurDroit(droit);
        } else {
            moteurDroit(droit);
            moteurGauche(gauche);
        }
    }

    /**
     * Méthode de commande du moteur gauche (doit être assigné).
     *
     * @param cmd the cmd
     *
     * @throws IllegalStateException the illegal state exception
     */
    public final void moteurGauche(final int cmd) throws IllegalStateException {
        if (numMoteurGauche == AbstractPropulsionsMotors.MOTOR_1) {
            speedMoteur1(cmd);
        } else if (numMoteurGauche == AbstractPropulsionsMotors.MOTOR_2) {
            speedMoteur2(cmd);
        }

        exceptionAssignationMoteurGauche();
    }

    /**
     * Méthode de commande du moteur droit (doit être assigné).
     *
     * @param cmd the cmd
     *
     * @throws IllegalStateException the illegal state exception
     */
    public final void moteurDroit(final int cmd) throws IllegalStateException {
        if (numMoteurDroit == AbstractPropulsionsMotors.MOTOR_1) {
            speedMoteur1(cmd);
        } else if (numMoteurDroit == AbstractPropulsionsMotors.MOTOR_2) {
            speedMoteur2(cmd);
        }

        exceptionAssignationMoteurDroit();
    }

    /**
     * Méthode pour commander l'arret des moteurs configurés
     */
    public void stopAll() {
        stop1();
        stop2();
    }

    /**
     * Méthode d'arrêt du moteur gauche (doit être assigné).
     *
     * @throws IllegalStateException the illegal state exception
     */
    public final void stopGauche() throws IllegalStateException {
        if (numMoteurGauche == AbstractPropulsionsMotors.MOTOR_1) {
            stop1();
        } else if (numMoteurGauche == AbstractPropulsionsMotors.MOTOR_2) {
            stop2();
        }

        exceptionAssignationMoteurGauche();
    }

    /**
     * Méthode d'arret du moteur droit (doit être assigné).
     *
     * @throws IllegalStateException the illegal state exception
     */
    public final void stopDroit() throws IllegalStateException {
        if (numMoteurDroit == AbstractPropulsionsMotors.MOTOR_1) {
            stop1();
        } else if (numMoteurDroit == AbstractPropulsionsMotors.MOTOR_2) {
            stop2();
        }

        exceptionAssignationMoteurDroit();
    }

    /**
     * Méthode d'arrêt du moteur 1
     */
    public void stop1() {
        speedMoteur1(getStopSpeed());
    }

    /**
     * Méthode d'arrêt du moteur 2
     */
    public void stop2() {
        speedMoteur2(getStopSpeed());
    }

    /**
     * Récupération de la valeur pour le stop.
     */
    public int getStopSpeed() {
        return 0;
    }

    /**
     * Vitesse courante du moteur droit
     */
    public Integer currentSpeedDroit() {
        if (numMoteurDroit == AbstractPropulsionsMotors.MOTOR_1) {
            return currentSpeedMoteur1();
        } else if (numMoteurDroit == AbstractPropulsionsMotors.MOTOR_2) {
            return currentSpeedMoteur2();
        }

        return null;
    }

    /**
     * Vitesse courante du moteur gauche
     */
    public Integer currentSpeedGauche() {
        if (numMoteurGauche == AbstractPropulsionsMotors.MOTOR_1) {
            return currentSpeedMoteur1();
        } else if (numMoteurGauche == AbstractPropulsionsMotors.MOTOR_2) {
            return currentSpeedMoteur2();
        }

        return null;
    }

    /**
     * Valeur minimal pour la vitesse du moteur.
     */
    public int getMinSpeed() {
        return minVal - offsetValue;
    }

    /**
     * Valeur maximal pour la vitesse du moteur
     */
    public int getMaxSpeed() {
        return maxVal - offsetValue;
    }

    /**
     * Vitesse courante du moteur 1
     */
    protected int currentSpeedMoteur1() {
        return prevM1 - offsetValue;
    }

    /**
     * Vitesse courante du moteur 2
     */
    protected int currentSpeedMoteur2() {
        return prevM2 - offsetValue;
    }

    public abstract void init();
    public abstract void speedMoteur1(final int val);
    public abstract void speedMoteur2(final int val);

    public abstract void printVersion();

    /**
     * Méthode de contrôle du bornage des commandes moteurs.
     *
     * @param val the val
     *
     * @return the int
     */
    protected int check(final int val) {
        int result = val;
        if (val < minVal) {
            result = minVal;
        }
        if (val > maxVal) {
            result = maxVal;
        }

        return result;
    }

    /**
     * Exception assignation moteur gauche.
     */
    protected void exceptionAssignationMoteurGauche() {
        if (numMoteurGauche == AbstractPropulsionsMotors.UNDEF_MOTOR) {
            log.error("L'assignation du moteur gauche n'est pas faite");
            throw new IllegalStateException("L'assignation du moteur gauche n'est pas faite");
        }
    }

    /**
     * Exception assignation moteur droit.
     */
    protected void exceptionAssignationMoteurDroit() {
        if (numMoteurDroit == AbstractPropulsionsMotors.UNDEF_MOTOR) {
            log.error("L'assignation du moteur droit n'est pas faite");
            throw new IllegalStateException("L'assignation du moteur droit n'est pas faite");
        }
    }
}

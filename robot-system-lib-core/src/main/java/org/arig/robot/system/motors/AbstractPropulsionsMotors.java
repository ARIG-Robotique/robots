package org.arig.robot.system.motors;

import lombok.extern.slf4j.Slf4j;

/**
 * The Class AbstractMotors.
 * 
 * @author mythril
 */
@Slf4j
public abstract class AbstractPropulsionsMotors {

    /** The Constant UNDEF_MOTOR. */
    public static final int UNDEF_MOTOR = 0;

    /** The Constant MOTOR_1. */
    public static final int MOTOR_1 = 1;

    /** The Constant MOTOR_2. */
    public static final int MOTOR_2 = 2;

    /** The num moteur gauche. */
    private int numMoteurGauche;

    /** The num moteur droit. */
    private int numMoteurDroit;

    /** The alternate. */
    private boolean alternate;

    /** The min val. */
    protected int minVal;

    /** The max val. */
    protected int maxVal;

    /** The prev m1. */
    protected int prevM1;

    /** The prev m2. */
    protected int prevM2;

    /**
     * Instantiates a new abstract motors.
     */
    public AbstractPropulsionsMotors() {
        assignMotors(AbstractPropulsionsMotors.UNDEF_MOTOR, AbstractPropulsionsMotors.UNDEF_MOTOR);
        alternate = false;
    }

    /**
     * Méthode pour assigner le numéro du moteur pour la commande gauche / droite
     * 
     * @param numMoteurGauche
     *            the num moteur gauche
     * @param numMoteurDroit
     *            the num moteur droit
     */
    public final void assignMotors(final int numMoteurGauche, final int numMoteurDroit) {
        this.numMoteurGauche = numMoteurGauche;
        this.numMoteurDroit = numMoteurDroit;
    }

    /**
     * Méthode de génération groupé de la commande droite / gauche
     * 
     * @param gauche
     *            the gauche
     * @param droit
     *            the droit
     */
    public final void generateMouvement(final int gauche, final int droit) {
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
     * @param cmd
     *            the cmd
     * @throws IllegalStateException
     *             the illegal state exception
     */
    public final void moteurGauche(final int cmd) throws IllegalStateException {
        if (numMoteurGauche == AbstractPropulsionsMotors.MOTOR_1) {
            moteur1(cmd);
        } else if (numMoteurGauche == AbstractPropulsionsMotors.MOTOR_2) {
            moteur2(cmd);
        }

        exceptionAssignationMoteurGauche();
    }

    /**
     * Méthode de commande du moteur droit (doit être assigné).
     * 
     * @param cmd
     *            the cmd
     * @throws IllegalStateException
     *             the illegal state exception
     */
    public final void moteurDroit(final int cmd) throws IllegalStateException {
        if (numMoteurDroit == AbstractPropulsionsMotors.MOTOR_1) {
            moteur1(cmd);
        } else if (numMoteurDroit == AbstractPropulsionsMotors.MOTOR_2) {
            moteur2(cmd);
        }

        exceptionAssignationMoteurDroit();
    }

    /**
     * Méthode pour commander l'arret des moteurs configurés
     */
    public final void stopAll() {
        stop1();
        stop2();
    }

    /**
     * Méthode d'arrêt du moteur gauche (doit être assigné).
     * 
     * @throws IllegalStateException
     *             the illegal state exception
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
     * @throws IllegalStateException
     *             the illegal state exception
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
        moteur1(0);
    }

    /**
     * Méthode d'arrêt du moteur 2
     */
    public void stop2() {
        moteur2(0);
    }

    /**
     * Inits the.
     */
    public abstract void init();

    /**
     * Moteur1.
     * 
     * @param val the cmd
     */
    public abstract void moteur1(final int val);

    /**
     * Moteur2.
     * 
     * @param val the cmd
     */
    public abstract void moteur2(final int val);

    /**
     * Prints the version.
     */
    public abstract void printVersion();

    /**
     * Méthode de contrôle du bornage des commandes moteurs.
     * 
     * @param val
     *            the val
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
    private void exceptionAssignationMoteurGauche() {
        if (numMoteurGauche == AbstractPropulsionsMotors.UNDEF_MOTOR) {
            log.error("L'assignation du moteur gauche n'est pas faite");
            throw new IllegalStateException("L'assignation du moteur gauche n'est pas faite");
        }
    }

    /**
     * Exception assignation moteur droit.
     */
    private void exceptionAssignationMoteurDroit() {
        if (numMoteurDroit == AbstractPropulsionsMotors.UNDEF_MOTOR) {
            log.error("L'assignation du moteur droit n'est pas faite");
            throw new IllegalStateException("L'assignation du moteur droit n'est pas faite");
        }
    }
}

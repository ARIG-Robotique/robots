package org.arig.robot.system.motors;

import lombok.extern.slf4j.Slf4j;

/**
 * The Class ArigLMD18200Motors.
 * 
 * @author mythril
 */
@Slf4j
public class ArigLMD18200Motors extends AbstractMotors {

    /** The prev m3. */
    private int prevM3;

    /** The prev m4. */
    private int prevM4;

    /*
     * (non-Javadoc)
     * 
     * @see org.arig.robot.system.motors.AbstractMotors#init()
     */
    @Override
    public void init() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.arig.robot.system.motors.AbstractMotors#moteur1(int)
     */
    @Override
    public void moteur1(final int val) {
        final int cmd = check(val);
        if (cmd == prevM1) {
            return;
        }
        prevM1 = cmd;

        if (log.isDebugEnabled()) {
            log.debug("Commande du moteur 1 : {}", cmd);
        }
        // TODO : Implementation de la commande
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.arig.robot.system.motors.AbstractMotors#moteur2(int)
     */
    @Override
    public void moteur2(final int val) {
        final int cmd = check(val);
        if (cmd == prevM2) {
            return;
        }
        prevM2 = cmd;

        if (log.isDebugEnabled()) {
            log.debug("Commande du moteur 2 : {}", cmd);
        }
        // TODO : Implémentation de la commande
    }

    /**
     * Moteur3.
     * 
     * @param val
     *            the val
     */
    public void moteur3(final int val) {
        final int cmd = check(val);
        if (cmd == prevM3) {
            return;
        }
        prevM3 = cmd;

        if (log.isDebugEnabled()) {
            log.debug("Commande du moteur 3 : {}", cmd);
        }
        // TODO : Implémentation de la commande
    }

    /**
     * Moteur4.
     * 
     * @param val
     *            the val
     */
    public void moteur4(final int val) {
        final int cmd = check(val);
        if (cmd == prevM4) {
            return;
        }
        prevM4 = cmd;

        if (log.isDebugEnabled()) {
            log.debug("Commande du moteur 4 : {}", cmd);
        }
        // TODO : Implémentation de la commande
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.arig.robot.system.motors.AbstractMotors#printVersion()
     */
    @Override
    public void printVersion() {
        // TODO : Injecter la version du projet
        log.info("ARIG carte LMD18200 4 moteurs version 1.0.0");
    }
}

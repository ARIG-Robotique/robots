package org.arig.robot.system.motors;

import lombok.extern.slf4j.Slf4j;

/**
 * The Class ArigLMD18200Motors.
 *
 * @author gdepuille
 */
@Slf4j
public class ArigLMD18200Motors extends AbstractPropulsionsMotors {

    private int prevM3;
    private int prevM4;

    @Override
    public void init() {
    }

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
     * @param val the val
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
     * @param val the val
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

    @Override
    public int getMinSpeed() {
        return 0;
    }

    @Override
    public int getMaxSpeed() {
        return 0;
    }

    @Override
    protected int currentSpeedMoteur1() {
        return 0;
    }

    @Override
    protected int currentSpeedMoteur2() {
        return 0;
    }

    @Override
    public void printVersion() {
        log.info("ARIG carte LMD18200 4 moteurs");
    }
}
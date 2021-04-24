package org.arig.robot.system.avoiding;

public interface IAvoidingService {

    enum Mode {
        BASIC, FULL, BASIC_RETRY, SEMI_COMPLETE
    }

    /**
     * Execution du système d'évittement
     */
    void process();

    void setSafeAvoidance(boolean enabled);

}

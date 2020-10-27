package org.arig.robot.system.avoiding;

public interface IAvoidingService {

    /**
     * Execution du système d'évittement
     */
    void process();

    void setSafeAvoidance(boolean enabled);

}

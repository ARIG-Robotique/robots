package org.arig.robot.strategy;

import lombok.Getter;

/**
 * @author gdepuille on 23/05/17.
 */
public abstract class AbstractAction implements IAction {

    @Getter
    private String UUID = java.util.UUID.randomUUID().toString();

}

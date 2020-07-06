package org.arig.robot.strategy.actions;

import org.arig.robot.strategy.AbstractMacroAction;
import org.arig.robot.utils.TableUtils;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractMacroNerellAction extends AbstractMacroAction {

    @Autowired
    protected TableUtils tableUtils;

}

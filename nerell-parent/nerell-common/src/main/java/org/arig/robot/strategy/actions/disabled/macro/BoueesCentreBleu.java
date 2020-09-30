package org.arig.robot.strategy.actions.disabled.macro;

import org.arig.robot.strategy.actions.AbstractMacroNerellAction;
import org.arig.robot.strategy.actions.active.Bouee6;
import org.arig.robot.strategy.actions.active.Bouee7;
import org.arig.robot.strategy.actions.active.Bouee8;
import org.arig.robot.strategy.actions.disabled.Bouee5;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;

@Component
public class BoueesCentreBleu extends AbstractMacroNerellAction {

    @Autowired
    private Bouee8 bouee8;

    @Autowired
    private Bouee7 bouee7;

    @Autowired
    private Bouee6 bouee6;

    @Autowired
    private Bouee5 bouee5;

    @Override
    public String name() {
        return "Bouees 8 7 6";
    }

    @Override
    public int order() {
        return 4 + tableUtils.alterOrder(bouee8.entryPoint());
    }

    @PostConstruct
    public void init() {
        this.actions = Arrays.asList(bouee8, bouee7, bouee6, bouee5);
    }

}

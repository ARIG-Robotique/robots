package org.arig.robot.strategy.actions.disabled.macro;

import org.arig.robot.strategy.actions.AbstractMacroNerellAction;
import org.arig.robot.strategy.actions.active.Bouee10;
import org.arig.robot.strategy.actions.active.Bouee11;
import org.arig.robot.strategy.actions.active.Bouee9;
import org.arig.robot.strategy.actions.disabled.Bouee12;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;

@Component
public class BoueesCentreJaune extends AbstractMacroNerellAction {

    @Autowired
    private Bouee9 bouee9;

    @Autowired
    private Bouee10 bouee10;

    @Autowired
    private Bouee11 bouee11;

    @Autowired
    private Bouee12 bouee12;

    @Override
    public String name() {
        return "Bouees 9 10 11 12";
    }

    @Override
    public int order() {
        return 4 + tableUtils.alterOrder(bouee9.entryPoint());
    }

    @PostConstruct
    public void init() {
        this.actions = Arrays.asList(bouee9, bouee10, bouee11, bouee12);
    }

}

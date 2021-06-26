package org.arig.robot.strategy.actions.disabled.macro;

import org.arig.robot.strategy.actions.AbstractNerellMacroAction;
import org.arig.robot.strategy.actions.active.NerellBouee6;
import org.arig.robot.strategy.actions.active.NerellBouee7;
import org.arig.robot.strategy.actions.active.NerellBouee8;
import org.arig.robot.strategy.actions.disabled.NerellBouee5;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;

@Component
public class BoueesCentreBleu extends AbstractNerellMacroAction {

    @Autowired
    private NerellBouee8 bouee8;

    @Autowired
    private NerellBouee7 bouee7;

    @Autowired
    private NerellBouee6 bouee6;

    @Autowired
    private NerellBouee5 bouee5;

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

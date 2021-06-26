package org.arig.robot.strategy.actions.disabled.macro;

import org.arig.robot.strategy.actions.AbstractNerellMacroAction;
import org.arig.robot.strategy.actions.active.NerellBouee10;
import org.arig.robot.strategy.actions.active.NerellBouee11;
import org.arig.robot.strategy.actions.active.NerellBouee12;
import org.arig.robot.strategy.actions.active.NerellBouee9;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;

@Component
public class BoueesCentreJaune extends AbstractNerellMacroAction {

    @Autowired
    private NerellBouee9 bouee9;

    @Autowired
    private NerellBouee10 bouee10;

    @Autowired
    private NerellBouee11 bouee11;

    @Autowired
    private NerellBouee12 bouee12;

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

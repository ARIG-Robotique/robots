package org.arig.robot.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.arig.robot.constants.IConstantesConfig;
import org.arig.robot.strategy.IAction;
import org.arig.robot.strategy.StrategyManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

/**
 * @author gdepuille on 22/12/14.
 */
@Slf4j
@Profile(IConstantesConfig.profileMonitoring)
@RestController
@RequestMapping("/strategy")
public class StrategyController {

    @Autowired
    private StrategyManager strategyManager;

    @RequestMapping(method = RequestMethod.GET)
    public List<IAction> listStrategy() {
        return strategyManager.getActions();
    }

    @RequestMapping(method = RequestMethod.GET)
    public void execute(@RequestParam String uid) {
        Optional<IAction> action = strategyManager.getActions().stream()
                .filter(a -> StringUtils.equalsIgnoreCase(a.getUUID(), uid))
                .findFirst();

        if (action.isPresent()) {
            IAction exec = action.get();
            log.info("Execution de l'action : {}", exec.name());
            exec.execute();
        } else {
            log.warn("Aucune action avec l'UID {}", uid);
        }
    }
}

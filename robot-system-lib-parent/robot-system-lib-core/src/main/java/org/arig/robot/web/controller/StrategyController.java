package org.arig.robot.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.arig.robot.constants.IConstantesConfig;
import org.arig.robot.strategy.IAction;
import org.arig.robot.strategy.StrategyManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

/**
 * @author gdepuille on 22/12/14.
 */
@Slf4j
@RestController
@RequestMapping("/strategy")
@Profile(IConstantesConfig.profileMonitoring)
public class StrategyController {

    @Autowired
    private StrategyManager strategyManager;

    @GetMapping
    public List<IAction> listStrategy() {
        return strategyManager.getActions();
    }

    @PostMapping(path = "/execute")
    public void execute(@RequestParam("uid") String uid) {
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

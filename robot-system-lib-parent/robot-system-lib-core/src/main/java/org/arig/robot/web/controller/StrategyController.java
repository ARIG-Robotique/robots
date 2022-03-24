package org.arig.robot.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.arig.robot.constants.ConstantesConfig;
import org.arig.robot.model.ActionSuperviseur;
import org.arig.robot.strategy.Action;
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
import java.util.stream.Collectors;

/**
 * @author gdepuille on 22/12/14.
 */
@Slf4j
@RestController
@RequestMapping("/strategy")
@Profile(ConstantesConfig.profileMonitoring)
public class StrategyController {

    @Autowired
    private StrategyManager strategyManager;

    @GetMapping
    public List<ActionSuperviseur> listStrategy() {
        return strategyManager.actions().stream()
                .map(ActionSuperviseur::fromAction)
                .collect(Collectors.toList());
    }

    @PostMapping(path = "/execute")
    public void execute(@RequestParam("uid") String uid) {
        Optional<Action> action = strategyManager.actions().stream()
                .filter(a -> StringUtils.equalsIgnoreCase(a.uuid(), uid))
                .findFirst();

        if (action.isPresent()) {
            Action exec = action.get();
            log.info("Execution de l'action : {}", exec.name());
            exec.execute();
        } else {
            log.warn("Aucune action avec l'UID {}", uid);
        }
    }
}

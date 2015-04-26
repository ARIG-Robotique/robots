package org.arig.eurobot.web.controller;

import com.google.common.base.Function;
import lombok.extern.slf4j.Slf4j;
import org.arig.eurobot.web.model.CapteurNumerique;
import org.arig.robot.system.capteurs.AbstractBoard2007NoMux;
import org.arig.robot.system.capteurs.RaspiBoard2007NoMux;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mythril on 21/12/13.
 */
@Slf4j
@Profile("raspi")
@RestController
@RequestMapping("/capteurs")
public class CapteurController {

    /*@Autowired
    private RaspiBoard2007NoMux dic;*/

    @RequestMapping(method = RequestMethod.GET)
    public List<CapteurNumerique> listAll() {
        List<CapteurNumerique> capteurList = new ArrayList<>();
//        for (Integer capteurId : dic.getIds()) {
//            AbstractBoard2007NoMux.CapteursDefinition cd = dic.getDefinitionById(capteurId);
//
//            Function<AbstractBoard2007NoMux.CapteursDefinition, CapteurNumerique> f = (input) -> new CapteurNumerique(input.getId(), String.format("%s (%s)", input.name(), input.getDescription()), dic.readCapteurValue(input.getId()));
//
//            capteurList.add(f.apply(cd));
//        }

        return capteurList;
    }
}

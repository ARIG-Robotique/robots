package org.arig.prehistobot.web;

import com.pi4j.io.gpio.Pin;
import lombok.extern.slf4j.Slf4j;
import org.arig.prehistobot.model.Capteur;
import org.arig.robot.system.capteurs.IDigitalInputCapteurs;
import org.arig.robot.system.capteurs.RaspiBoard2007NoMux;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mythril on 21/12/13.
 */
@Profile("gpio")
@RestController
@RequestMapping("/capteurs")
@Slf4j
public class CapteurControllers {

    @Autowired
    private RaspiBoard2007NoMux dic;

    @RequestMapping(method = RequestMethod.GET)
    private List<Capteur> listAll() {
        List<Capteur> capteurList = new ArrayList<>();
        dic.readCapteurValue()
    }
}

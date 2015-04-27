package org.arig.eurobot.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.arig.eurobot.services.IOServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mythril on 21/12/13.
 */
@Slf4j
@Profile("raspi")
@RestController
@RequestMapping("/capteurs")
public class CapteurController {

    @Autowired
    private IOServices ioServices;

    @RequestMapping(method = RequestMethod.GET)
    public Map<String, Boolean> listAll() {
        Map<String, Boolean> capteurList = new HashMap<>();

        capteurList.put("AU", ioServices.auOk());
        capteurList.put("Puissance Moteur", ioServices.alimMoteurOk());
        capteurList.put("Puissance Servo", ioServices.alimServoOk());
        capteurList.put("Tirette", ioServices.tirette());
        capteurList.put("Bouton tapis", ioServices.btnTapis());
        capteurList.put("Butée avant gauche", ioServices.buteeAvantGauche());
        capteurList.put("Butée avant droite", ioServices.buteeAvantDroit());
        capteurList.put("Butée arrière gauche", ioServices.buteeArriereGauche());
        capteurList.put("Butée arrière droite", ioServices.buteeArriereDroit());
        capteurList.put("Gobelet gauche", ioServices.gobeletGauche());
        capteurList.put("Gobelet droit", ioServices.gobeletDroit());
        capteurList.put("Pied gauche", ioServices.piedGauche());
        capteurList.put("Pied droit", ioServices.piedDroit());
        capteurList.put("Pied ascenseur", ioServices.piedCentre());

        return capteurList;
    }

}

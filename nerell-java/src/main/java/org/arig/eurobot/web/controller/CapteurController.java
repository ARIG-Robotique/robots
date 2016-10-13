package org.arig.eurobot.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.arig.eurobot.constants.IConstantesSpringConfig;
import org.arig.eurobot.services.IOService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author gdepuille on 21/12/13.
 */
@Slf4j
@Profile(IConstantesSpringConfig.profileMonitoring)
@RestController
@RequestMapping("/capteurs")
public class CapteurController {

    @Autowired
    private IOService ioService;

    @RequestMapping(method = RequestMethod.GET)
    public Map<String, Object> listAll() {
        Map<String, Object> capteurList = new LinkedHashMap<>();

        capteurList.put("AU", ioService.auOk());
        capteurList.put("Puissance Moteur", ioService.alimMoteurOk());
        capteurList.put("Puissance Servo", ioService.alimServoOk());
        capteurList.put("Tirette", ioService.tirette());
        capteurList.put("Bouton tapis", ioService.btnTapis());
        capteurList.put("Equipe", ioService.equipe());
        capteurList.put("Butée avant gauche", ioService.buteeAvantGauche());
        capteurList.put("Butée avant droite", ioService.buteeAvantDroit());
        capteurList.put("Butée arrière gauche", ioService.buteeArriereGauche());
        capteurList.put("Butée arrière droite", ioService.buteeArriereDroit());
        capteurList.put("Pied ascenseur", ioService.piedCentre());
        capteurList.put("Pied gauche", ioService.piedGauche());
        capteurList.put("Pied droit", ioService.piedDroit());
        capteurList.put("Produit gauche", ioService.produitGauche());
        capteurList.put("Produit droit", ioService.produitDroit());
        capteurList.put("Gobelet gauche", ioService.gobeletGauche());
        capteurList.put("Gobelet droit", ioService.gobeletDroit());

        return capteurList;
    }

}

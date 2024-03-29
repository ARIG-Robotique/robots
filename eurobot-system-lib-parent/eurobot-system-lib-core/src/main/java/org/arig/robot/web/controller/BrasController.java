package org.arig.robot.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.ConstantesConfig;
import org.arig.robot.model.bras.AnglesBras;
import org.arig.robot.model.bras.CurrentBras;
import org.arig.robot.model.bras.PointBras;
import org.arig.robot.model.bras.PositionBras;
import org.arig.robot.services.BrasService;
import org.arig.robot.services.BrasServiceInternal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;


@Slf4j
@RestController
@RequestMapping("/bras")
@Profile(ConstantesConfig.profileMonitoring)
public class BrasController {

    @Autowired
    private BrasService brasService;

    @GetMapping("/config")
    public BrasServiceInternal.AllConfigBras getConfig() {
        return brasService.getConfig();
    }

    @GetMapping
    public Map<String, CurrentBras> getCurrent() {
        return brasService.getCurrent();
    }

    @PostMapping("/{bras}")
    public boolean setBras(@PathVariable("bras") final String bras,
                           @RequestParam("x") final int x,
                           @RequestParam("y") final int y,
                           @RequestParam("a") final int a) {
        assert bras != null;
        switch (bras) {
            case "avantGauche":
                return brasService.setBrasAvantGauche(new PointBras(x, y, a), null, 40);
            case "avantCentre":
                return brasService.setBrasAvantCentre(new PointBras(x, y, a), null, 40);
            case "avantDroit":
                return brasService.setBrasAvantDroit(new PointBras(x, y, a), null, 40);
            case "arriereGauche":
                return brasService.setBrasArriereGauche(new PointBras(x, y, a), null, 40);
            case "arriereCentre":
                return brasService.setBrasArriereCentre(new PointBras(x, y, a), null, 40);
            case "arriereDroit":
                return brasService.setBrasArriereDroit(new PointBras(x, y, a), null, 40);
            default:
                return false;
        }
    }

    @PostMapping("/{bras}/byName")
    public void setBrasByName(@PathVariable("bras") final String bras,
                              @RequestParam("name") final PositionBras position) {
        assert bras != null;
        switch (bras) {
            case "avantGauche":
                brasService.setBrasAvantGauche(position);
                break;
            case "avantCentre":
                brasService.setBrasAvantCentre(position);
                break;
            case "avantDroit":
                brasService.setBrasAvantDroit(position);
                break;
            case "arriereGauche":
                brasService.setBrasArriereGauche(position);
                break;
            case "arriereCentre":
                brasService.setBrasArriereCentre(position);
                break;
            case "arriereDroit":
                brasService.setBrasArriereDroit(position);
                break;
        }
    }

    @GetMapping("/{bras}/compute")
    public AnglesBras calculerAngles(@PathVariable("bras") final String bras,
                                     @RequestParam("x") final int x,
                                     @RequestParam("y") final int y,
                                     @RequestParam("a") final int a) {
        assert bras != null;
        switch (bras) {
            case "avantGauche":
                return brasService.calculerBrasAvantGauche(new PointBras(x, y, a));
            case "avantCentre":
                return brasService.calculerBrasAvantCentre(new PointBras(x, y, a));
            case "avantDroit":
                return brasService.calculerBrasAvantDroit(new PointBras(x, y, a));
            case "arriereGauche":
                return brasService.calculerBrasArriereGauche(new PointBras(x, y, a));
            case "arriereCentre":
                return brasService.calculerBrasArriereCentre(new PointBras(x, y, a));
            case "arriereDroit":
                return brasService.calculerBrasArriereDroit(new PointBras(x, y, a));
            default:
                return null;
        }
    }

}

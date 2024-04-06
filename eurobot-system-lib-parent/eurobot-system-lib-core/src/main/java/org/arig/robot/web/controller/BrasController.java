package org.arig.robot.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.ConstantesConfig;
import org.arig.robot.model.Bras;
import org.arig.robot.model.bras.AnglesBras;
import org.arig.robot.model.bras.CurrentBras;
import org.arig.robot.model.bras.OptionBras;
import org.arig.robot.model.bras.PointBras;
import org.arig.robot.model.bras.PositionBras;
import org.arig.robot.services.BrasService;
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
    public Map<Bras, BrasService.FullConfigBras> getConfig() {
        return brasService.getConfig();
    }

    @GetMapping
    public Map<Bras, CurrentBras> getCurrent() {
        return brasService.getCurrent();
    }

    @PostMapping("/{bras}")
    public boolean setBras(
            @PathVariable("bras") final Bras bras,
            @RequestParam("x") final int x,
            @RequestParam("y") final int y,
            @RequestParam("a") final int a,
            @RequestParam(name = "invertA1", required = false) final Boolean invertA1
    ) {
        assert bras != null;
        return brasService.setBras(bras, new PointBras(x, y, a, invertA1), null, 40);
    }

    @PostMapping("/{bras}/byName")
    public void setBrasByName(
            @PathVariable("bras") final Bras bras,
            @RequestParam("name") final PositionBras position
    ) {
        assert bras != null;
        brasService.setBras(bras, position, OptionBras.SLOW);
    }

    @GetMapping("/{bras}/compute")
    public AnglesBras calculerAngles(
            @PathVariable("bras") final Bras bras,
            @RequestParam("x") final int x,
            @RequestParam("y") final int y,
            @RequestParam("a") final int a,
            @RequestParam(name = "invertA1", required = false) final Boolean invertA1
    ) {
        assert bras != null;
        return brasService.calculerBras(bras, new PointBras(x, y, a, invertA1));
    }

}

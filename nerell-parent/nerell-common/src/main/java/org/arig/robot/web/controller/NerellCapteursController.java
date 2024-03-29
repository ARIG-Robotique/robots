package org.arig.robot.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.CouleurEchantillon;
import org.arig.robot.model.NerellRobotStatus;
import org.arig.robot.services.NerellIOService;
import org.arig.robot.system.capteurs.TCS34725ColorSensor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class NerellCapteursController extends AbstractCapteursController {

    @Autowired
    private NerellRobotStatus robotStatus;

    @Autowired
    private NerellIOService ioService;

    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();

        numeriqueInfos.put("Calage arrière droit", ioService::calageArriereDroit);
        numeriqueInfos.put("Calage arrière gauche", ioService::calageArriereGauche);
        numeriqueInfos.put("Calage avant bas droit", ioService::calageAvantBasDroit);
        numeriqueInfos.put("Calage avant bas gauche", ioService::calageAvantBasGauche);
        numeriqueInfos.put("Calage avant haut droit", ioService::calageAvantHautDroit);
        numeriqueInfos.put("Calage avant haut gauche", ioService::calageAvantHautGauche);
        numeriqueInfos.put("Calage lateral droit", ioService::calageLatteralDroit);
        numeriqueInfos.put("Calage echantillon", ioService::calagePriseEchantillon);

        numeriqueInfos.put("Presence carre de fouille", () -> ioService.presenceCarreFouille(false));
        numeriqueInfos.put("Presence prise bras", () -> ioService.presencePriseBras(false));
        numeriqueInfos.put("Presence stock 1", () -> ioService.presenceStock1(false));
        numeriqueInfos.put("Presence stock 2", () -> ioService.presenceStock2(false));
        numeriqueInfos.put("Presence stock 3", () -> ioService.presenceStock3(false));
        numeriqueInfos.put("Presence stock 4", () -> ioService.presenceStock4(false));
        numeriqueInfos.put("Presence stock 5", () -> ioService.presenceStock5(false));
        numeriqueInfos.put("Presence stock 6", () -> ioService.presenceStock6(false));
        numeriqueInfos.put("Presence ventouse bas", ioService::presenceVentouseBas);
        numeriqueInfos.put("Presence ventouse haut", ioService::presenceVentouseHaut);
        numeriqueInfos.put("Presence statuette", () -> ioService.presenceStatuette(false));

        couleursInfos.put("Ventouse haut", () -> {
            TCS34725ColorSensor.ColorData colorData = ioService.couleurVentouseHautRaw();
            CouleurEchantillon couleurEchantillon = ioService.computeCouleur(colorData);
            return String.format("%s (%d %d %d)", couleurEchantillon.name(), colorData.r(), colorData.g(), colorData.b());
        });
        couleursInfos.put("Ventouse bas", () -> {
            TCS34725ColorSensor.ColorData colorData = ioService.couleurVentouseBasRaw();
            CouleurEchantillon couleurEchantillon = ioService.computeCouleur(colorData);
            return String.format("%s (%d %d %d)", couleurEchantillon.name(), colorData.r(), colorData.g(), colorData.b());
        });

        textInfos.put("Equipe", () -> (robotStatus.team() != null) ? robotStatus.team().name() : "???");
    }
}

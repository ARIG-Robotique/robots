package org.arig.robot.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.CouleurEchantillon;
import org.arig.robot.model.OdinRobotStatus;
import org.arig.robot.services.OdinIOService;
import org.arig.robot.system.capteurs.TCS34725ColorSensor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class OdinCapteursController extends AbstractCapteursController {

    @Autowired
    private OdinRobotStatus robotStatus;

    @Autowired
    private OdinIOService ioService;

    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();

        numeriqueInfos.put("Calage arrière droite", ioService::calageArriereDroit);
        numeriqueInfos.put("Calage arrière gauche", ioService::calageArriereGauche);
        numeriqueInfos.put("Calage avant droite", ioService::calageAvantDroit);
        numeriqueInfos.put("Calage avant gauche", ioService::calageAvantGauche);
        numeriqueInfos.put("Calage lateral droit", ioService::calageLatteralDroit);

        numeriqueInfos.put("Presence carre de fouille", () -> ioService.presenceCarreFouille(false));
        numeriqueInfos.put("Presence prise bras", ioService::presencePriseBras);
        numeriqueInfos.put("Presence stock 1", ioService::presenceStock1);
        numeriqueInfos.put("Presence stock 2", ioService::presenceStock2);
        numeriqueInfos.put("Presence stock 3", ioService::presenceStock3);
        numeriqueInfos.put("Presence stock 4", ioService::presenceStock4);
        numeriqueInfos.put("Presence stock 5", ioService::presenceStock5);
        numeriqueInfos.put("Presence stock 6", ioService::presenceStock6);
        numeriqueInfos.put("Presence ventouse bas", ioService::presenceVentouseBas);
        numeriqueInfos.put("Presence ventouse haut", ioService::presenceVentouseHaut);

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

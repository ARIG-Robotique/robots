package org.arig.robot.web.controller;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.RobotStatus;
import org.arig.robot.services.IIOService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestController
public class CapteursController extends AbstractCapteursController implements InitializingBean {

    @Autowired
    private RobotStatus rs;

    @Autowired
    private IIOService ioService;

    @Getter
    @Accessors(fluent = true)
    private final Map<String, BooleanValue> numeriqueInfos = new LinkedHashMap<>();

    @Getter
    @Accessors(fluent = true)
    private final Map<String, DoubleValue> analogiqueInfos = new LinkedHashMap<>();

    @Getter
    @Accessors(fluent = true)
    private final Map<String, StringValue> textInfos = new LinkedHashMap<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        // Capteurs informations numérique
        numeriqueInfos.put("AU", ioService::auOk);
        numeriqueInfos.put("Puissance 5V", ioService::alimPuissance5VOk);
        numeriqueInfos.put("Puissance 12V", ioService::alimPuissance12VOk);
        numeriqueInfos.put("Tirette", ioService::tirette);
        numeriqueInfos.put("Presence lecture couleur", ioService::presenceLectureCouleur);
        numeriqueInfos.put("Bordure arrière droite", ioService::calageBordureArriereDroit);
        numeriqueInfos.put("Bordure arrière gauche", ioService::calageBordureArriereGauche);
        numeriqueInfos.put("Presence ventouse avant", ioService::presenceVentouseAvant);
        numeriqueInfos.put("Gobelet prit dans ventouse avant", ioService::gobeletPritDansVentouseAvant);

        // Capteurs informations analogique

        // Capteurs informations Text
        textInfos.put("Equipe", () -> rs.getTeam().name());
//        textInfos.put("Couleur palet (hex)", () -> ioService.couleurPaletRaw().hexColor());
//        textInfos.put("Couleur palet", () -> ioService.couleurPalet().name());
    }
}

package org.arig.robot;

import lombok.SneakyThrows;
import org.arig.robot.AbstractOrdonanceur.OrdonanceurStep;
import org.arig.robot.config.spring.NerellSimulator;
import org.arig.robot.config.spring.NerellSimulatorTestContext;
import org.arig.robot.constants.IConstantesConfig;
import org.arig.robot.model.ETeam;
import org.arig.robot.model.NerellRobotStatus;
import org.arig.robot.services.NerellIOServiceBouchon;
import org.arig.robot.system.capteurs.IEcran;
import org.arig.robot.system.capteurs.NerellTestEcran;
import org.arig.robot.utils.ThreadUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {NerellSimulator.class, NerellSimulatorTestContext.class})
public class NerellSimulatorTest {

    @Autowired
    private NerellRobotStatus rs;

    @Autowired
    private NerellOrdonanceur ordonanceur;

    @Autowired
    private NerellIOServiceBouchon ioService;

    @Autowired
    private IEcran ecran;

    @BeforeClass
    public static void before() {
        // Définition d'un ID unique pour le nommage des fichiers
        final String execId = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        System.setProperty(IConstantesConfig.keyExecutionId, execId);
        System.setProperty(IConstantesConfig.disableEcran, "true");

        // Surcharge de bean
        System.setProperty("spring.main.allow-bean-definition-overriding", "true");
    }

    @SneakyThrows
    @Test(timeout = 300000)
    public void runSimulateurTest() {
        rs.simulateur(true);

        ExecutorService exec = Executors.newSingleThreadExecutor();
        exec.submit(() -> ordonanceur.run());

        waitStep(OrdonanceurStep.WAIT_AU);
        ioService.au(true);

        // Selection de la couleur de l'équipe
        waitStep(OrdonanceurStep.AFTER_INIT);
        while(rs.team() != ETeam.JAUNE) {
            ((NerellTestEcran) ecran).getConfigInfos().setTeam(1); // 1 Jaune, 2 Bleu
            ThreadUtils.sleep(2000);
        }
        ((NerellTestEcran) ecran).getConfigInfos().setStartCalibration(true);

        // Mise tirette
        waitStep(OrdonanceurStep.READY_TO_PLAY);
        ioService.tirette(true);

        // Start match
        waitStep(OrdonanceurStep.WAIT_START_MATCH);
        ioService.tirette(false);

        // Remise tirette fin match
        waitStep(OrdonanceurStep.EJECTION);
        ioService.tirette(true);

        waitStep(OrdonanceurStep.READY_TO_STORE);
        ioService.tirette(false);

        // Check all are terminated
        waitStep(OrdonanceurStep.END);
    }

    private void waitStep(OrdonanceurStep step) {
        while(ordonanceur.getStep() != step) {
            ThreadUtils.sleep(2000);
        }
    };
}

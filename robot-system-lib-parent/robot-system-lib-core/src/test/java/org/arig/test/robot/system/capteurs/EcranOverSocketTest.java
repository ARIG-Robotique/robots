package org.arig.test.robot.system.capteurs;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.ecran.GetConfigInfos;
import org.arig.robot.model.ecran.UpdateMatchInfos;
import org.arig.robot.model.ecran.UpdateStateInfos;
import org.arig.robot.system.capteurs.EcranOverSocket;
import org.arig.robot.utils.ThreadUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import java.net.Socket;

@Slf4j
@RunWith(BlockJUnit4ClassRunner.class)
public class EcranOverSocketTest {

    private static EcranOverSocket ecran;

    @BeforeClass
    @SneakyThrows
    public static void initTest() {
        String host = "nerell";
        Assume.assumeTrue("Contrôle par la présence de l'ecran", serverListening(host, 9000));
        ecran = new EcranOverSocket(host, 9000);
        Assert.assertTrue(ecran.isOpen());
    }

    @AfterClass
    public static void stopTest() {
        if (ecran != null) {
            ecran.end();
        }
    }

    @Test
    @SneakyThrows
    public void testCommEcran() {
        final UpdateStateInfos state = new UpdateStateInfos();
        final UpdateMatchInfos match = new UpdateMatchInfos();

        state.setMessage("AU a débloquer");
        ecran.updateState(state);
        ThreadUtils.sleep(2000);

        state.setMessage("Attente alimentation");
        state.setAu(true);
        ecran.updateState(state);
        ThreadUtils.sleep(500);

        state.setAlim5vp(true);
        ecran.updateState(state);
        ThreadUtils.sleep(500);

        state.setAlim12v(true);
        ecran.updateState(state);
        ThreadUtils.sleep(500);

        state.setMessage("Choix couleur, strategy et start calibration");
        ecran.updateState(state);

        GetConfigInfos infos;
        do {
            infos = ecran.configInfos();
            log.info("Team {} ; Strategy {} ; Calibration {}", infos.getTeam(), infos.getStrategy(), infos.isStartCalibration());
            ThreadUtils.sleep(500);
        } while(!infos.isStartCalibration());

        state.setMessage("Calibration en cours");
        ecran.updateState(state);
        ThreadUtils.sleep(5000);

        state.setMessage("Attente présence tirette");
        ecran.updateState(state);
        ThreadUtils.sleep(2000);

        state.setMessage("Attente départ Match");
        state.setTirette(true);
        ecran.updateState(state);
        ThreadUtils.sleep(2000);

        state.setMessage("");
        state.setTirette(false);
        ecran.updateState(state);
        ecran.updateMatch(match);

        for (int i = 1 ; i <= 10 ; i++) {
            match.setScore(i * 10);
            ecran.updateMatch(match);
            ThreadUtils.sleep(1000);
        }
    }

    public static boolean serverListening(String host, int port) {
        try (Socket s = new Socket(host, port)) {
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

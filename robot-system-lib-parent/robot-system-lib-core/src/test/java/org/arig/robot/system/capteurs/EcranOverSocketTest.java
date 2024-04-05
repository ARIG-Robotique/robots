package org.arig.robot.system.capteurs;

import com.google.common.collect.ImmutableMap;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.SneakyThrows;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.communication.socket.AbstractResponseWithData;
import org.arig.robot.communication.socket.ecran.enums.EcranAction;
import org.arig.robot.model.ecran.AbstractEcranConfig;
import org.arig.robot.model.ecran.AbstractEcranState;
import org.arig.robot.model.ecran.EcranMatchInfo;
import org.arig.robot.model.ecran.EcranParams;
import org.arig.robot.system.capteurs.socket.AbstractEcranOverSocket;
import org.arig.robot.utils.SocketUtils;
import org.arig.robot.utils.ThreadUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@ExtendWith(SpringExtension.class)
class EcranOverSocketTest {

    enum TestTeam {
        ROUGE, VERT
    }

    enum TestStrat {
        STRAT_A, STRAT_B
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    @ToString(callSuper = true)
    static class TestEcranConfig extends AbstractEcranConfig {
        private TestTeam team;
        private TestStrat strategy;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    @ToString(callSuper = true)
    static class TestEcranState extends AbstractEcranState {
        private TestTeam team;
        private TestStrat strategy;
    }

    static class TestConfigResponse extends AbstractResponseWithData<EcranAction, TestEcranConfig> {
    }

    static class TestEcranOverSocket extends AbstractEcranOverSocket<TestEcranConfig, TestEcranState> {
        public TestEcranOverSocket(String hostname, Integer port) {
            super(hostname, port, TestConfigResponse.class);
        }
    }

    private static TestEcranOverSocket ecran;

    @BeforeAll
    @SneakyThrows
    static void initTest() {
        String host = "odin";
        int port = 8686;
        Assumptions.assumeTrue(SocketUtils.serverListening(host, port));

        ecran = new TestEcranOverSocket(host, port);
        ecran.openSocket();
        Assertions.assertTrue(ecran.isOpen());
    }

    @AfterAll
    static void stopTest() {
        if (ecran != null) {
            ecran.end();
        }
    }

    @Test
    @SneakyThrows
    void testCommEcran() {
        final EcranParams params = new EcranParams();
        final TestEcranState state = new TestEcranState();
        final EcranMatchInfo match = new EcranMatchInfo();

        params.setName("Test");
        params.setPrimary(true);
        params.setTeams(ImmutableMap.of(TestTeam.ROUGE.name(), "red", TestTeam.VERT.name(), "green"));
        params.setStrategies(Stream.of(TestStrat.values()).map(Enum::name).collect(Collectors.toList()));
        params.setOptions(Arrays.asList("option_1", "option_2"));
        ecran.setParams(params);
        ThreadUtils.sleep(500);

        state.setMessage("AU a débloquer");
        state.setI2c(true);
        state.setLidar(true);
        ecran.updateState(state);
        ThreadUtils.sleep(2000);

        state.setMessage("Attente alimentation");
        state.setAu(true);
        ecran.updateState(state);
        ThreadUtils.sleep(500);

        state.setAlimServos(true);
        state.setAlimMoteurs(true);
        ecran.updateState(state);
        ThreadUtils.sleep(500);

        state.setMessage("Choix couleur, strategy et start calibration");
        ecran.updateState(state);

        AbstractEcranConfig infos;
        do {
            infos = ecran.configInfos();
            log.info("Team {} ; Strategy {} ; Calibration {}", infos.getTeam(), infos.getStrategy(), infos.isStartCalibration());
            ThreadUtils.sleep(500);
        } while (!infos.isStartCalibration());

        state.setMessage("Calibration en cours");
        ecran.updateState(state);
        ThreadUtils.sleep(2000);

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

        for (int i = 1; i <= 10; i++) {
            match.setScore(i * 10);
            match.setMessage("Action " + i);
            ecran.updateMatch(match);
            ThreadUtils.sleep(1000);
        }

        state.setMessage("Attente remise tirette");
        ecran.updateState(state);
        ThreadUtils.sleep(2000);
    }
}

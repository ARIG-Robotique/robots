package org.arig.test.robot.system.motion;

import lombok.SneakyThrows;
import org.arig.robot.constants.IConstantesConfig;
import org.arig.robot.model.CommandeRobot;
import org.arig.robot.monitoring.IMonitoringWrapper;
import org.arig.robot.system.motion.IAsservissementPolaire;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.UUID;

/**
 * @author gdepuille on 19/03/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { AsservissementPolaireTestContext.class })
public class AsservissementPolaireTest {

    @Autowired
    private IAsservissementPolaire asserv;

    @Autowired
    private CommandeRobot cmdRobot;

    @Autowired
    private IMonitoringWrapper monitoringWrapper;

    @Before
    public void before() {
        System.setProperty(IConstantesConfig.keyExecutionId, UUID.randomUUID().toString());
        monitoringWrapper.cleanAllPoints();
    }

    @After
    public void after() {
        monitoringWrapper.save();
    }

    @Test
    @SneakyThrows
    public void testAsserv() {
        cmdRobot.getConsigne().setDistance(1000);

        for (int i = 2000 ; i >= 0 ; i--) {
            asserv.process(1);
            Thread.sleep(1);
        }
    }
}

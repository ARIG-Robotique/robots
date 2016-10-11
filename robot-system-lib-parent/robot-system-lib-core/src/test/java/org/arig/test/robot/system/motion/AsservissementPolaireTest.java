package org.arig.test.robot.system.motion;

import lombok.SneakyThrows;
import org.arig.robot.system.motion.IAsservissementPolaire;
import org.arig.robot.vo.CommandeRobot;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by gdepuille on 19/03/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { AsservissementPolaireTestContext.class })
public class AsservissementPolaireTest {

    @Autowired
    private IAsservissementPolaire asserv;

    @Autowired
    private CommandeRobot cmdRobot;

    @Test
    @SneakyThrows
    public void testAsserv() {
        cmdRobot.getConsigne().setDistance(1000);

        for (int i = 2000 ; i >= 0 ; i--) {
            asserv.process();
            Thread.currentThread().sleep(10);
        }
    }
}

package org.arig.test.robot.strategy;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.strategy.StrategyManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author gdepuille on 06/05/15.
 */
@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {StrategyManagerTestContext.class})
public class StrategyManagerTest {

    @Autowired
    private StrategyManager sm;

    @Test
    public void testStrategyManagerList() {
        while(sm.actionsCount() != 0) {
            sm.execute();
        }

        sm.execute();
    }
}

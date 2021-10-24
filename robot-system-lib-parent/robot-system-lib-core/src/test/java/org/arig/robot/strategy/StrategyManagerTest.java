package org.arig.robot.strategy;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * @author gdepuille on 06/05/15.
 */
@Slf4j
@ExtendWith(SpringExtension.class)
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

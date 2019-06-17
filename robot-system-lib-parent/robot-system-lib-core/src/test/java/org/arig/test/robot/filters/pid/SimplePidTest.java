package org.arig.test.robot.filters.pid;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.filters.pid.IPidFilter;
import org.arig.robot.filters.pid.SimplePidFilter;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author gdepuille on 15/03/15.
 */
@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { PidTestContext.class })
public class SimplePidTest extends AbstractPidTest {

    @Autowired
    private SimplePidFilter pid;

    @Override
    IPidFilter pid() {
        return pid;
    }

}

package org.arig.robot.filters.pid;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * @author gdepuille on 15/03/15.
 */
@Slf4j
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {PidTestContext.class})
public class SimplePidTest extends AbstractPidTest {

  @Autowired
  private SimplePidFilter pid;

  @Override
  protected PidFilter pid() {
    return pid;
  }
}

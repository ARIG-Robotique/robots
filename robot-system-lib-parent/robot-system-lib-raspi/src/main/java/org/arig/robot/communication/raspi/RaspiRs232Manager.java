package org.arig.robot.communication.raspi;

import com.pi4j.io.serial.Serial;
import org.arig.robot.exception.NotYetImplementedException;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author gdepuille on 20/12/13.
 */
public class RaspiRs232Manager {

    @Autowired
    private Serial port;

    public RaspiRs232Manager() {
        throw new NotYetImplementedException();
    }
}

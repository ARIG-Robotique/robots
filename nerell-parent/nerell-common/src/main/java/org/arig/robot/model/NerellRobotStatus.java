package org.arig.robot.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.system.capteurs.CarreFouilleReader;

@Slf4j
@Data
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper = true)
public class NerellRobotStatus extends EurobotStatus {

    public NerellRobotStatus(CarreFouilleReader carreFouilleReader) {
        super(true, carreFouilleReader);
    }

    private boolean etalonageBaliseOk = false;

    @Setter(AccessLevel.NONE)
    private boolean baliseEnabled = false;

    public void enableBalise() {
        log.info("[RS] activation de la balise");
        baliseEnabled = true;
    }

    public void disableBalise() {
        log.info("[RS] désactivation de la balise");
        baliseEnabled = false;
    }
}

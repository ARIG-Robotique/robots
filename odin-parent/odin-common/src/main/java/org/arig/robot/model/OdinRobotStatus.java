package org.arig.robot.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Objects;

@Slf4j
@Data
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper = true)
public class OdinRobotStatus extends EurobotStatus {

    private EOdinStrategy strategy = EOdinStrategy.BASIC_NORD;

    @Override
    public void stopMatch() {
        super.stopMatch();
    }

    public void setStrategy(int value) {
        switch (value) {
            case 0:
                strategy = EOdinStrategy.BASIC_NORD;
                break;
            case 1:
                strategy = EOdinStrategy.BASIC_SUD;
                break;
            case 2:
                strategy = EOdinStrategy.AGGRESSIVE;
                break;
            case 3:
                strategy = EOdinStrategy.FINALE;
                break;
            default:
                throw new IllegalArgumentException("Strategy invalide");
        }
    }

    private boolean doubleDepose;

    private boolean deposePartielle;

}

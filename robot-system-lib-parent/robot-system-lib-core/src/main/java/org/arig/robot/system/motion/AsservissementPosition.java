package org.arig.robot.system.motion;

import lombok.AllArgsConstructor;
import org.arig.robot.filters.pid.PidFilter;
import org.arig.robot.filters.ramp.TrapezoidalRampFilter;
import org.arig.robot.model.CommandeAsservissementPosition;
import org.arig.robot.system.encoders.AbstractEncoder;

@AllArgsConstructor
public class AsservissementPosition implements IAsservissement {

    private final CommandeAsservissementPosition cmd;

    private AbstractEncoder encoder;

    private final PidFilter pid;

    private final TrapezoidalRampFilter ramp;

    @Override
    public void reset(final boolean resetFilters) {
        pid.reset();

        if (resetFilters) {
            ramp.reset();
        }
    }

    @Override
    public void process(final long timeStepMs, boolean obstacleDetected) {
        // Rampe accel / decel
        ramp.setConsigneVitesse(cmd.getVitesse().getValue());
        ramp.setFrein(cmd.isFrein());
        final double position = ramp.filter(cmd.getConsigne().getValue());

        // Correction PID
        pid.consigne(position);
        final double distance = pid.filter(encoder.getValue());

        // Comande moteur
        cmd.getMoteur().setValue((int) distance);
    }
}

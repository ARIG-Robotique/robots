package org.arig.robot.model.monitor;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.arig.robot.model.Point;
import org.arig.robot.model.enums.TypeMouvement;

import java.util.Collection;

/**
 * @author gdepuille on 15/11/16.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MonitorMouvementPath extends AbstractMonitorMouvement {

    private final TypeMouvement type = TypeMouvement.PATH;
    private Collection<Point> path;
}

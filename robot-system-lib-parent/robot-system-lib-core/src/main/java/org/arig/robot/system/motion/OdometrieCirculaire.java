package org.arig.robot.system.motion;

import org.arig.robot.exception.NotYetImplementedException;
import org.arig.robot.vo.enums.TypeOdometrie;

/**
 * The Class Odometrie.
 *
 * @author gdepuille
 */
public class OdometrieCirculaire extends AbstractOdometrie {

    /**
     * Instantiates a new circular odometrie.
     */
    public OdometrieCirculaire() {
        super(TypeOdometrie.CIRCULAIRE);
    }

    /**
     * Calcul selon le postulat que le robot roule en courbe
     */
    @Override
    protected void process() {
        throw new NotYetImplementedException();
    }
}

package org.arig.robot.system.bras;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.Point3D;
import org.arig.robot.system.servos.SD21Servos;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author mythril on 04/01/14.
 */
@Slf4j
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class Bras2SegmentImpl extends AbstractBras {

    @Autowired
    private SD21Servos servos;

    private final double r1;

    private final double r2;

    private byte servoAngle1;

    private byte servoAngle2;

    public Bras2SegmentImpl(Point3D P1, double r1, double r2) {
        super(P1);
        this.r1 = r1;
        this.r2 = r2;
    }

    @Override
    public void toP(Point3D P) {

        // ----------- //
        // Calcul de r //
        // ----------- //
        double dX = P.getX() - getP1().getX();
        double dZ = P.getZ() - getP1().getZ();
        double r = Math.sqrt(Math.pow(dX, 2) + Math.pow(dZ, 2));

        // ------------------------------------------ //
        // Calcul de alpha1 (angle du servo moteur 1) //
        // ------------------------------------------ //
        double alpha3 = Math.atan(dZ / dX);
        double alpha4 = alKashiAngleRad(r1, r, r2);
        double alpha1 = alpha4 - alpha3;

        // ------------------------------------------ //
        // Calcul de alpha2 (angle du servo moteur 2) //
        // ------------------------------------------ //
        double alpha6 = alKashiAngleRad(r2, r1, r);
        double alpha2 = Math.PI - alpha6;

        // Log.
        double angleServo1Deg = Math.toDegrees(alpha1);
        double angleServo2Deg = Math.toDegrees(alpha2);

        log.info("Calcul des angles pour positionner le bras au point {} => servo 1 : {}° , servo 2 :{}°", P.toString(), angleServo1Deg, angleServo2Deg);

        // TODO : Generation du mouvement
    }

    public void configServo1(byte numero, byte speed) {
        this.servoAngle1 = numero;
        servos.setSpeed(numero, speed);
    }

    public void configServo2(byte numero, byte speed) {
        this.servoAngle2 = numero;
        servos.setSpeed(numero, speed);
    }
}

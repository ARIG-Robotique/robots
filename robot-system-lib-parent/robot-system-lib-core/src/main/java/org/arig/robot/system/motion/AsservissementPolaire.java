package org.arig.robot.system.motion;

import org.arig.robot.filters.pid.IPidFilter;
import org.arig.robot.filters.ramp.IRampFilter;
import org.arig.robot.system.encoders.Abstract2WheelsEncoders;
import org.arig.robot.vo.CommandeRobot;
import org.arig.robot.vo.enums.TypeConsigne;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * The Class AsservissementPolaire.
 * 
 * @author mythril
 */
public class AsservissementPolaire implements IAsservissementPolaire {

    /** The commande robot. */
    @Autowired
    private CommandeRobot cmdRobot;

    /** The encoders. */
    @Autowired
    private Abstract2WheelsEncoders encoders;

    /** The pid distance. */
    @Autowired
    @Qualifier("pidDistance")
    private IPidFilter pidDistance;

    /** The pid orientation. */
    @Autowired
    @Qualifier("pidOrientation")
    private IPidFilter pidOrientation;

    /** The filter distance. */
    @Autowired
    @Qualifier("rampDistance")
    private IRampFilter rampDistance;

    /** The filter orientation. */
    @Autowired
    @Qualifier("rampOrientation")
    private IRampFilter rampOrientation;

    /** The set point distance. */
    private double setPointDistance;

    /** The set point orientation. */
    private double setPointOrientation;

    /** The output distance. */
    private double outputDistance;

    /** The output orientation. */
    private double outputOrientation;

    /**
     * Instantiates a new asservissement polaire.
     */
    public AsservissementPolaire() {
        super();
    }

    @Override
    public void reset() {
        reset(false);
    }

    @Override
    public void reset(final boolean resetFilters) {
        pidDistance.reset();
        pidOrientation.reset();

        if (resetFilters) {
            rampDistance.reset();
            rampOrientation.reset();
        }
    }

    @Override
    public void process() {
        // Application du filtre pour la génération du profil trapézoidale et définition des consignes
        // de distance pour le mode DIST ou XY
        if (cmdRobot.isType(TypeConsigne.DIST) || cmdRobot.isType(TypeConsigne.XY)) {
            setPointDistance = rampDistance.filter(cmdRobot.getVitesse().getDistance(), cmdRobot.getConsigne().getDistance(), encoders.getDistance(), cmdRobot.isFrein());
            outputDistance = pidDistance.compute(setPointDistance, encoders.getDistance());
        } else {
            outputDistance = 0;
        }
        // Toujours le frein pour l'orientation
        if (cmdRobot.isType(TypeConsigne.ANGLE) || cmdRobot.isType(TypeConsigne.XY)) {
            setPointOrientation = rampOrientation.filter(cmdRobot.getVitesse().getOrientation(), cmdRobot.getConsigne().getOrientation(), encoders.getOrientation(), true);
            outputOrientation = pidOrientation.compute(setPointOrientation, encoders.getOrientation());
        } else {
            outputOrientation = 0;
        }

        // Consigne moteurs
        cmdRobot.getMoteur().setDroit((int) (outputDistance + outputOrientation));
        cmdRobot.getMoteur().setGauche((int) (outputDistance - outputOrientation));

//        if (csvCollector != null) {
//            CsvData c = csvCollector.getCurrent();
//            c.setSetPointDistance(pidDistance.getSetPoint());
//            c.setInputDistance(pidDistance.getInput());
//            c.setErreurDistance(pidDistance.getError());
//            c.setSumErreurDistance(pidDistance.getErrorSum());
//            c.setOutputPidDistance(pidDistance.getOutput());
//
//            c.setSetPointOrient(pidOrientation.getSetPoint());
//            c.setInputOrient(pidOrientation.getInput());
//            c.setErreurOrient(pidOrientation.getError());
//            c.setSumErreurOrient(pidOrientation.getErrorSum());
//            c.setOutputPidOrient(pidOrientation.getOutput());
//
//            c.setCmdMoteurGauche(cmdRobot.getMoteur().getGauche());
//            c.setCmdMoteurDroit(cmdRobot.getMoteur().getDroit());
//        }
    }
}

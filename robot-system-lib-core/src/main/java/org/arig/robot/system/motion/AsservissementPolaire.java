package org.arig.robot.system.motion;

import lombok.Setter;
import org.arig.robot.csv.CsvCollector;
import org.arig.robot.csv.CsvData;
import org.arig.robot.filters.pid.IPidFilter;
import org.arig.robot.filters.ramp.IRampFilter;
import org.arig.robot.system.encoders.Abstract2WheelsEncoders;
import org.arig.robot.vo.CommandeRobot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * The Class AsservissementPolaire.
 * 
 * @author mythril
 */
public class AsservissementPolaire implements IAsservissementPolaire {

    @Autowired(required = false)
    private CsvCollector csvCollector;

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

    /** The min fenetre distance. */
    @Setter
    private double minFenetreDistance;

    /** The min fenetre orientation. */
    @Setter
    private double minFenetreOrientation;

    /**
     * Instantiates a new asservissement polaire.
     */
    public AsservissementPolaire() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.arig.robot.system.motion.IAsservissement#reset()
     */
    @Override
    public void reset() {
        reset(false);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.arig.robot.system.motion.IAsservissement#reset(boolean)
     */
    @Override
    public void reset(final boolean resetFilters) {
        pidDistance.reset();
        pidOrientation.reset();

        if (resetFilters) {
            rampDistance.reset();
            rampOrientation.reset();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.arig.robot.system.motion.IAsservissement#process()
     */
    @Override
    public void process() {
        // Application du filtre pour la génération du profil trapézoidale et définition des consignes
        setPointDistance = rampDistance.filter(cmdRobot.getVitesse().getDistance(), cmdRobot.getConsigne().getDistance(), encoders.getDistance(), cmdRobot.isFrein());
        // Toujours le frein pour l'orientation
        setPointOrientation = rampOrientation.filter(cmdRobot.getVitesse().getOrientation(), cmdRobot.getConsigne().getOrientation(), encoders.getOrientation(), true);

        // Calcul des filtres PID
        outputDistance = pidDistance.compute(setPointDistance, encoders.getDistance());
        outputOrientation = pidOrientation.compute(setPointOrientation, encoders.getOrientation());

        // Consigne moteurs
        cmdRobot.getMoteur().setDroit((int) (outputDistance + outputOrientation));
        cmdRobot.getMoteur().setGauche((int) (outputDistance - outputOrientation));

        if (csvCollector != null) {
            CsvData c = csvCollector.getCurrent();
            c.setSetPointDistance(pidDistance.getSetPoint());
            c.setInputDistance(pidDistance.getInput());
            c.setErreurDistance(pidDistance.getError());
            c.setSumErreurDistance(pidDistance.getErrorSum());
            c.setOutputPidDistance(pidDistance.getOutput());

            c.setSetPointOrient(pidOrientation.getSetPoint());
            c.setInputOrient(pidOrientation.getInput());
            c.setErreurOrient(pidOrientation.getError());
            c.setSumErreurOrient(pidOrientation.getErrorSum());
            c.setOutputPidOrient(pidOrientation.getOutput());

            c.setCmdMoteurGauche(cmdRobot.getMoteur().getGauche());
            c.setCmdMoteurDroit(cmdRobot.getMoteur().getDroit());
        }
    }

    // TODO : Déplacer ça dans le mouvement manager car pas utile de le faire ici.

    /**
     * Méthode permettant de récuperer la zone pour la fenetre en distance.
     * 
     * @return the fenetre approche distance
     */
    public double getFenetreApprocheDistance() {
        // Application du théorème de Shannon
        // En gros l'idée est que la fenêtre varie en fonction de la vitesse afin qu'a pleine bourre on la dépasse pas
        // et que l'on se mette a faire des tours sur soit même
        //return Math.max(minFenetreDistance, 3 * setPointDistance);

        return minFenetreDistance;
    }

    /**
     * Méthode permettant de récuperer la zone pour la fenetre en distance.
     * 
     * @return the fenetre approche orientation
     */
    public double getFenetreApprocheOrientation() {
        // Application du théorème de Shannon
        // En gros l'idée est que la fenêtre varie en fonction de la vitesse afin qu'a pleine bourre on la dépasse pas
        // et que l'on se mette a faire des tours sur soit même
        //return Math.max(minFenetreOrientation, 3 * setPointOrientation);

        return minFenetreOrientation;
    }
}

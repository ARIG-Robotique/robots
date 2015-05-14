package org.arig.eurobot.services.avoiding;

import lombok.extern.slf4j.Slf4j;
import org.arig.eurobot.constants.IConstantesRobot;
import org.arig.eurobot.constants.IConstantesServos;
import org.arig.robot.exception.I2CException;
import org.arig.robot.filters.values.MovingIntegerValueAverage;
import org.arig.robot.system.avoiding.IAvoidingService;
import org.arig.robot.system.capteurs.I2CAdcAnalogInput;
import org.arig.robot.system.servos.SD21Servos;
import org.arig.robot.utils.ConvertionRobotUnit;
import org.arig.robot.vo.Point;
import org.arig.robot.vo.Position;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.*;

/**
 * Created by gdepuille on 13/05/15.
 */
@Slf4j
public abstract class AbstractAvoidingService implements IAvoidingService, InitializingBean {

    @Autowired
    private ConvertionRobotUnit conv;

    /** The position. */
    @Autowired
    @Qualifier("currentPosition")
    private Position position;

    @Autowired
    private SD21Servos sd21;

    @Autowired
    private I2CAdcAnalogInput analogInput;

    /*@Autowired
    @Qualifier("usFront")
    private SRF02I2CSonar usFront;

    @Autowired
    @Qualifier("usGauche")
    private SRF02I2CSonar usGauche;

    @Autowired
    @Qualifier("usDroit")
    private SRF02I2CSonar usDroit;

    @Autowired
    @Qualifier("usBack")
    private SRF02I2CSonar usBack;*/

    private static final int minValueUSCm = 5;
    private static final int seuilUSCm = 60;
    private static final int distanceObstacleMm = 500;

    private int angleFrontDeg;
    private int angleGaucheDeg;
    private int angleDroitDeg;

    private int positionServosCounter = 0;

    private Map<Integer, Integer> values = new HashMap<>();
    private Map<Integer, MovingIntegerValueAverage> avgs = new HashMap<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("Initialisation du service d'évittement d'obstacle");
        /*usFront.printVersion();
        usDroit.printVersion();
        usGauche.printVersion();
        usBack.printVersion();*/
    }

    public void process() {
        switch (positionServosCounter) {
            case 0:
            case 4:
                sd21.setPosition(IConstantesServos.SONAR, IConstantesServos.SONAR_0_DEG);
                angleFrontDeg = 0;angleGaucheDeg = 90;angleDroitDeg = -90;
                break;
            case 1:
            case 3:
                sd21.setPosition(IConstantesServos.SONAR, IConstantesServos.SONAR_P_22_DEG);
                angleFrontDeg = 22;angleGaucheDeg = 112;angleDroitDeg = -67;
                break;
            case 2:
                sd21.setPosition(IConstantesServos.SONAR, IConstantesServos.SONAR_P_45_DEG);
                angleFrontDeg = 45;angleGaucheDeg = 135;angleDroitDeg = -45;
                break;
            case 5:
            case 7:
                sd21.setPosition(IConstantesServos.SONAR, IConstantesServos.SONAR_M_22_DEG);
                angleFrontDeg = -22;angleGaucheDeg = 67;angleDroitDeg = -112;
                break;
            case 6:
                sd21.setPosition(IConstantesServos.SONAR, IConstantesServos.SONAR_M_45_DEG);
                angleFrontDeg = -45;angleGaucheDeg = 45;angleDroitDeg = -135;
                break;
        }

        /*try {
            Thread.currentThread().sleep(50);
        } catch (InterruptedException e) {
            log.warn("Erreur d'attente");
        }*/

        // Lecture Sonar
        //Future<Integer> frontDistance = usFront.readValue();
        //Future<Integer> gaucheDistance = usGauche.readValue();
        //Future<Integer> droitDistance = usDroit.readValue();
        //Future<Integer> backDistance = usBack.readValue();

        //while (!(frontDistance.isDone())); // && gaucheDistance.isDone() && droitDistance.isDone()));
        try {
            if (!avgs.containsKey(angleFrontDeg)) {
                avgs.put(angleFrontDeg, new MovingIntegerValueAverage(3));
            }
            int v = avgs.get(angleFrontDeg).average(analogInput.readCapteurValue((byte) 0));
            //System.out.println(String.format("%s", v));

            //System.out.println(String.format("%s;%s;%s", frontDistance.get(), gaucheDistance.get(), droitDistance.get()));
            //System.out.println(String.format("%s", frontDistance.get()));
            // Stockage des valeurs
            values.put(angleFrontDeg, v);

            /*if (Math.abs(angleGaucheDeg) < 90 && gaucheDistance.get() >= minValueUSCm) {
                values.put(angleGaucheDeg, gaucheDistance.get());
            }
            if (Math.abs(angleDroitDeg) < 90 && droitDistance.get() >= minValueUSCm) {
                values.put(angleDroitDeg, droitDistance.get());
            }*/

            // Stockages des points d'obstacles
            List<Point> detectedPoints = new ArrayList<>();
            /*if (frontDistance.get() >= minValueUSCm && frontDistance.get() < seuilUSCm) {
                detectedPoints.add(getPointFromAngleUs(0));
            }*/
            Set<Integer> angles = values.keySet();
            for (Integer angle : angles) {
                Integer distance = values.get(angle);
                if (distance > 850) {
                    //Point pt = getPointFromAngleUs(angle);
                    //if (pt != null) {
                        detectedPoints.add(new Point());
                    //}
                }
            }

            // 3. Si inclus, on stop et on met a jour le path
            processWithPoints(detectedPoints);

        } catch (I2CException e) {
            log.error("Erreur lors de la récupération des distances : {}", e.toString());
        }
        //positionServosCounter++;
        if (positionServosCounter > 7) {
            positionServosCounter = 0;
        }
    }

    protected abstract void processWithPoints(List<Point> points);

    /**
     * Définition d'un point pour l'obstacle autour du robot.
     *
     * @param angleDeg Valeur en degré sur le robot.
     * @return Le point si présent sur la table, null sinon
     */
    private Point getPointFromAngleUs(double angleDeg) {
        // 1.A Récupération du point dans le repère cartésien de la table par
        double theta = conv.pulseToRad(position.getAngle()) + Math.toRadians(angleDeg); // On calcul la position sur l'angle du repère pour n'avoir que la translation a faire
        Point ptObstacle = new Point();
        ptObstacle.setX(distanceObstacleMm * Math.cos(theta));
        ptObstacle.setY(distanceObstacleMm * Math.sin(theta));

        // 1.B Translation du point de la position du robot
        ptObstacle.addDeltaX(conv.pulseToMm(position.getPt().getX()));
        ptObstacle.addDeltaY(conv.pulseToMm(position.getPt().getY()));

        // 2. Controles inclus sur la table
        return (checkPointInTable(ptObstacle)) ? ptObstacle : null;
    }

    /**
     * Controle que les coordonnées du point sont sur la table.
     *
     * @param pt
     * @return true si le point est sur la table
     */
    private boolean checkPointInTable(Point pt) {
        return pt.getX() > IConstantesRobot.minX && pt.getX() < IConstantesRobot.maxX
                && pt.getY() > IConstantesRobot.minY && pt.getY() < IConstantesRobot.maxY;
    }
}

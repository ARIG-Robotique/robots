package org.arig.eurobot.model;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.AbstractRobotStatus;

@Slf4j
@Data
@EqualsAndHashCode(callSuper = true)
public class RobotStatus extends AbstractRobotStatus {

    private Team team;

    @Setter(AccessLevel.NONE)
    private boolean ascenseurEnabled = false;
    public void enableAscenseur() {
        log.info("Activation ascenseur");
        ascenseurEnabled = true;
    }
    public void disableAscenseur() {
        log.info("Désactivation ascenseur");
        ascenseurEnabled = false;
    }

    @Setter(AccessLevel.NONE)
    private int nbPied = 0;

    public void incNbPied() {
        nbPied ++;
    }
    public void resetNbPied() {
        nbPied = 0;
    }

    private boolean initialCollectFinished = false;

    // Présence dans robot
    private boolean balleDansAscenseur = true;
    private boolean tapisPresent = true;

    // Gobelets
    private boolean gobeletEscalierJauneRecupere = false;
    private boolean gobeletEscalierVertRecupere = false;
    private boolean gobeletCentraleRecupere = false;
    private boolean gobeletClapJauneRecupere = false;
    private boolean gobeletClapVertRecuere = false;

    // Zones dépose salle cinema principal
    private boolean gobeletSalleCinemaEscalierPose = false;
    private boolean gobeletSalleCinemaClapPose = false;
    private boolean gobeletSalleCinemaPrincipalPose = false;

    @Setter(AccessLevel.NONE)
    private int indexZoneDeposeSallePrincipale = 0;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private double [] zonesDeposeSallePrincipalJaune = {250, 350, 450};

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private double [] zonesDeposeSallePrincipalVert = {2750, 2650, 2550};

    public double getYZoneDeposePrincipale() {
        double [] tmp = (team == Team.JAUNE) ? zonesDeposeSallePrincipalJaune : zonesDeposeSallePrincipalVert;
        if (indexZoneDeposeSallePrincipale >= tmp.length) {
            indexZoneDeposeSallePrincipale = tmp.length - 1;
        }
        double value = tmp[indexZoneDeposeSallePrincipale];
        indexZoneDeposeSallePrincipale++;
        return value;
    }
}

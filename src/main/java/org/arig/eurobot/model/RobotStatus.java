package org.arig.eurobot.model;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.arig.robot.model.AbstractRobotStatus;

@Slf4j
@Data
@EqualsAndHashCode(callSuper = true)
public class RobotStatus extends AbstractRobotStatus {

    private Team team;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private StopWatch matchTime = new StopWatch();

    public void startMatch() {
        matchTime.start();
    }
    public void stopMatch() {
        matchTime.stop();
    }
    public long getElapsedTime() {
        return matchTime.getTime();
    }

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
    private boolean calageBordureEnabled = false;
    public void enableCalageBordure() {
        log.info("Activation calage bordure");
        calageBordureEnabled = true;
    }
    public void disableCalageBordure() {
        log.info("Désactivation calage bordure");
        calageBordureEnabled = false;
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
    private boolean gobeletClapVertRecupere = false;

    // Pieds
    private boolean pied1Recupere = false; // Collecte init
    private boolean pied2Recupere = false; // Collecte init
    private boolean pied3Recupere = false; // Collecte init
    private boolean pied4Recupere = false; // Tapis / Escalier
    private boolean pied5Recupere = false; // Proche repère
    private boolean pied6Recupere = false; // Proche clap 1
    private boolean pied7Recupere = false; // Proche clap 1 (dans le coin)
    private boolean pied8Recupere = false; // Tapis / Escalier (dans le coin)

    // Clap
    private boolean clap1Fait = false;
    private boolean clap2Fait = false;
    private boolean clap3Fait = false;

    // Zones dépose salle cinema principal
    private boolean gobeletSalleCinemaEscalierPose = false;
    private boolean gobeletSalleCinemaClapPose = false;
    private boolean gobeletSalleCinemaPrincipalPose = false;

    @Setter(AccessLevel.NONE)
    private int indexZoneDeposeSallePrincipale = 0;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private double [] zonesDeposeSallePrincipalJaune = {300, 400, 500};

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private double [] zonesDeposeSallePrincipalVert = {2700, 2600, 2500};

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

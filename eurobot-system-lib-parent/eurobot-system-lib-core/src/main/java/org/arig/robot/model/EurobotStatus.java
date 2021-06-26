package org.arig.robot.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IEurobotConfig;
import org.arig.robot.model.communication.balise.enums.EDirectionGirouette;
import org.arig.robot.utils.ArigCollectionUtils;
import org.arig.robot.utils.EcueilUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Data
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper = false)
public class EurobotStatus extends AbstractRobotStatus {

    public EurobotStatus(boolean mainRobot) {
        super(IEurobotConfig.matchTimeMs, mainRobot);
    }

    @Setter(AccessLevel.NONE)
    private ETeam team = ETeam.UNKNOWN;

    public void setTeam(ETeam team) {
        this.team = team;

        final int tirageEcueil = 1;
        couleursEcueilEquipe(EcueilUtils.tirageEquipe(team()));
        couleursEcueilCommun(EcueilUtils.tirageCommunEquipe(team(), tirageEcueil));
    }

    private EDirectionGirouette directionGirouette = EDirectionGirouette.UNKNOWN;

    private ECouleurBouee[] couleursEcueilEquipe = new ECouleurBouee[]{ECouleurBouee.INCONNU, ECouleurBouee.INCONNU, ECouleurBouee.INCONNU, ECouleurBouee.INCONNU, ECouleurBouee.INCONNU};
    @Setter(AccessLevel.NONE)
    private ECouleurBouee[] couleursEcueilCommunEquipe = new ECouleurBouee[]{ECouleurBouee.ROUGE, ECouleurBouee.INCONNU, ECouleurBouee.INCONNU, ECouleurBouee.INCONNU, ECouleurBouee.VERT};
    @Setter(AccessLevel.NONE)
    private ECouleurBouee[] couleursEcueilCommunAdverse = new ECouleurBouee[]{ECouleurBouee.ROUGE, ECouleurBouee.INCONNU, ECouleurBouee.INCONNU, ECouleurBouee.INCONNU, ECouleurBouee.VERT};

    public void couleursEcueilCommun(ECouleurBouee[] couleurs) {
        for (int i = 0; i < couleurs.length; i++) {
            couleursEcueilCommunEquipe[i] = couleurs[i];
            couleursEcueilCommunAdverse[4 - i] = couleurs[i] == ECouleurBouee.INCONNU ? ECouleurBouee.INCONNU : couleurs[i] == ECouleurBouee.VERT ? ECouleurBouee.ROUGE : ECouleurBouee.VERT;
        }
    }

    private byte ecueilCommunBleuDispo = 5;
    private byte ecueilCommunJauneDispo = 5;

    /**
     * STATUT
     */

    ///////////////////////////////////////////////////////
    //                      Bouées                       //
    ///////////////////////////////////////////////////////
    //              5                    12              //
    //                                                   //
    //     1          6               11         13      //
    // -----| 2                               14 |-------//
    // Bleu |            7         10            | Jaune //
    // -----| 3                               15 |-------//
    //     4               8     9               16      //
    // -----------  -----------------------  ----------- //

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private List<Bouee> bouees = Arrays.asList(
            new Bouee(1, ECouleurBouee.ROUGE, new Point(300, 2000 - 400)),
            new Bouee(2, ECouleurBouee.VERT, new Point(450, 2000 - 510)),
            new Bouee(3, ECouleurBouee.ROUGE, new Point(450, 2000 - 1080)),
            new Bouee(4, ECouleurBouee.VERT, new Point(300, 2000 - 1200)),
            new Bouee(5, ECouleurBouee.ROUGE, new Point(670, 2000 - 100)),
            new Bouee(6, ECouleurBouee.VERT, new Point(950, 2000 - 400)),
            new Bouee(7, ECouleurBouee.ROUGE, new Point(1100, 2000 - 800)),
            new Bouee(8, ECouleurBouee.VERT, new Point(1270, 2000 - 1200)),
            new Bouee(9, ECouleurBouee.ROUGE, new Point(1730, 2000 - 1200)),
            new Bouee(10, ECouleurBouee.VERT, new Point(1900, 2000 - 800)),
            new Bouee(11, ECouleurBouee.ROUGE, new Point(2050, 2000 - 400)),
            new Bouee(12, ECouleurBouee.VERT, new Point(2330, 2000 - 100)),
            new Bouee(13, ECouleurBouee.VERT, new Point(2700, 2000 - 400)),
            new Bouee(14, ECouleurBouee.ROUGE, new Point(2550, 2000 - 510)),
            new Bouee(15, ECouleurBouee.VERT, new Point(2550, 2000 - 1080)),
            new Bouee(16, ECouleurBouee.ROUGE, new Point(2700, 2000 - 1200))
    );

    public void boueePrise(int... numeros) {
        for (int i : numeros) {
            bouees.get(i - 1).setPrise();
        }
    }

    public boolean boueePresente(int numero) {
        return bouees.get(numero - 1).presente();
    }

    public Point boueePt(int numero) {
        return bouees.get(numero - 1).pt();
    }

    public ECouleurBouee boueeCouleur(int numero) {
        return bouees.get(numero - 1).couleur();
    }

    private List<Bouee> hautFond = new ArrayList<>();

    public boolean hautFondEmpty() {
        return hautFond.isEmpty();
    }

    private boolean ecueilEquipePris = false;
    private boolean ecueilCommunEquipePris = false;
    private boolean ecueilCommunAdversePris = false;
    private boolean mancheAAir1 = false;
    private boolean mancheAAir2 = false;
    private boolean phare = false;
    private boolean pavillon = false;
    private boolean pavillonSelf = false;
    private EPort port = EPort.AUCUN;
    private EPort otherPort = EPort.AUCUN;

    public boolean inPort() {
        return port.isInPort();
    }

    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private List<ECouleurBouee> grandPort = new ArrayList<>();

    public void deposeGrandPort(ECouleurBouee... bouees) {
        ArigCollectionUtils.addAllIgnoreNull(grandPort, bouees);
    }

    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private List<ECouleurBouee> petitPort = new ArrayList<>();

    public void deposePetitPort(ECouleurBouee... bouees) {
        ArigCollectionUtils.addAllIgnoreNull(petitPort, bouees);
    }

    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    protected GrandChenaux grandChenaux = new GrandChenaux();

    public boolean grandChenalVertEmpty() {
        return grandChenaux.chenalVertEmpty();
    }

    public boolean grandChenalRougeEmpty() {
        return grandChenaux.chenalRougeEmpty();
    }

    public void deposeGrandChenalVert(GrandChenaux.Line line, ECouleurBouee... bouees) {
        grandChenaux.addVert(line, bouees);
    }

    public void deposeGrandChenalRouge(GrandChenaux.Line line, ECouleurBouee... bouees) {
        grandChenaux.addRouge(line, bouees);
    }

    public Chenaux cloneGrandChenaux() {
        return grandChenaux.copy();
    }

    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private PetitChenaux petitChenaux = new PetitChenaux();

    public boolean petitChenalVertEmpty() {
        return petitChenaux.chenalVertEmpty();
    }

    public boolean petitChenalRougeEmpty() {
        return petitChenaux.chenalRougeEmpty();
    }

    public void deposePetitChenalVert(ECouleurBouee... bouees) {
        petitChenaux.addVert(bouees);
    }

    public void deposePetitChenalRouge(ECouleurBouee... bouees) {
        petitChenaux.addRouge(bouees);
    }

    public Chenaux clonePetitChenaux() {
        return petitChenaux.copy();
    }

    public int calculerPoints() {
        int points = 0;

        // le robot secondaire ne compte pas les points si la comm est ok
        if (groupOk() && !mainRobot()) {
            return points;
        }

        points += 2 + (phare ? 13 : 0);
        points += grandPort.size();
        points += petitPort.size();
        points += grandChenaux.score();
        points += petitChenaux.score();
        points += (mancheAAir1 && mancheAAir2) ? 15 : (mancheAAir1 || mancheAAir2) ? 5 : 0;
        if (twoRobots()) {
            if (port.isInPort() && otherPort.isInPort() && port != otherPort) {
                // cas spécial ou chaque robot est dans un port différent
                // 0 points
            } else {
                points += calculerPointsPort(port, 1);
                points += calculerPointsPort(otherPort, 1);
            }
        } else {
            points += calculerPointsPort(port, 2);
        }
        points += pavillon ? 10 : 0;
        return points;
    }

    private int calculerPointsPort(EPort port, int mult) {
        switch (port) {
            case NORD:
                return directionGirouette == EDirectionGirouette.UP ? 10 * mult : 3 * mult;
            case SUD:
                return directionGirouette == EDirectionGirouette.DOWN ? 10 * mult : 3 * mult;
            default:
                return 0;
        }
    }

    @Override
    public Map<String, Integer> scoreStatus() {
        Map<String, Integer> r = new HashMap<>();
        r.put("Phare", 2 + (phare ? 13 : 0));
        r.put("Grand port", grandPort.size());
        r.put("Petit port", petitPort.size());
        r.put("Grand chenaux", grandChenaux.score());
        r.put("Petit chenaux", petitChenaux.score());
        r.put("Manche à air", (mancheAAir1 && mancheAAir2) ? 15 : (mancheAAir1 || mancheAAir2) ? 5 : 0);
        if (twoRobots()) {
            if (port.isInPort() && otherPort.isInPort() && port != otherPort) {
                r.put("Port", 0);
            } else {
                r.put("Port", calculerPointsPort(port, 1) +  calculerPointsPort(otherPort, 1));
            }
        } else {
            r.put("Port", calculerPointsPort(port, 2));
        }
        r.put("Pavillon", pavillon ? 10 : 0);
        return r;
    }

    @Override
    public Map<String, Object> gameStatus() {
        Map<String, Object> r = new HashMap<>();
        r.put("bouees", bouees.stream().map(b -> !b.presente()).collect(Collectors.toList()));
        r.put("hautFond", new ArrayList<>(hautFond));
        r.put("grandPort", grandPort);
        r.put("grandChenalVert", grandChenaux.chenalVert);
        r.put("grandChenalRouge", grandChenaux.chenalRouge);
        r.put("petitPort", petitPort);
        r.put("petitChenalVert", petitChenaux.chenalVert);
        r.put("petitChenalRouge", petitChenaux.chenalRouge);
        r.put("phare", phare);
        r.put("mancheAAir1", mancheAAir1);
        r.put("mancheAAir2", mancheAAir2);
        r.put("ecueilEquipePris", ecueilEquipePris);
        r.put("ecueilCommunEquipePris", ecueilCommunEquipePris);
        r.put("ecueilCommunAdversePris", ecueilCommunAdversePris);
        r.put("girouette", directionGirouette);
        r.put("couleursEcueilEquipe", couleursEcueilEquipe);
        r.put("couleursEcueilCommunEquipe", couleursEcueilCommunEquipe);
        r.put("couleursEcueilCommunAdverse", couleursEcueilCommunAdverse);
        return r;
    }

}

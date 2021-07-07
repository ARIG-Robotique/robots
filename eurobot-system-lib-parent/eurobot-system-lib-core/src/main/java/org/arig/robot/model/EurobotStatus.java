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
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    private EStrategy strategy = EStrategy.BASIC;

    public void setTeam(ETeam team) {
        this.team = team;

        couleursEcueilEquipe(EcueilUtils.tirageEquipe(team));
        //couleursEcueilCommun(EcueilUtils.tirageCommunEquipe(team, 1));
    }

    private boolean doubleDepose = false;
    private boolean deposePartielle = false;
    private boolean echangeEcueil = false;

    private EDirectionGirouette directionGirouette = EDirectionGirouette.UNKNOWN;

    public void directionGirouette(EDirectionGirouette directionGirouette) {
        log.info("[RS] girouette {}", directionGirouette);
        this.directionGirouette = directionGirouette;
    }

    private ECouleurBouee[] couleursEcueilEquipe = new ECouleurBouee[]{ECouleurBouee.INCONNU, ECouleurBouee.INCONNU, ECouleurBouee.INCONNU, ECouleurBouee.INCONNU, ECouleurBouee.INCONNU};
    @Setter(AccessLevel.NONE)
    private ECouleurBouee[] couleursEcueilCommunEquipe = new ECouleurBouee[]{ECouleurBouee.ROUGE, ECouleurBouee.INCONNU, ECouleurBouee.INCONNU, ECouleurBouee.INCONNU, ECouleurBouee.VERT};
    @Setter(AccessLevel.NONE)
    private ECouleurBouee[] couleursEcueilCommunAdverse = new ECouleurBouee[]{ECouleurBouee.ROUGE, ECouleurBouee.INCONNU, ECouleurBouee.INCONNU, ECouleurBouee.INCONNU, ECouleurBouee.VERT};

    public void couleursEcueilCommun(ECouleurBouee[] couleurs) {
        log.info("[RS] Ecueil commun {}", Stream.of(couleurs).map(Enum::name).collect(Collectors.joining(",")));
        for (int i = 0; i < couleurs.length; i++) {
            couleursEcueilCommunEquipe[i] = couleurs[i];
            couleursEcueilCommunAdverse[4 - i] = couleurs[i] == ECouleurBouee.INCONNU ? ECouleurBouee.INCONNU : couleurs[i] == ECouleurBouee.VERT ? ECouleurBouee.ROUGE : ECouleurBouee.VERT;
        }
    }

    private byte ecueilCommunBleuDispo = 5;

    public void ecueilCommunBleuDispo(byte ecueilCommunBleuDispo) {
        log.info("[RS] Ecueil commun bleu dispo {}", ecueilCommunBleuDispo);
        this.ecueilCommunBleuDispo = ecueilCommunBleuDispo;
    }

    private byte ecueilCommunJauneDispo = 5;

    public void ecueilCommunJauneDispo(byte ecueilCommunJauneDispo) {
        log.info("[RS] Ecueil commun jaune dispo {}", ecueilCommunJauneDispo);
        this.ecueilCommunJauneDispo = ecueilCommunJauneDispo;
    }

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
        log.info("[RS] bouees prises {}", Arrays.stream(numeros).mapToObj(String::valueOf).collect(Collectors.joining(",")));
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

    public void hautFond(List<Bouee> hautFond) {
        log.info("[RS] haut fond {}", hautFond.stream().map(Bouee::couleur).map(Enum::name).collect(Collectors.joining(",")));
        this.hautFond = hautFond;
    }

    public boolean hautFondEmpty() {
        return hautFond.isEmpty();
    }

    private boolean hautFondPris = false;

    public void hautFondPris(boolean hautFondPris) {
        log.info("[RS] haut fond pris {}", hautFondPris);
        this.hautFondPris = hautFondPris;
    }

    private boolean ecueilEquipePris = false;

    public void ecueilEquipePris(boolean ecueilEquipePris) {
        log.info("[RS] ecueil equipe pris {}", ecueilEquipePris);
        this.ecueilEquipePris = ecueilEquipePris;
    }

    private boolean ecueilCommunEquipePris = false;

    public void ecueilCommunEquipePris(boolean ecueilCommunEquipePris) {
        log.info("[RS] ecueil commun equipe pris {}", ecueilCommunEquipePris);
        this.ecueilCommunEquipePris = ecueilCommunEquipePris;
    }

    private boolean ecueilCommunAdversePris = false;

    public void ecueilCommunAdversePris(boolean ecueilCommunAdversePris) {
        log.info("[RS] ecueil commun adverse pris {}", ecueilCommunAdversePris);
        this.ecueilCommunAdversePris = ecueilCommunAdversePris;
    }

    private boolean mancheAAir1 = false;

    public void mancheAAir1(boolean mancheAAir1) {
        log.info("[RS] manche à air 1 {}", mancheAAir1);
        this.mancheAAir1 = mancheAAir1;
    }

    private boolean mancheAAir2 = false;

    public void mancheAAir2(boolean mancheAAir2) {
        log.info("[RS] manche à aire 2 {}", mancheAAir2);
        this.mancheAAir2 = mancheAAir2;
    }

    private boolean phare = false;

    public void phare(boolean phare) {
        log.info("[RS] phare {}", phare);
        this.phare = phare;
    }

    private boolean pavillon = false;

    public void pavillon(boolean pavillon) {
        log.info("[RS] pavillon {}", pavillon);
        this.pavillon = pavillon;
    }

    private boolean pavillonSelf = false;

    public void pavillonSelf(boolean pavillonSelf) {
        log.info("[RS] pavillon (perso) {}", pavillonSelf);
        this.pavillonSelf = pavillonSelf;
    }

    private EPort port = EPort.AUCUN;

    public void port(EPort port) {
        log.info("[RS] port {}", port);
        this.port = port;
    }

    private EPort otherPort = EPort.AUCUN;

    public void otherPort(EPort otherPort) {
        log.info("[RS] other port {}", otherPort);
        this.otherPort = otherPort;
    }

    public boolean inPort() {
        return port.isInPort();
    }

    private boolean echangeReady = false;

    public void echangeReady(boolean echangeReady) {
        log.info("[RS] échange ready {}", echangeReady);
        this.echangeReady = echangeReady;
    }

    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private List<ECouleurBouee> grandPort = new ArrayList<>();

    public void deposeGrandPort(ECouleurBouee... bouees) {
        log.info("[RS] dépose grand port {}", Stream.of(bouees).filter(Objects::nonNull).map(Enum::name).collect(Collectors.joining(",")));
        ArigCollectionUtils.addAllIgnoreNull(grandPort, bouees);
    }

    public boolean grandPortEmpty() {
        return grandPort.isEmpty();
    }

    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private List<ECouleurBouee> petitPort = new ArrayList<>();

    public void deposePetitPort(ECouleurBouee... bouees) {
        log.info("[RS] dépose petit port {}", Stream.of(bouees).filter(Objects::nonNull).map(Enum::name).collect(Collectors.joining(",")));
        ArigCollectionUtils.addAllIgnoreNull(petitPort, bouees);
    }

    @Setter(AccessLevel.NONE)
    private int stepsPetitPort = 0;

    public void incStepsPetitPort() {
        log.info("[RS] inc steps petit port");
        this.stepsPetitPort++;
    }

    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    protected GrandChenaux grandChenaux = new GrandChenaux();

    public void deposeGrandChenalVert(GrandChenaux.Line line, int index, ECouleurBouee bouee) {
        log.info("[RS] Dépose grand chenal vert {} : {} at {}", line, bouee, index);
        grandChenaux.addVert(line, index, bouee);
    }

    public void deposeGrandChenalRouge(GrandChenaux.Line line, int index, ECouleurBouee bouee) {
        log.info("[RS] Dépose grand chenal rouge {} : {} at {}", line, bouee, index);
        grandChenaux.addRouge(line, index, bouee);
    }

    public void deposeGrandChenalVert(GrandChenaux.Line line, ECouleurBouee... bouees) {
        log.info("[RS] Dépose grand chenal vert {} : {}", line, Stream.of(bouees)
                .map(b -> b == null ? "null" : b.name())
                .collect(Collectors.joining(",")));
        grandChenaux.addVert(line, bouees);
    }

    public void deposeGrandChenalRouge(GrandChenaux.Line line, ECouleurBouee... bouees) {
        log.info("[RS] Dépose grand chenal rouge {} : {}", line, Stream.of(bouees)
                .map(b -> b == null ? "null" : b.name())
                .collect(Collectors.joining(",")));
        grandChenaux.addRouge(line, bouees);
    }

    public boolean grandChenalVertBordureEmpty() {
        return grandChenaux.chenalVertBordureEmpty();
    }

    public boolean grandChenalVertEmpty() {
        return grandChenaux.chenalVertEmpty();
    }

    public boolean grandChenalRougeBordureEmpty() {
        return grandChenaux.chenalRougeBordureEmpty();
    }

    public boolean grandChenalRougeEmpty() {
        return grandChenaux.chenalRougeEmpty();
    }

    public Chenaux cloneGrandChenaux() {
        return grandChenaux.copy();
    }

    public List<ECouleurBouee> getGrandChenalVert(GrandChenaux.Line line) {
        return grandChenaux.getVert(line);
    }

    public List<ECouleurBouee> getGrandChenalRouge(GrandChenaux.Line line) {
        return grandChenaux.getRouge(line);
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
        log.info("[RS] dépose petit chenal vert {}", Stream.of(bouees).filter(Objects::nonNull).map(Enum::name).collect(Collectors.joining(",")));
        petitChenaux.addVert(bouees);
    }

    public void deposePetitChenalRouge(ECouleurBouee... bouees) {
        log.info("[RS] dépose petit chenal rouge {}", Stream.of(bouees).filter(Objects::nonNull).map(Enum::name).collect(Collectors.joining(",")));
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
                r.put("Port", calculerPointsPort(port, 1) + calculerPointsPort(otherPort, 1));
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

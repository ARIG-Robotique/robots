package org.arig.robot.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.communication.balise.enums.EDirectionGirouette;
import org.arig.robot.utils.EcueilUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Data
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper = true)
public class NerellRobotStatus extends AbstractRobotStatus {

    public NerellRobotStatus(final int matchTimeMs) {
        super(matchTimeMs);
    }

    @Override
    public void setTeam(int value) {
        super.setTeam(value);

        final int tirageEcueil = 1;
        couleursEcueilEquipe(EcueilUtils.tirageEquipe(team()));
        couleursEcueilCommunEquipe(EcueilUtils.tirageCommunEquipe(team(), tirageEcueil));
        couleursEcueilCommunAdverse(EcueilUtils.tirageCommunAdverse(team(), tirageEcueil));
    }

    @Override
    public void stopMatch() {
        super.stopMatch();

        this.disableBalise();
    }

    private EStrategy strategy = EStrategy.BASIC_NORD;

    public void setStrategy(int value) {
        switch (value) {
            case 0:
                strategy = EStrategy.BASIC_NORD;
                break;
            case 1:
                strategy = EStrategy.BASIC_SUD;
                break;
            case 2:
                strategy = EStrategy.AGGRESSIVE;
                break;
            case 3:
                strategy = EStrategy.FINALE;
                break;
            default:
                throw new IllegalArgumentException("Strategy invalide");
        }
    }

    private boolean doubleDepose;

    private boolean deposePartielle;

    private EDirectionGirouette directionGirouette = EDirectionGirouette.UNKNOWN;

    private ECouleurBouee[] couleursEcueilEquipe = new ECouleurBouee[]{ECouleurBouee.INCONNU, ECouleurBouee.INCONNU, ECouleurBouee.INCONNU, ECouleurBouee.INCONNU, ECouleurBouee.INCONNU};
    private ECouleurBouee[] couleursEcueilCommunEquipe = new ECouleurBouee[]{ECouleurBouee.ROUGE, ECouleurBouee.INCONNU, ECouleurBouee.INCONNU, ECouleurBouee.INCONNU, ECouleurBouee.VERT};
    private ECouleurBouee[] couleursEcueilCommunAdverse = new ECouleurBouee[]{ECouleurBouee.ROUGE, ECouleurBouee.INCONNU, ECouleurBouee.INCONNU, ECouleurBouee.INCONNU, ECouleurBouee.VERT};

    private byte ecueilCommunBleuDispo = 5;
    private byte ecueilCommunJauneDispo = 5;

    @Setter(AccessLevel.NONE)
    private boolean pincesAvantEnabled = false;

    public void enablePincesAvant() {
        pincesAvantEnabled = true;
    }

    public void disablePincesAvant() {
        pincesAvantEnabled = false;
    }

    @Setter(AccessLevel.NONE)
    private boolean baliseEnabled = false;

    public void enableBalise() {
        baliseEnabled = true;
    }

    public void disableBalise() {
        baliseEnabled = false;
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

    public Bouee bouee(int numero) {
        return bouees.get(numero - 1);
    }

    private List<Bouee> hautFond = new ArrayList<>();

    private boolean ecueilEquipePris = false;
    private boolean ecueilCommunEquipePris = false;
    private boolean ecueilCommunAdversePris = false;
    private boolean mancheAAir1 = false;
    private boolean mancheAAir2 = false;
    private boolean phare = false;
    private boolean bonPort = false;
    private boolean mauvaisPort = false;
    private boolean pavillon = false;
    private boolean deposePartielleDone = false;

    public boolean inPort() {
        return bonPort || mauvaisPort;
    }

    @Setter(AccessLevel.NONE)
    private List<ECouleurBouee> grandPort = new ArrayList<>();

    @Setter(AccessLevel.NONE)
    private List<ECouleurBouee> petitPort = new ArrayList<>();

    @Setter(AccessLevel.NONE)
    private GrandChenaux grandChenaux = new GrandChenaux();

    @Setter(AccessLevel.NONE)
    private PetitChenaux petitChenaux = new PetitChenaux();

    // De gauche a droite, dans le sens du robot
    @Accessors(fluent = true)
    private ECouleurBouee[] pincesArriere = new ECouleurBouee[]{null, null, null, null, null};

    // De gauche à droite, dans le sens du robot
    @Accessors(fluent = true)
    private ECouleurBouee[] pincesAvant = new ECouleurBouee[]{null, null, null, null};

    public void pinceArriere(int pos, ECouleurBouee bouee) {
        pincesArriere[pos] = bouee;
    }

    public void pinceAvant(int pos, ECouleurBouee bouee) {
        pincesAvant[pos] = bouee;
    }

    public void clearPincesArriere() {
        Arrays.fill(pincesArriere, null);
    }

    public void clearPincesAvant() {
        Arrays.fill(pincesAvant, null);
    }

    public boolean pincesArriereEmpty() {
        return Arrays.stream(pincesArriere).noneMatch(Objects::nonNull);
    }

    public boolean pincesAvantEmpty() {
        return Arrays.stream(pincesAvant).noneMatch(Objects::nonNull);
    }

    public int calculerPoints() {
        int points = 2 + (phare ? 13 : 0);
        points += grandPort.size();
        points += petitPort.size();
        points += grandChenaux.score();
        points += petitChenaux.score();
        points += (mancheAAir1 && mancheAAir2) ? 15 : (mancheAAir1 || mancheAAir2) ? 5 : 0;
        points += bonPort ? 20 : (mauvaisPort ? 6 : 0);
        points += pavillon ? 10 : 0;
        return points;
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
        r.put("Port", bonPort ? 20 : (mauvaisPort ? 6 : 0));
        r.put("Pavillon", pavillon ? 10 : 0);
        return r;
    }

    @Override
    public Map<String, Object> gameStatus() {
        Map<String, Object> r = new HashMap<>();
        r.put("bouees", bouees.stream().map(Bouee::prise).collect(Collectors.toList()));
        r.put("hautFond", new ArrayList<>(hautFond));
        r.put("grandPort", grandPort);
        r.put("grandChenalVert", grandChenaux.chenalVert);
        r.put("grandChenalRouge", grandChenaux.chenalRouge);
        r.put("petitPort", petitPort);
        r.put("petitChenalVert", petitChenaux.chenalVert);
        r.put("petitChenalRouge", petitChenaux.chenalRouge);
        r.put("pincesAvant", pincesAvant);
        r.put("pincesArriere", pincesArriere);
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

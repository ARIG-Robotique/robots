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
import org.arig.robot.utils.EcueilUtils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
public class EurobotStatus extends AbstractRobotStatus<EStatusEvent> {

    public EurobotStatus() {
        super(IEurobotConfig.matchTimeMs, EStatusEvent.class);
    }

    private ETeam team = ETeam.UNKNOWN;

    private boolean twoRobots = false;

    public void setTeam(int value) {
        team = ETeam.values()[value];

        final int tirageEcueil = 1;
        couleursEcueilEquipe(EcueilUtils.tirageEquipe(team()));
        couleursEcueilCommunEquipe(EcueilUtils.tirageCommunEquipe(team(), tirageEcueil));
        couleursEcueilCommunAdverse(EcueilUtils.tirageCommunAdverse(team(), tirageEcueil));
    }

    private EDirectionGirouette directionGirouette = EDirectionGirouette.UNKNOWN;

    private ECouleurBouee[] couleursEcueilEquipe = new ECouleurBouee[]{ECouleurBouee.INCONNU, ECouleurBouee.INCONNU, ECouleurBouee.INCONNU, ECouleurBouee.INCONNU, ECouleurBouee.INCONNU};
    private ECouleurBouee[] couleursEcueilCommunEquipe = new ECouleurBouee[]{ECouleurBouee.ROUGE, ECouleurBouee.INCONNU, ECouleurBouee.INCONNU, ECouleurBouee.INCONNU, ECouleurBouee.VERT};
    private ECouleurBouee[] couleursEcueilCommunAdverse = new ECouleurBouee[]{ECouleurBouee.ROUGE, ECouleurBouee.INCONNU, ECouleurBouee.INCONNU, ECouleurBouee.INCONNU, ECouleurBouee.VERT};

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
            null,
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

    public void boueePrise(int numero) {
        bouees.get(numero).setPrise();
    }

    public void boueePresente(int numero, boolean presente) {
        bouees.get(numero).setPresente(presente);
    }

    public boolean boueePresente(int numero) {
        return bouees.get(numero).presente();
    }

    public Point boueePt(int numero) {
        return bouees.get(numero).pt();
    }

    public ECouleurBouee boueeCouleur(int numero) {
        return bouees.get(numero).couleur();
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
    private boolean deposePartielleDone = false;

    @Setter(AccessLevel.NONE)
    private EPort port = EPort.AUCUN;

    @Setter(AccessLevel.NONE)
    private EPort otherPort = EPort.AUCUN;

    public void bonPort() {
        port = EPort.BON;
    }

    public void mauvaisPort() {
        port = EPort.MAUVAIS;
    }

    public boolean inPort() {
        return port != EPort.AUCUN;
    }

    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private List<ECouleurBouee> grandPort = new ArrayList<>();

    public void deposeGrandPort(ECouleurBouee bouee) {
        grandPort.add(bouee);
    }

    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private List<ECouleurBouee> petitPort = new ArrayList<>();

    public void deposePetitPort(ECouleurBouee bouee) {
        petitPort.add(bouee);
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

    public void deposeGrandChenalVert(ECouleurBouee... bouees) {
        grandChenaux.addVert(bouees);
    }

    public void deposeGrandChenalRouge(ECouleurBouee... bouees) {
        grandChenaux.addRouge(bouees);
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
        int points = 2 + (phare ? 13 : 0);
        points += grandPort.size();
        points += petitPort.size();
        points += grandChenaux.score();
        points += petitChenaux.score();
        points += (mancheAAir1 && mancheAAir2) ? 15 : (mancheAAir1 || mancheAAir2) ? 5 : 0;
        if (twoRobots) {
            points += port == EPort.BON ? 10 : (port == EPort.MAUVAIS ? 3 : 0);
            points += otherPort == EPort.BON ? 10 : (otherPort == EPort.MAUVAIS ? 3 : 0);
        } else {
            points += port == EPort.BON ? 20 : (port == EPort.MAUVAIS ? 6 : 0);
        }
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
        if (twoRobots) {
            r.put("Port 1", port == EPort.BON ? 10 : (port == EPort.MAUVAIS ? 3 : 0));
            r.put("Port 2", otherPort == EPort.BON ? 10 : (port == EPort.MAUVAIS ? 3 : 0));
        } else {
            r.put("Port", port == EPort.BON ? 20 : (port == EPort.MAUVAIS ? 6 : 0));
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

    /**
     * SERIALIZATION
     * Implémentation custom
     */

    public void writeStatus(ObjectOutputStream os) throws IOException {
        os.writeByte(team.ordinal());

        os.writeBoolean(ecueilEquipePris);
        os.writeBoolean(ecueilCommunEquipePris);
        os.writeBoolean(ecueilCommunAdversePris);
        os.writeBoolean(mancheAAir1);
        os.writeBoolean(mancheAAir2);
        os.writeBoolean(phare);

        os.writeByte(port.ordinal());

        os.writeByte(bouees.size()); // pas utilisé mais au cas ou
        for (Bouee bouee : bouees) {
            if (bouee != null) {
                os.writeByte(bouee.state().ordinal());
            }
        }

        os.writeByte(grandPort.size());
        for (ECouleurBouee bouee : grandPort) {
            os.writeByte(bouee.ordinal());
        }

        os.writeByte(petitPort.size());
        for (ECouleurBouee bouee : petitPort) {
            os.writeByte(bouee.ordinal());
        }

        grandChenaux.writeObject(os);
        petitChenaux.writeObject(os);

        os.writeByte(directionGirouette.ordinal());
        for (ECouleurBouee bouee : couleursEcueilCommunEquipe) {
            os.writeByte(bouee.ordinal());
        }

        os.writeByte(hautFond.size());
        for (Bouee bouee : hautFond) {
            os.writeByte(bouee.couleur().ordinal());
            os.writeShort((int) bouee.pt().getX());
            os.writeShort((int) bouee.pt().getY());
        }
    }

    public void readStatus(ObjectInputStream is) throws IOException {
        setTeam(is.readByte());

        ecueilEquipePris = is.readBoolean();
        ecueilCommunEquipePris = is.readBoolean();
        ecueilCommunAdversePris = is.readBoolean();
        mancheAAir1 = is.readBoolean();
        mancheAAir2 = is.readBoolean();
        phare = is.readBoolean();

        otherPort = EPort.values()[is.readByte()];

        byte nbBoues = is.readByte(); // pas utilisé mais au cas ou
        for (Bouee bouee : bouees) {
            if (bouee != null) {
                bouee.state(Bouee.EState.values()[is.readByte()]);
            }
        }

        grandPort.clear();
        byte nbGrandPort = is.readByte();
        for (byte i = 0; i < nbGrandPort; i++) {
            grandPort.add(ECouleurBouee.values()[is.readByte()]);
        }

        petitPort.clear();
        byte nbPetitPort = is.readByte();
        for (byte i = 0; i < nbPetitPort; i++) {
            petitPort.add(ECouleurBouee.values()[is.readByte()]);
        }

        grandChenaux.readObject(is);
        petitChenaux.readObject(is);

        directionGirouette = EDirectionGirouette.values()[is.readByte()];
        for (byte i = 0; i < 5; i++) {
            ECouleurBouee bouee = ECouleurBouee.values()[is.readByte()];
            couleursEcueilCommunEquipe[i] = bouee;
            couleursEcueilCommunAdverse[4 - i] = bouee == ECouleurBouee.INCONNU ? ECouleurBouee.INCONNU : bouee == ECouleurBouee.VERT ? ECouleurBouee.ROUGE : ECouleurBouee.VERT;
        }

        hautFond.clear();
        byte nbHautFond = is.readByte();
        for (byte i = 0; i < nbHautFond; i++) {
            ECouleurBouee couleur = ECouleurBouee.values()[is.readByte()];
            Point pt = new Point(is.readShort(), is.readShort());
            hautFond.add(new Bouee(0, couleur, pt));
        }
    }

    @Override
    public void integrateJournal(List<EventLog<EStatusEvent>> journal, boolean self) {
        for (EventLog<EStatusEvent> event : journal) {
            byte value = event.getValue();
            switch (event.getEvent()) {
                case ECUEIL_EQUIPE_PRIS:
                    ecueilEquipePris = true;
                    break;
                case ECUEIL_COMMUN_EQUIPE_PRIS:
                    ecueilCommunEquipePris = true;
                    break;
                case ECUEIL_COMMUN_ADVERSE_PRIS:
                    ecueilCommunAdversePris = true;
                    break;
                case MANCHE_AIR_1:
                    mancheAAir1 = true;
                    break;
                case MANCHE_AIR_2:
                    mancheAAir2 = true;
                    break;
                case PHARE:
                    phare = true;
                    break;
                case PAVILLON:
                    pavillon = true;
                    break;
                case PORT:
                    if (self) {
                        port = EPort.values()[value];
                    } else {
                        otherPort = EPort.values()[value];
                    }
                    break;
                case BOUEE_PRISE:
                    boueePrise(value);
                    break;
                case DEPOSE_GRAND_PORT:
                    deposeGrandPort(ECouleurBouee.values()[value]);
                    break;
                case DEPOSE_PETIT_PORT:
                    deposePetitPort(ECouleurBouee.values()[value]);
                    break;
                case DEPOSE_GRAND_CHENAL_ROUGE:
                    deposeGrandChenalRouge(ECouleurBouee.values()[value]);
                    break;
                case DEPOSE_GRAND_CHENAL_VERT:
                    deposeGrandChenalVert(ECouleurBouee.values()[value]);
                    break;
                case DEPOSE_PETIT_CHENAL_ROUGE:
                    deposePetitChenalRouge(ECouleurBouee.values()[value]);
                    break;
                case DEPOSE_PETIT_CHENAL_VERT:
                    deposePetitChenalVert(ECouleurBouee.values()[value]);
                    break;
            }
        }
    }
}

package org.arig.robot.services;

import lombok.Getter;
import org.arig.robot.model.Bouee;
import org.arig.robot.model.ECouleurBouee;
import org.arig.robot.model.EPort;
import org.arig.robot.model.EStatusEvent;
import org.arig.robot.model.EStrategy;
import org.arig.robot.model.ETeam;
import org.arig.robot.model.EurobotStatus;
import org.arig.robot.model.GrandChenaux;
import org.arig.robot.model.Point;
import org.arig.robot.model.communication.balise.enums.EDirectionGirouette;
import org.arig.robot.system.group.IRobotGroup;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Stream;

@Service
public class RobotGroupService implements InitializingBean, IRobotGroup.Handler {

    @Autowired
    private EurobotStatus rs;

    @Autowired
    private IRobotGroup group;

    @Autowired
    private ThreadPoolExecutor threadPoolTaskExecutor;

    @Getter
    private boolean calage;

    @Getter
    private boolean ready;

    @Getter
    private boolean start;

    @Override
    public void afterPropertiesSet() {
        group.listen(this);
    }

    @Override
    public void handle(int eventOrdinal, byte[] value) {
        switch (EStatusEvent.values()[eventOrdinal]) {
            case CALAGE:
                calage = true;
                break;
            case READY:
                ready = true;
                break;
            case START:
                start = true;
                break;
            case ECHANGE_READY:
                rs.echangeReady(true);
                break;
            case TEAM:
                rs.setTeam(ETeam.values()[value[0]]);
                break;
            case CONFIG:
                rs.strategy(EStrategy.values()[value[0]]);
                rs.doubleDepose(value[1] > 0);
                rs.deposePartielle(value[2] > 0);
                rs.echangeEcueil(value[3] > 0);
                break;
            case GIROUETTE:
                rs.directionGirouette(EDirectionGirouette.values()[value[0]]);
                break;
            case HAUT_FOND_PRIS:
                rs.hautFondPris(true);
                break;
            case ECUEIL_EQUIPE_PRIS:
                rs.ecueilEquipePris(true);
                break;
            case ECUEIL_COMMUN_EQUIPE_PRIS:
                rs.ecueilCommunEquipePris(true);
                break;
            case ECUEIL_COMMUN_ADVERSE_PRIS:
                rs.ecueilCommunAdversePris(true);
                break;
            case MANCHE_AIR_1:
                rs.mancheAAir1(true);
                break;
            case MANCHE_AIR_2:
                rs.mancheAAir2(true);
                break;
            case PHARE:
                rs.phare(true);
                break;
            case PAVILLON:
                rs.pavillon(true);
                break;
            case PORT:
                rs.otherPort(EPort.values()[value[0]]);
                break;
            case BOUEE_PRISE:
                for (byte numero : value) {
                    rs.boueePrise(numero);
                }
                break;
            case DEPOSE_GRAND_PORT:
                rs.deposeGrandPort(getBouees(value));
                break;
            case DEPOSE_PETIT_PORT:
                rs.deposePetitPort(getBouees(value));
                break;
            case STEPS_PETIT_PORT:
                rs.incStepsPetitPort();
                break;
            case DEPOSE_UNITAIRE_GRAND_CHENAL_VERT:
                rs.deposeGrandChenalVert(
                        GrandChenaux.Line.values()[value[0]],
                        value[1],
                        ECouleurBouee.values()[value[2]]
                );
                break;
            case DEPOSE_UNITAIRE_GRAND_CHENAL_ROUGE:
                rs.deposeGrandChenalRouge(
                        GrandChenaux.Line.values()[value[0]],
                        value[1],
                        ECouleurBouee.values()[value[2]]
                );
                break;
            case DEPOSE_GRAND_CHENAL_ROUGE:
                rs.deposeGrandChenalRouge(GrandChenaux.Line.values()[value[0]], getBouees(value, 1));
                break;
            case DEPOSE_GRAND_CHENAL_VERT:
                rs.deposeGrandChenalVert(GrandChenaux.Line.values()[value[0]], getBouees(value, 1));
                break;
            case DEPOSE_PETIT_CHENAL_ROUGE:
                rs.deposePetitChenalRouge(getBouees(value));
                break;
            case DEPOSE_PETIT_CHENAL_VERT:
                rs.deposePetitChenalVert(getBouees(value));
                break;
            case HAUT_FOND:
                List<Bouee> hautFond = new ArrayList<>();
                for (byte i = 0; i < value[0]; i++) {
                    hautFond.add(new Bouee(0,
                            ECouleurBouee.values()[value[1 + i * 3]],
                            new Point(value[2 + i * 3] * 10, value[3 + i * 3] * 10)
                    ));
                }
                rs.hautFond(hautFond);
                break;
        }
    }

    private ECouleurBouee[] getBouees(byte[] value) {
        return getBouees(value, 0);
    }

    private ECouleurBouee[] getBouees(byte[] value, int start) {
        ECouleurBouee[] bouees = new ECouleurBouee[value.length - start];
        for (int i = start; i < value.length; i++) {
            ECouleurBouee bouee = ECouleurBouee.values()[value[i]];
            bouees[i - start] = bouee == ECouleurBouee.NULL ? null : bouee;
        }
        return bouees;
    }

    public void calage() {
        sendEvent(EStatusEvent.CALAGE);
    }

    public void ready() {
        sendEvent(EStatusEvent.READY);
    }

    public void start() {
        sendEvent(EStatusEvent.START);
    }

    public void team(ETeam team) {
        rs.setTeam(team);
        sendEvent(EStatusEvent.TEAM, team);
    }

    public void configuration() {
        byte[] data = new byte[]{
                (byte) rs.strategy().ordinal(),
                (byte) (rs.doubleDepose() ? 1 : 0),
                (byte) (rs.deposePartielle() ? 1 : 0),
                (byte) (rs.echangeEcueil() ? 1 : 0)
        };
        sendEvent(EStatusEvent.CONFIG, data);
    }

    public void echangeReady() {
        rs.echangeReady(true);
        sendEvent(EStatusEvent.ECHANGE_READY);
    }

    public void directionGirouette(EDirectionGirouette direction) {
        rs.directionGirouette(direction);
        sendEvent(EStatusEvent.GIROUETTE, direction);
    }

    public void hautFondPris() {
        rs.hautFondPris(true);
        sendEvent(EStatusEvent.HAUT_FOND_PRIS);
    }

    public void ecueilEquipePris() {
        rs.ecueilEquipePris(true);
        sendEvent(EStatusEvent.ECUEIL_EQUIPE_PRIS);
    }

    public void ecueilCommunEquipePris() {
        rs.ecueilCommunEquipePris(true);
        sendEvent(EStatusEvent.ECUEIL_COMMUN_EQUIPE_PRIS);
    }

    public void ecueilCommunAdversePris() {
        rs.ecueilCommunAdversePris(true);
        sendEvent(EStatusEvent.ECUEIL_COMMUN_ADVERSE_PRIS);
    }

    public void mancheAAir1() {
        rs.mancheAAir1(true);
        sendEvent(EStatusEvent.MANCHE_AIR_1);
    }

    public void mancheAAir2() {
        rs.mancheAAir2(true);
        sendEvent(EStatusEvent.MANCHE_AIR_2);
    }

    public void phare() {
        rs.phare(true);
        sendEvent(EStatusEvent.PHARE);
    }

    public void pavillon() {
        rs.pavillon(true);
        sendEvent(EStatusEvent.PAVILLON);
    }

    public void port(EPort port) {
        rs.port(port);
        sendEvent(EStatusEvent.PORT, port);
    }

    public void deposeGrandPort(ECouleurBouee... bouees) {
        rs.deposeGrandPort(bouees);
        sendEventBouees(EStatusEvent.DEPOSE_GRAND_PORT, bouees);
    }

    public void deposePetitPort(ECouleurBouee... bouees) {
        rs.deposePetitPort(bouees);
        sendEventBouees(EStatusEvent.DEPOSE_PETIT_PORT, bouees);
    }

    public void incStepsPetitPort() {
        rs.incStepsPetitPort();
        sendEvent(EStatusEvent.STEPS_PETIT_PORT);
    }

    public void deposeGrandChenalVert(GrandChenaux.Line line, int index, ECouleurBouee bouee) {
        if (bouee != null) {
            rs.deposeGrandChenalVert(line, index, bouee);
            byte[] data = new byte[]{(byte) line.ordinal(), (byte) index, (byte) bouee.ordinal()};
            sendEvent(EStatusEvent.DEPOSE_UNITAIRE_GRAND_CHENAL_VERT, data);
        }
    }

    public void deposeGrandChenalRouge(GrandChenaux.Line line, int index, ECouleurBouee bouee) {
        if (bouee != null) {
            rs.deposeGrandChenalRouge(line, index, bouee);
            byte[] data = new byte[]{(byte) line.ordinal(), (byte) index, (byte) bouee.ordinal()};
            sendEvent(EStatusEvent.DEPOSE_UNITAIRE_GRAND_CHENAL_ROUGE, data);
        }
    }

    public void deposeGrandChenalVert(GrandChenaux.Line line, ECouleurBouee... bouees) {
        rs.deposeGrandChenalVert(line, bouees);
        sendEventGrandChenal(EStatusEvent.DEPOSE_GRAND_CHENAL_VERT, line, bouees);
    }

    public void deposeGrandChenalRouge(GrandChenaux.Line line, ECouleurBouee... bouees) {
        rs.deposeGrandChenalRouge(line, bouees);
        sendEventGrandChenal(EStatusEvent.DEPOSE_GRAND_CHENAL_ROUGE, line, bouees);
    }

    public void deposePetitChenalVert(ECouleurBouee... bouees) {
        rs.deposePetitChenalVert(bouees);
        sendEventBouees(EStatusEvent.DEPOSE_PETIT_CHENAL_VERT, bouees);
    }

    public void deposePetitChenalRouge(ECouleurBouee... bouees) {
        rs.deposePetitChenalRouge(bouees);
        sendEventBouees(EStatusEvent.DEPOSE_PETIT_CHENAL_ROUGE, bouees);
    }

    public void boueePrise(int... numeros) {
        rs.boueePrise(numeros);
        if (numeros.length > 0) {
            byte[] data = new byte[numeros.length];
            for (int i = 0; i < numeros.length; i++) {
                data[i] = (byte) numeros[i];
            }
            sendEvent(EStatusEvent.BOUEE_PRISE, data);
        }
    }

    public void hautFond(List<Bouee> hautFond) {
        rs.hautFond(hautFond);
        byte[] data = new byte[1 + hautFond.size() * 3];
        data[0] = (byte) hautFond.size();
        for (int i = 0; i < hautFond.size(); i++) {
            data[1 + i * 3] = (byte) hautFond.get(i).couleur().ordinal();
            // on part du principe qu'il n'y a pas de bouée X > 2560 mm
            data[2 + i * 3] = (byte) Math.round(hautFond.get(i).pt().getX() / 10.0);
            data[3 + i * 3] = (byte) Math.round(hautFond.get(i).pt().getY() / 10.0);
        }
        sendEvent(EStatusEvent.HAUT_FOND, data);
    }

    private void sendEventGrandChenal(EStatusEvent event, GrandChenaux.Line line, ECouleurBouee... bouees) {
        byte[] data = new byte[1 + bouees.length];
        data[0] = (byte) line.ordinal();
        for (int i = 0; i < bouees.length; i++) {
            if (bouees[i] != null) {
                data[i + 1] = (byte) bouees[i].ordinal();
            }
        }
        sendEvent(event, data);
    }

    private void sendEventBouees(EStatusEvent event, ECouleurBouee[] bouees) {
        ECouleurBouee[] boueesFiltered = Stream.of(bouees).filter(Objects::nonNull).toArray(ECouleurBouee[]::new);

        if (boueesFiltered.length > 0) {
            byte[] data = new byte[boueesFiltered.length];
            for (int i = 0; i < boueesFiltered.length; i++) {
                data[i] = (byte) boueesFiltered[i].ordinal();
            }
            sendEvent(event, data);
        }
    }

    private void sendEvent(EStatusEvent event) {
        sendEvent(event, new byte[]{});
    }

    private <E extends Enum<E>> void sendEvent(EStatusEvent event, E value) {
        sendEvent(event, new byte[]{(byte) value.ordinal()});
    }

    private void sendEvent(EStatusEvent event, byte[] data) {
        CompletableFuture.runAsync(() -> {
            group.sendEventLog(event, data);
        }, threadPoolTaskExecutor);
    }
}

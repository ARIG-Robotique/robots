package org.arig.robot.services;

import org.arig.robot.model.Bouee;
import org.arig.robot.model.ECouleurBouee;
import org.arig.robot.model.EPort;
import org.arig.robot.model.EStatusEvent;
import org.arig.robot.model.ETeam;
import org.arig.robot.model.EurobotStatus;
import org.arig.robot.model.Point;
import org.arig.robot.model.communication.balise.enums.EDirectionGirouette;
import org.arig.robot.system.group.IRobotGroup;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

@Service
public class RobotGroupService implements InitializingBean, IRobotGroup.Handler {

    @Autowired
    private EurobotStatus rs;

    @Autowired
    private IRobotGroup group;

    @Autowired
    private ThreadPoolExecutor threadPoolTaskExecutor;

    @Override
    public void afterPropertiesSet() {
        group.listen(this);
    }

    @Override
    public void handle(int eventOrdinal, byte[] value) {
        switch (EStatusEvent.values()[eventOrdinal]) {
            case TEAM:
                rs.setTeam(ETeam.values()[value[0]]);
                break;
            case GIROUETTE:
                rs.directionGirouette(EDirectionGirouette.values()[value[0]]);
                break;
            case COULEUR_ECUEIL:
                rs.couleursEcueilCommun(getBouees(value));
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
            case DEPOSE_GRAND_CHENAL_ROUGE:
                rs.deposeGrandChenalRouge(getBouees(value));
                break;
            case DEPOSE_GRAND_CHENAL_VERT:
                rs.deposeGrandChenalVert(getBouees(value));
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
        ECouleurBouee[] bouees = new ECouleurBouee[value.length];
        for (int i = 0; i < value.length; i++) {
            bouees[i] = ECouleurBouee.values()[value[i]];
        }
        return bouees;
    }

    public void team(ETeam team) {
        rs.setTeam(team);
        sendEvent(EStatusEvent.TEAM, team);
    }

    public void directionGirouette(EDirectionGirouette direction) {
        rs.directionGirouette(direction);
        sendEvent(EStatusEvent.GIROUETTE, direction);
    }

    public void couleursEcueilCommun(ECouleurBouee[] couleurs) {
        rs.couleursEcueilCommun(couleurs);
        sendEventBouees(EStatusEvent.COULEUR_ECUEIL, couleurs);
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

    public void bonPort() {
        rs.bonPort();
        sendEvent(EStatusEvent.PORT, EPort.BON);
    }

    public void mauvaisPort() {
        rs.mauvaisPort();
        sendEvent(EStatusEvent.PORT, EPort.MAUVAIS);
    }

    public void deposeGrandPort(ECouleurBouee... bouees) {
        rs.deposeGrandPort(bouees);
        sendEventBouees(EStatusEvent.DEPOSE_GRAND_PORT, bouees);
    }

    public void deposePetitPort(ECouleurBouee... bouees) {
        rs.deposePetitPort(bouees);
        sendEventBouees(EStatusEvent.DEPOSE_PETIT_PORT, bouees);
    }

    public void deposeGrandChenalVert(ECouleurBouee... bouees) {
        rs.deposeGrandChenalVert(bouees);
        sendEventBouees(EStatusEvent.DEPOSE_GRAND_CHENAL_VERT, bouees);
    }

    public void deposeGrandChenalRouge(ECouleurBouee... bouees) {
        rs.deposeGrandChenalRouge(bouees);
        sendEventBouees(EStatusEvent.DEPOSE_GRAND_CHENAL_ROUGE, bouees);
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

    private void sendEventBouees(EStatusEvent event, ECouleurBouee... bouees) {
        if (bouees.length > 0) {
            byte[] data = new byte[bouees.length];
            for (int i = 0; i < bouees.length; i++) {
                data[i] = (byte) bouees[i].ordinal();
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

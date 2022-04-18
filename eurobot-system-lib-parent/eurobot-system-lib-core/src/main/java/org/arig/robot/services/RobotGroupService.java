package org.arig.robot.services;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.arig.robot.model.CouleurCarreFouille;
import org.arig.robot.model.CouleurEchantillon;
import org.arig.robot.model.EurobotStatus;
import org.arig.robot.model.InitStep;
import org.arig.robot.model.SiteDeRetour;
import org.arig.robot.model.StatusEvent;
import org.arig.robot.model.Strategy;
import org.arig.robot.model.Team;
import org.arig.robot.system.group.RobotGroup;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Stream;

@Slf4j
@Service
public class RobotGroupService implements RobotGroup.Handler {

    private final EurobotStatus rs;
    private final RobotGroup group;
    private final ThreadPoolExecutor threadPoolTaskExecutor;

    /**
     * Indique au secondaire de démarrer le callage
     */
    @Getter
    private boolean calage;

    /**
     * Indique au secondaire l'etat "pret" (écran vérouillé)
     */
    @Getter
    private boolean ready;

    /**
     * Indique au secondaire le début de match
     */
    @Getter
    private boolean start;

    @Getter
    private int initStep = 0;

    public RobotGroupService(final EurobotStatus rs, final RobotGroup group, final ThreadPoolExecutor threadPoolTaskExecutor) {
        this.rs = rs;
        this.group = group;
        this.threadPoolTaskExecutor = threadPoolTaskExecutor;

        group.listen(this);
    }

    @Override
    public void handle(int eventOrdinal, byte[] data) {
        StatusEvent event = StatusEvent.values()[eventOrdinal];
        log.info("[GROUP] Handle event {} : {}", event, data);

        switch (event) {
            case CALAGE:
                calage = true;
                break;
            case INIT:
                initStep = data[0];
                break;
            case READY:
                ready = true;
                break;
            case START:
                start = true;
                break;
            case TEAM:
                rs.setTeam(Team.values()[data[0]]);
                break;
            case STRATEGY:
                rs.strategy(Strategy.values()[data[0]]);
                break;
            case CONFIG:
                rs.statuettePresente(data[0] > 0);
                rs.vitrinePresente(data[1] > 0);
                break;
            case CURRENT_ACTION:
                String actionName = null;
                if (data.length > 0) {
                    actionName = new String(data, StandardCharsets.UTF_8);
                }
                rs.otherCurrentAction(actionName);
                break;

            case DISTRIBUTEUR_EQUIPE_PRIS:
                rs.distributeurEquipePris(true);
                break;
            case DISTRIBUTEUR_COMMUN_EQUIPE_PRIS:
                rs.distributeurCommunEquipePris(true);
                break;
            case DISTRIBUTEUR_COMMUN_ADVERSE_PRIS:
                rs.distributeurCommunAdversePris(true);
                break;
            case SITE_ECHANTILLON_PRIS:
                rs.siteEchantillonPris(true);
                break;
            case SITE_ECHANTILLON_ADVERSE_PRIS:
                rs.siteEchantillonAdversePris(true);
                break;
            case SITE_DE_FOUILLE_PRIS:
                rs.siteDeFouillePris(true);
                break;
            case SITE_DE_FOUILLE_ADVERSE_PRIS:
                rs.siteDeFouilleAdversePris(true);
                break;
            case VITRINE_ACTIVE:
                rs.vitrineActive(true);
                break;
            case STATUETTE_PRIS:
                rs.statuettePris(true, false);
                break;
            case STATUETTE_DANS_VITRINE:
                rs.statuetteDansVitrine(true);
                break;
            case REPLIQUE_DEPOSE:
                rs.repliqueDepose(true);
                break;
            case ECHANTILLON_ABRI_CHANTIER_DISTRIBUTEUR_PRIS:
                rs.echantillonAbriChantierDistributeurPris(true);
                break;
            case ECHANTILLON_ABRI_CHANTIER_CARRE_FOUILLE_PRIS:
                rs.echantillonAbriChantierCarreFouillePris(true);
                break;
            case ECHANTILLON_CAMPEMENT_PRIS:
                rs.echantillonCampementPris(true);
                break;
            case SITE_DE_RETOUR:
                rs.siteDeRetourAutreRobot(SiteDeRetour.values()[data[0]]);
                break;

            case COULEUR_CARRE_FOUILLE:
                rs.couleurCarreFouille(data[0], CouleurCarreFouille.values()[data[1]]);
                break;
            case BASCULE_CARRE_FOUILLE:
                rs.basculeCarreFouille(data[0]);
                break;
            case DEPOSE_CAMPEMENT_ROUGE:
                rs.deposeCampementRouge(getEchantillons(data));
                break;
            case DEPOSE_CAMPEMENT_VERT:
                rs.deposeCampementVert(getEchantillons(data));
                break;
            case DEPOSE_CAMPEMENT_BLEU:
                rs.deposeCampementBleu(getEchantillons(data));
                break;
            case DEPOSE_GALERIE_ROUGE:
                rs.deposeGalerieRouge(getEchantillons(data));
                break;
            case DEPOSE_GALERIE_ROUGE_VERT:
                rs.deposeGalerieRougeVert(getEchantillons(data));
                break;
            case DEPOSE_GALERIE_VERT:
                rs.deposeGalerieVert(getEchantillons(data));
                break;
            case DEPOSE_GALERIE_VERT_BLEU:
                rs.deposeGalerieVertBleu(getEchantillons(data));
                break;
            case DEPOSE_GALERIE_BLEU:
                rs.deposeGalerieBleu(getEchantillons(data));
                break;
            case DEPOSE_ABRI_CHANTIER:
                rs.deposeAbriChantier(getEchantillons(data));
                break;

            default:
                log.warn("Reception d'un event inconnu : " + event);
                break;
        }
    }

    @Override
    public void setCurrentAction(String name) {
        rs.currentAction(name);
        if (StringUtils.isBlank(name)) {
            sendEvent(StatusEvent.CURRENT_ACTION);
        } else {
            sendEvent(StatusEvent.CURRENT_ACTION, name.getBytes(StandardCharsets.UTF_8));
        }
    }

    /**
     * Appellé par le principal pour démarrer le callage bordure
     */
    public void calage() {
        calage = true;
        sendEvent(StatusEvent.CALAGE);
    }

    /**
     * Appellé par les deux robots pour le phasage des mouvements à l'init
     */
    public void initStep(InitStep s) {
        initStep = s.step();
        sendEvent(StatusEvent.INIT, new byte[]{(byte) initStep});
    }

    /**
     * Attends que l'autre robot ait terminé une étape d'init
     */
    public void waitInitStep(InitStep s) {
        if (!rs.twoRobots()){
            log.warn("Un seul robot, on ne peut pas attendre l'autre robot !");
            return;
        }

        do {
            ThreadUtils.sleep(200);
        } while (this.initStep != s.step());
    }

    /**
     * Appellé par le principal en fin d'init
     */
    public void ready() {
        ready = true;
        sendEvent(StatusEvent.READY);
    }

    /**
     * Appellé par le principal pour le début de match
     */
    public void start() {
        start = true;
        sendEvent(StatusEvent.START);
    }

    public void team(Team team) {
        rs.setTeam(team);
        sendEvent(StatusEvent.TEAM, team);
    }

    public void strategy(Strategy strategy) {
        rs.strategy(strategy);
        sendEvent(StatusEvent.STRATEGY, strategy);
    }

    public void configuration() {
        byte[] data = new byte[]{
                (byte) (rs.statuettePresente() ? 1 : 0),
                (byte) (rs.vitrinePresente() ? 1 : 0)
        };
        sendEvent(StatusEvent.CONFIG, data);
    }

    public void distributeurEquipePris() {
        rs.distributeurEquipePris(true);
        sendEvent(StatusEvent.DISTRIBUTEUR_EQUIPE_PRIS);
    }

    public void distributeurCommunEquipePris() {
        rs.distributeurCommunEquipePris(true);
        sendEvent(StatusEvent.DISTRIBUTEUR_COMMUN_EQUIPE_PRIS);
    }

    public void distributeurCommunAdversePris() {
        rs.distributeurCommunAdversePris(true);
        sendEvent(StatusEvent.DISTRIBUTEUR_COMMUN_ADVERSE_PRIS);
    }

    public void siteEchantillonPris() {
        rs.siteEchantillonPris(true);
        sendEvent(StatusEvent.SITE_ECHANTILLON_PRIS);
    }

    public void siteEchantillonAdversePris() {
        rs.siteEchantillonAdversePris(true);
        sendEvent(StatusEvent.SITE_ECHANTILLON_ADVERSE_PRIS);
    }

    public void siteDeFouillePris() {
        rs.siteDeFouillePris(true);
        sendEvent(StatusEvent.SITE_DE_FOUILLE_PRIS);
    }

    public void siteDeFouilleAdversePris() {
        rs.siteDeFouilleAdversePris(true);
        sendEvent(StatusEvent.SITE_DE_FOUILLE_ADVERSE_PRIS);
    }

    public void vitrineActive() {
        rs.vitrineActive(true);
        sendEvent(StatusEvent.VITRINE_ACTIVE);
    }

    public void statuettePris() {
        rs.statuettePris(true, true);
        sendEvent(StatusEvent.STATUETTE_PRIS);
    }

    public void statuetteDansVitrine() {
        rs.statuetteDansVitrine(true);
        sendEvent(StatusEvent.STATUETTE_DANS_VITRINE);
    }

    public void repliqueDepose() {
        rs.repliqueDepose(true);
        sendEvent(StatusEvent.REPLIQUE_DEPOSE);
    }

    public void echantillonAbriChantierDistributeurPris() {
        rs.echantillonAbriChantierDistributeurPris(true);
        sendEvent(StatusEvent.ECHANTILLON_ABRI_CHANTIER_DISTRIBUTEUR_PRIS);
    }

    public void echantillonAbriChantierCarreFouillePris() {
        rs.echantillonAbriChantierCarreFouillePris(true);
        sendEvent(StatusEvent.ECHANTILLON_ABRI_CHANTIER_CARRE_FOUILLE_PRIS);
    }

    public void echantillonCampementPris() {
        rs.echantillonCampementPris(true);
        sendEvent(StatusEvent.ECHANTILLON_CAMPEMENT_PRIS);
    }

    public void siteDeRetour(SiteDeRetour siteDeRetour) {
        rs.siteDeRetour(siteDeRetour);
        sendEvent(StatusEvent.SITE_DE_RETOUR, siteDeRetour);
    }

    public void basculeCarreFouille(int numero) {
        rs.basculeCarreFouille(numero);
        sendEvent(StatusEvent.BASCULE_CARRE_FOUILLE, new byte[]{(byte) numero});
    }

    public void couleurCarreFouille(int numero, CouleurCarreFouille carreFouille) {
        rs.couleurCarreFouille(numero, carreFouille);
        sendEvent(StatusEvent.COULEUR_CARRE_FOUILLE, new byte[]{(byte) numero, (byte) carreFouille.ordinal()});
    }

    public void deposeAbriChantier(CouleurEchantillon ... echantillons) {
        rs.deposeAbriChantier(echantillons);
        sendEvent(StatusEvent.DEPOSE_ABRI_CHANTIER, echantillons);
    }

    public void deposeCampementRouge(CouleurEchantillon ... echantillons) {
        rs.deposeCampementRouge(echantillons);
        sendEvent(StatusEvent.DEPOSE_CAMPEMENT_ROUGE, echantillons);
    }

    public void deposeCampementVert(CouleurEchantillon ... echantillons) {
        rs.deposeCampementBleu(echantillons);
        sendEvent(StatusEvent.DEPOSE_CAMPEMENT_VERT, echantillons);
    }

    public void deposeCampementBleu(CouleurEchantillon ... echantillons) {
        rs.deposeCampementBleu(echantillons);
        sendEvent(StatusEvent.DEPOSE_CAMPEMENT_BLEU, echantillons);
    }

    public void deposeGalerieRouge(CouleurEchantillon ... echantillons) {
        rs.deposeGalerieRouge(echantillons);
        sendEvent(StatusEvent.DEPOSE_GALERIE_ROUGE, echantillons);
    }
    public void deposeGalerieRougeVert(CouleurEchantillon ... echantillons) {
        rs.deposeGalerieRougeVert(echantillons);
        sendEvent(StatusEvent.DEPOSE_GALERIE_ROUGE_VERT, echantillons);
    }
    public void deposeGalerieVert(CouleurEchantillon ... echantillons) {
        rs.deposeGalerieVert(echantillons);
        sendEvent(StatusEvent.DEPOSE_GALERIE_VERT, echantillons);
    }
    public void deposeGalerieVertBleu(CouleurEchantillon ... echantillons) {
        rs.deposeGalerieVertBleu(echantillons);
        sendEvent(StatusEvent.DEPOSE_GALERIE_VERT_BLEU, echantillons);
    }
    public void deposeGalerieBleu(CouleurEchantillon ... echantillons) {
        rs.deposeGalerieBleu(echantillons);
        sendEvent(StatusEvent.DEPOSE_GALERIE_BLEU, echantillons);
    }

    private CouleurEchantillon[] getEchantillons(byte[] data) {
        CouleurEchantillon[] echantillons = new CouleurEchantillon[data.length];
        for (int i = 0; i < data.length; i++) {
            echantillons[i] = CouleurEchantillon.values()[data[i]];
        }
        return echantillons;
    }

    private void sendEvent(StatusEvent event) {
        sendEvent(event, new byte[]{});
    }

    @SafeVarargs
    private <E extends Enum<E>> void sendEvent(StatusEvent event, E ... data) {
        Enum[] dataBytes = Stream.of(data)
                .filter(Objects::nonNull)
                .toArray(Enum[]::new);
        if (dataBytes.length > 0) {
            byte[] values = new byte[dataBytes.length];
            for (int i = 0; i < dataBytes.length; i++) {
                values[i] = (byte) dataBytes[i].ordinal();
            }
            sendEvent(event, values);
        }
    }

    private void sendEvent(StatusEvent event, byte[] data) {
        CompletableFuture.runAsync(() -> {
            log.info("[GROUP] Send event {} : {}", event, data);
            group.sendEventLog(event, data);
        }, threadPoolTaskExecutor);
    }
}

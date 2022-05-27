package org.arig.robot.services;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.arig.robot.model.*;
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
                rs.reverseCarreDeFouille(data[0] > 0);
                rs.doubleDeposeGalerie(data[1] > 0);
                rs.priseUnitaire(data[2] > 0);
                rs.siteDeFouille(data[3] > 0);
                rs.etalonageBaliseOk(data[4] > 0);
                break;
            case CURRENT_ACTION:
                String actionName = null;
                if (data.length > 0) {
                    actionName = new String(data, StandardCharsets.UTF_8);
                }
                rs.otherCurrentAction(actionName);
                break;
            case CURRENT_POSITION:
                int x = ((data[0] & 0xff) << 8) + (data[1] & 0xff);
                int y = ((data[2] & 0xff) << 8) + (data[3] & 0xff);
                rs.otherPosition(x, y);
                break;

            case DISTRIBUTEUR_EQUIPE:
                rs.distributeurEquipe(StatutDistributeur.values()[data[0]]);
                break;
            case DISTRIBUTEUR_COMMUN_EQUIPE:
                rs.distributeurCommunEquipe(StatutDistributeur.values()[data[0]]);
                break;
            case DISTRIBUTEUR_COMMUN_ADVERSE:
                rs.distributeurCommunAdverse(StatutDistributeur.values()[data[0]]);
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
                rs.statuettePrise(true, false);
                break;
            case STATUETTE_DEPOSE:
                rs.statuetteDepose(true);
                rs.statuettePrise(true, false); // Si bug de prise, mais qu'elle est quand même la plus tardivement (vibrations, etc...)
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
            case DEPOSE_CAMPEMENT_ROUGE_VERT_NORD:
                rs.deposeCampementRougeVertNord(getEchantillons(data));
                break;
            case DEPOSE_CAMPEMENT_ROUGE_VERT_SUD:
                rs.deposeCampementRougeVertSud(getEchantillons(data));
                break;
            case DEPOSE_CAMPEMENT_BLEU_VERT_NORD:
                rs.deposeCampementBleuVertNord(getEchantillons(data));
                break;
            case DEPOSE_CAMPEMENT_BLEU_VERT_SUD:
                rs.deposeCampementBleuVertSud(getEchantillons(data));
                break;
            case CURRENT_CAMPEMENT:
                rs.otherCampement(Campement.Position.values()[data[0]]);
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
            case DEPOSE_GALERIE_BLEU_VERT:
                rs.deposeGalerieBleuVert(getEchantillons(data));
                break;
            case DEPOSE_GALERIE_BLEU:
                rs.deposeGalerieBleu(getEchantillons(data));
                break;
            case DEPOSE_ABRI_CHANTIER:
                rs.deposeAbriChantier(getEchantillons(data));
                break;
            case PERIODE_GALERIE:
                rs.periodeGalerieAutreRobot(Galerie.Periode.values()[data[0]]);
                break;
            case GALERIE_BLOQUEE:
                rs.periodeGalerieBloquee(Galerie.Periode.values()[data[0]]);
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

    @Override
    public void setCurrentPosition(int x, int y) {
        byte[] data = new byte[]{
                (byte) ((x >> 8) & 0xff),
                (byte) (x & 0xff),
                (byte) ((y >> 8) & 0xff),
                (byte) (y & 0xff)
        };
        sendEvent(StatusEvent.CURRENT_POSITION, data);
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
        if (!rs.twoRobots()) {
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
                (byte) (rs.reverseCarreDeFouille() ? 1 : 0),
                (byte) (rs.doubleDeposeGalerie() ? 1 : 0),
                (byte) (rs.priseUnitaire() ? 1 : 0),
                (byte) (rs.siteDeFouille() ? 1 : 0),
                (byte) (rs.etalonageBaliseOk() ? 1 : 0)
        };
        sendEvent(StatusEvent.CONFIG, data);
    }

    public void distributeurEquipe(StatutDistributeur distributeurEquipe) {
        rs.distributeurEquipe(distributeurEquipe);
        sendEvent(StatusEvent.DISTRIBUTEUR_EQUIPE, distributeurEquipe);
    }

    public void distributeurCommunEquipe(StatutDistributeur distributeurCommunEquipe) {
        rs.distributeurCommunEquipe(distributeurCommunEquipe);
        sendEvent(StatusEvent.DISTRIBUTEUR_COMMUN_EQUIPE, distributeurCommunEquipe);
    }

    public void distributeurCommunAdverse(StatutDistributeur distributeurCommunEquipe) {
        rs.distributeurCommunAdverse(distributeurCommunEquipe);
        sendEvent(StatusEvent.DISTRIBUTEUR_COMMUN_ADVERSE, distributeurCommunEquipe);
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
        rs.statuettePrise(true, true);
        sendEvent(StatusEvent.STATUETTE_PRIS);
    }

    public void statuetteDansVitrine() {
        rs.statuetteDepose(true);
        rs.statuettePrise(true, true); // Si bug de prise, mais qu'elle est quand même la plus tardivement (vibrations, etc...)
        sendEvent(StatusEvent.STATUETTE_DEPOSE);
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

    public void deposeAbriChantier(CouleurEchantillon... echantillons) {
        rs.deposeAbriChantier(echantillons);
        sendEvent(StatusEvent.DEPOSE_ABRI_CHANTIER, echantillons);
    }

    public void deposeCampementRougeVertNord(CouleurEchantillon... echantillons) {
        rs.deposeCampementRougeVertNord(echantillons);
        sendEvent(StatusEvent.DEPOSE_CAMPEMENT_ROUGE_VERT_NORD, echantillons);
    }

    public void deposeCampementRougeVertSud(CouleurEchantillon... echantillons) {
        rs.deposeCampementRougeVertSud(echantillons);
        sendEvent(StatusEvent.DEPOSE_CAMPEMENT_ROUGE_VERT_SUD, echantillons);
    }

    public void deposeCampementBleuVertNord(CouleurEchantillon... echantillons) {
        rs.deposeCampementBleuVertNord(echantillons);
        sendEvent(StatusEvent.DEPOSE_CAMPEMENT_BLEU_VERT_NORD, echantillons);
    }

    public void deposeCampementBleuVertSud(CouleurEchantillon... echantillons) {
        rs.deposeCampementBleuVertSud(echantillons);
        sendEvent(StatusEvent.DEPOSE_CAMPEMENT_BLEU_VERT_SUD, echantillons);
    }

    public void positionCampement(Campement.Position pos) {
        sendEvent(StatusEvent.CURRENT_CAMPEMENT, pos);
    }

    public void periodeGalerie(Galerie.Periode periode) {
        sendEvent(StatusEvent.PERIODE_GALERIE, periode);
    }

    public void deposeGalerieRouge(CouleurEchantillon... echantillons) {
        rs.deposeGalerieRouge(echantillons);
        sendEvent(StatusEvent.DEPOSE_GALERIE_ROUGE, echantillons);
    }

    public void deposeGalerieRougeVert(CouleurEchantillon... echantillons) {
        rs.deposeGalerieRougeVert(echantillons);
        sendEvent(StatusEvent.DEPOSE_GALERIE_ROUGE_VERT, echantillons);
    }

    public void deposeGalerieVert(CouleurEchantillon... echantillons) {
        rs.deposeGalerieVert(echantillons);
        sendEvent(StatusEvent.DEPOSE_GALERIE_VERT, echantillons);
    }

    public void deposeGalerieBleuVert(CouleurEchantillon... echantillons) {
        rs.deposeGalerieBleuVert(echantillons);
        sendEvent(StatusEvent.DEPOSE_GALERIE_BLEU_VERT, echantillons);
    }

    public void deposeGalerieBleu(CouleurEchantillon... echantillons) {
        rs.deposeGalerieBleu(echantillons);
        sendEvent(StatusEvent.DEPOSE_GALERIE_BLEU, echantillons);
    }

    public void periodeGalerieBloquee(Galerie.Periode periode) {
        rs.periodeGalerieBloquee(periode);
        sendEvent(StatusEvent.GALERIE_BLOQUEE, periode);
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
    private <E extends Enum<E>> void sendEvent(StatusEvent event, E... data) {
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
            log.debug("[GROUP] Send event {} : {}", event, data);
            group.sendEventLog(event, data);
        }, threadPoolTaskExecutor);
    }
}

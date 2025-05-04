package org.arig.robot.services;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.arig.robot.model.EurobotStatus;
import org.arig.robot.model.InitStep;
import org.arig.robot.model.BackstageState;
import org.arig.robot.model.StatusEvent;
import org.arig.robot.model.Strategy;
import org.arig.robot.model.Team;
import org.arig.robot.system.group.RobotGroup;
import org.arig.robot.utils.ThreadUtils;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@Slf4j
public class RobotGroupService implements RobotGroup.Handler {

    private final EurobotStatus rs;
    @Getter
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
     * Indique aux secondaires le début de match
     */
    @Getter
    private boolean start;

    @Getter
    private boolean end;

    @Getter
    private boolean quit;

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
            case END:
                end = true;
                break;
            case QUIT:
                quit = true;
                break;
            case TEAM:
                rs.team(Team.values()[data[0]]);
                break;
            case STRATEGY:
                rs.strategy(Strategy.values()[data[0]]);
                break;
            case CONFIG:
                rs.limit2Etages(data[0] > 0);
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

            case BACKSTAGE:
                rs.backstage(BackstageState.values()[data[0]]);
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
        waitInitStep(s, 60);
    }

    public void waitInitStep(InitStep s, int timeoutSecond) {
        if (!rs.twoRobots()) {
            log.warn("Un seul robot, on ne peut pas attendre l'autre robot !");
            return;
        }

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        do {
            ThreadUtils.sleep(200);
        } while (this.initStep != s.step() && stopWatch.getTime(TimeUnit.SECONDS) < timeoutSecond);
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

    /**
     * Appellé par le principal pour la fin de match
     */
    public void end() {
        end = true;
        sendEvent(StatusEvent.END);
    }

    /**
     * Appellé par le principal pour arreter le programme de manière synchronisé
     */
    public void quit() {
        quit = true;
        sendEvent(StatusEvent.QUIT);
    }

    public void team(Team team) {
        rs.team(team);
        sendEvent(StatusEvent.TEAM, team);
    }

    public void strategy(Strategy strategy) {
        rs.strategy(strategy);
        sendEvent(StatusEvent.STRATEGY, strategy);
    }

    public void configuration() {
        byte[] data = new byte[]{
                (byte) (rs.limit2Etages() ? 1 : 0)
        };
        sendEvent(StatusEvent.CONFIG, data);
    }

    /* ************************************************************************ */
    /* ****************************** ACTIONS ********************************* */
    /* ************************************************************************ */

    public void backstage(BackstageState backstageState) {
        rs.backstage(backstageState);
        sendEvent(StatusEvent.BACKSTAGE, backstageState);
    }

    /* ************************************************************************ */
    /* ****************************** BUSINESS ******************************** */
    /* ************************************************************************ */

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

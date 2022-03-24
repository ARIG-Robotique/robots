package org.arig.robot.services;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.StatusEvent;
import org.arig.robot.model.Strategy;
import org.arig.robot.model.Team;
import org.arig.robot.model.EurobotStatus;
import org.arig.robot.system.group.RobotGroup;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Service
public class RobotGroupService implements InitializingBean, RobotGroup.Handler {

    @Autowired
    private EurobotStatus rs;

    @Autowired
    private RobotGroup group;

    @Autowired
    private ThreadPoolExecutor threadPoolTaskExecutor;

    @Getter
    private boolean calage;

    @Getter
    private boolean ready;

    @Getter
    private boolean start;

    @Getter
    private int initStep = 0;

    @Override
    public void afterPropertiesSet() {
        group.listen(this);
    }

    @Override
    public void handle(int eventOrdinal, byte[] value) {
        switch (StatusEvent.values()[eventOrdinal]) {
            case CALAGE:
                calage = true;
                break;
            case INIT:
                initStep = value[0];
                break;
            case READY:
                ready = true;
                break;
            case START:
                start = true;
                break;
            case TEAM:
                rs.setTeam(Team.values()[value[0]]);
                break;
            case STRATEGY:
                rs.strategy(Strategy.values()[value[0]]);
                break;
            case CONFIG:
                rs.option1(value[0] > 0);
                rs.option2(value[1] > 0);
                break;
            case CURRENT_ACTION:
                String actionName = null;
                if (value.length > 0) {
                    actionName = new String(value, StandardCharsets.UTF_8);
                }
                rs.otherCurrentAction(actionName);
                break;
        }
    }

    @Override
    public void setCurrentAction(String name) {
        if (name == null) {
            sendEvent(StatusEvent.CURRENT_ACTION);
        } else {
            sendEvent(StatusEvent.CURRENT_ACTION, name.getBytes(StandardCharsets.UTF_8));
        }
    }

    public void calage() {
        sendEvent(StatusEvent.CALAGE);
    }

    public void initStep(int step) {
        sendEvent(StatusEvent.INIT, new byte[]{(byte) step});
    }

    public void waitInitStep(int step) {
        do {
            ThreadUtils.sleep(200);
        } while (this.initStep != step);
    }

    public void ready() {
        sendEvent(StatusEvent.READY);
    }

    public void start() {
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
                (byte) (rs.option1() ? 1 : 0),
                (byte) (rs.option2() ? 1 : 0)
        };
        sendEvent(StatusEvent.CONFIG, data);
    }

    private void sendEvent(StatusEvent event) {
        sendEvent(event, new byte[]{});
    }

    private <E extends Enum<E>> void sendEvent(StatusEvent event, E value) {
        sendEvent(event, new byte[]{(byte) value.ordinal()});
    }

    private void sendEvent(StatusEvent event, byte[] data) {
        CompletableFuture.runAsync(() -> {
            group.sendEventLog(event, data);
        }, threadPoolTaskExecutor);
    }
}

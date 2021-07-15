package org.arig.robot.services;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.EStatusEvent;
import org.arig.robot.model.EStrategy;
import org.arig.robot.model.ETeam;
import org.arig.robot.model.EurobotStatus;
import org.arig.robot.system.group.IRobotGroup;
import org.arig.robot.utils.ThreadUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
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

    @Getter
    private int initStep = 0;

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
                rs.setTeam(ETeam.values()[value[0]]);
                break;
            case STRATEGY:
                rs.strategy(EStrategy.values()[value[0]]);
                break;
            case CONFIG:
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
            sendEvent(EStatusEvent.CURRENT_ACTION);
        } else {
            sendEvent(EStatusEvent.CURRENT_ACTION, name.getBytes(StandardCharsets.UTF_8));
        }
    }

    public void calage() {
        sendEvent(EStatusEvent.CALAGE);
    }

    public void initStep(int step) {
        sendEvent(EStatusEvent.INIT, new byte[]{(byte) step});
    }

    public void waitInitStep(int step) {
        do {
            ThreadUtils.sleep(200);
        } while (this.initStep != step);
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

    public void strategy(EStrategy strategy) {
        rs.strategy(strategy);
        sendEvent(EStatusEvent.STRATEGY, strategy);
    }

    public void configuration() {
        byte[] data = new byte[]{};
        sendEvent(EStatusEvent.CONFIG, data);
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

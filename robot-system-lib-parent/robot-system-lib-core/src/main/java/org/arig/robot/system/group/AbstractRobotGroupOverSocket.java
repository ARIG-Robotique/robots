package org.arig.robot.system.group;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.communication.socket.AbstractQuery;
import org.arig.robot.communication.socket.AbstractResponse;
import org.arig.robot.communication.socket.group.EventLogQuery;
import org.arig.robot.communication.socket.group.enums.GroupAction;
import org.arig.robot.model.AbstractRobotStatus;
import org.arig.robot.system.communication.AbstractBidirectionalSocket;

import java.util.concurrent.Executor;

@Slf4j
public abstract class AbstractRobotGroupOverSocket extends AbstractBidirectionalSocket<GroupAction> implements RobotGroup {

    private Handler handler = null;

    public AbstractRobotGroupOverSocket(int serverPort, String otherHost, int otherPort, Executor executor) {
        super(serverPort, otherHost, otherPort, 2000, executor);
    }

    @Override
    public void listen(Handler handler) {
        this.handler = handler;
    }

    protected abstract boolean groupOk();

    @Override
    protected Class<GroupAction> getActionEnum() {
        return GroupAction.class;
    }

    @Override
    protected Class<? extends AbstractQuery<GroupAction>> getQueryClass(GroupAction action) {
        switch (action) {
            case EVENT_LOG:
                return EventLogQuery.class;
            default:
                return null;
        }
    }

    @Override
    protected AbstractResponse<GroupAction> handleQuery(AbstractQuery<GroupAction> query) {
        switch (query.getAction()) {
            case EVENT_LOG:
                if (handler != null) {
                    int eventOrdinal = ((EventLogQuery) query).getEventOrdinal();
                    byte[] value = ((EventLogQuery) query).getValue();
                    handler.handle(eventOrdinal, value);
                }
                return null;
            default:
                return null;
        }
    }

    @Override
    public void setCurrentAction(String name) {
        if (handler != null) {
            handler.setCurrentAction(name);
        }
    }

    @Override
    public void setCurrentPosition(int x, int y) {
        if (handler != null) {
            handler.setCurrentPosition(x, y);
        }
    }

    @Override
    public synchronized <E extends Enum<E>> void sendEventLog(E event, byte[] value) {
        if (!groupOk()) {
            return;
        }

        try {
            sendToSocketAndGet(EventLogQuery.build(event, value), null);
        } catch (Exception e) {
            log.warn("Impossible d'échanger l'event log");
        }
    }
}

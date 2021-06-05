package org.arig.robot.system.group;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.communication.socket.AbstractQuery;
import org.arig.robot.communication.socket.AbstractResponse;
import org.arig.robot.communication.socket.group.CurrentActionQuery;
import org.arig.robot.communication.socket.group.CurrentActionResponse;
import org.arig.robot.communication.socket.group.EventLogQuery;
import org.arig.robot.communication.socket.group.enums.GroupAction;
import org.arig.robot.model.AbstractRobotStatus;
import org.arig.robot.system.communication.AbstractBidirectionalSocket;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

@Slf4j
public class AbstractRobotGroupOverSocket extends AbstractBidirectionalSocket<GroupAction> implements IRobotGroup {

    @Autowired
    private AbstractRobotStatus rs;

    private final List<Handler> handlers = new ArrayList<>();

    public AbstractRobotGroupOverSocket(int serverPort, String otherHost, int otherPort, Executor executor) {
        super(serverPort, otherHost, otherPort, 2000, executor);
    }

    @Override
    public void listen(Handler handler) {
        this.handlers.add(handler);
    }

    @Override
    protected Class<GroupAction> getActionEnum() {
        return GroupAction.class;
    }

    @Override
    protected Class<? extends AbstractQuery<GroupAction>> getQueryClass(GroupAction action) {
        switch (action) {
            case CURRENT_ACTION:
                return CurrentActionQuery.class;
            case EVENT_LOG:
                return EventLogQuery.class;
            default:
                return null;
        }
    }

    @Override
    protected AbstractResponse<GroupAction> handleQuery(AbstractQuery<GroupAction> query) {
        switch (query.getAction()) {
            case CURRENT_ACTION:
                return new CurrentActionResponse(rs.currentAction());
            case EVENT_LOG:
                int eventOrdinal = ((EventLogQuery) query).getEventOrdinal();
                byte[] value = ((EventLogQuery) query).getValue();
                handlers.forEach(h -> h.handle(eventOrdinal, value));
                return null;
            default:
                return null;
        }
    }

    @Override
    public synchronized String getCurrentAction() {
        if (!rs.groupOk()) {
            return null;
        }

        try {
            CurrentActionResponse response = sendToSocketAndGet(new CurrentActionQuery(), CurrentActionResponse.class);
            return response.getData();
        } catch (Exception e) {
            log.warn("Impossible de récupérer l'action courante");
            return null;
        }
    }

    @Override
    public synchronized <E extends Enum<E>> void sendEventLog(E event, byte[] value) {
        if (!rs.groupOk()) {
            return;
        }

        try {
            sendToSocketAndGet(EventLogQuery.build(event, value), null);
        } catch (Exception e) {
            log.warn("Impossible d'échanger l'event log");
        }
    }

}

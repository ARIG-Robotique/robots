package org.arig.robot.system.communication;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.communication.socket.AbstractQuery;
import org.arig.robot.communication.socket.AbstractResponse;

import java.io.IOException;
import java.util.concurrent.Executor;

/**
 * Wrapper pour communication full-duplex avec deux sockets
 *
 * <p>
 * A: appeller `openSocket`, ouvre le serveur A
 * B: appeller `openSocket`, ouvre le serveur B
 * A: appeller `tryConnect`, se connecte au serveur B
 * B: appeller `tryConnect`, se connecte au serveur A
 * </p>
 */
@Slf4j
public abstract class AbstractBidirectionalSocket<T extends Enum<T>> extends AbstractSocketServer<T> {

    private final AbstractSocketClient<T> client;

    public AbstractBidirectionalSocket(final int serverPort, final String hostname, final int port, final int timeout, final Executor executor) {
        super(serverPort, executor);

        client = new AbstractSocketClient<T>(hostname, port, timeout) {
        };
    }

    @Override
    public boolean isOpen() {
        return super.isOpen() && client.isOpen();
    }

    public boolean tryConnect() {
        try {
            client.openSocket();
            return true;
        } catch (IOException e) {
            client.end(true);
            return false;
        }
    }

    @Override
    public void end() {
        client.end();
        super.end();
    }

    protected <Q extends AbstractQuery<T>, R extends AbstractResponse<T>> R sendToSocketAndGet(Q query, Class<R> responseClazz) throws IOException {
        client.openIfNecessary();
        return client.sendToSocketAndGet(query, responseClazz);
    }

}

package org.arig.robot.system.group;

public interface IRobotGroup {

    interface Handler {
        void handle(int eventOrdinal, byte[] value);
    }

    boolean isOpen();

    boolean tryConnect();

    String getCurrentAction();

    <E extends Enum<E>> void sendEventLog(E event, byte[] data);

    void listen(Handler handler);
}

package org.arig.robot.system.group;

public interface IRobotGroup {

    interface Handler {
        void handle(int eventOrdinal, byte[] value);

        void setCurrentAction(String name);
    }

    boolean isOpen();

    boolean tryConnect();

    void setCurrentAction(String name);

    <E extends Enum<E>> void sendEventLog(E event, byte[] data);

    void listen(Handler handler);
}

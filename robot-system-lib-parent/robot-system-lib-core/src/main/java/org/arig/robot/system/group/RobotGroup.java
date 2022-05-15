package org.arig.robot.system.group;

public interface RobotGroup {

    interface Handler {
        void handle(int eventOrdinal, byte[] value);

        void setCurrentAction(String name);

        void setCurrentPosition(int x, int y);
    }

    boolean isOpen();

    boolean tryConnect();

    void setCurrentAction(String name);

    void setCurrentPosition(int x, int y);

    <E extends Enum<E>> void sendEventLog(E event, byte[] data);

    void listen(Handler handler);
}

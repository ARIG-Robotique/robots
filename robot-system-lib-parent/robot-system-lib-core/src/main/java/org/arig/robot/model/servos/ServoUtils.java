package org.arig.robot.model.servos;

public class ServoUtils {

    public static ServoGroup groupImportant() {
        return ServoGroup.builder()
                .name("Important")
                .order(1)
                .build();
    }

    public static ServoGroup groupDerier() {
        return ServoGroup.builder()
                .name("Derière")
                .order(2)
                .build();
    }

    public static ServoGroup groupDivers() {
        return ServoGroup.builder()
                .name("Divers")
                .order(3)
                .build();
    }
}

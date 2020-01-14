package org.arig.robot.model.servos;

public class ServoUtils {

    public static ServoGroup groupImportant() {
        return ServoGroup.builder()
                .name("Tout")
                .order(1)
                .build();
    }

}

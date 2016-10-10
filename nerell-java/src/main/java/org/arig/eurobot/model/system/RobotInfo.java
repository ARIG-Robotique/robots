package org.arig.eurobot.model.system;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by gdepuille on 29/04/15.
 */
@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RobotInfo {

    private static final RobotInfo INSTANCE = new RobotInfo();

    private final HardwareInfo hardware = new HardwareInfo();
    private final MemoryInfo memory = new MemoryInfo();
    private final OperatingSystemInfo operatingSystem = new OperatingSystemInfo();
    private final JavaInfo java = new JavaInfo();
    private final NetworkInfo network = new NetworkInfo();
    private final CodecInfo codec = new CodecInfo();
    private final ClockInfo clock = new ClockInfo();

    public static final RobotInfo getInstance() { return INSTANCE; }

}

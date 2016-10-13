package org.arig.robot.model.system;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.pi4j.system.SystemInfo;

import java.io.IOException;

/**
 * @author gdepuille on 29/04/15.
 */
public class MemoryInfo {

    @JsonGetter
    public long getTotal() throws IOException, InterruptedException {
        return SystemInfo.getMemoryTotal();
    }

    @JsonGetter
    public long getUsed() throws IOException, InterruptedException {
        return SystemInfo.getMemoryUsed();
    }

    @JsonGetter
    public long getFree() throws IOException, InterruptedException {
        return SystemInfo.getMemoryFree();
    }

    @JsonGetter
    public long getShared() throws IOException, InterruptedException {
        return SystemInfo.getMemoryShared();
    }

    @JsonGetter
    public long getBuffers() throws IOException, InterruptedException {
        return SystemInfo.getMemoryBuffers();
    }

    @JsonGetter
    public long getCached() throws IOException, InterruptedException {
        return SystemInfo.getMemoryCached();
    }

    @JsonGetter
    public float getVoltageSDRAM_CV() throws IOException, InterruptedException {
        return SystemInfo.getMemoryVoltageSDRam_C();
    }

    @JsonGetter
    public float getVoltageSDRAM_I() throws IOException, InterruptedException {
        return SystemInfo.getMemoryVoltageSDRam_I();
    }

    @JsonGetter
    public float getVoltageSDRAM_P() throws IOException, InterruptedException {
        return SystemInfo.getMemoryVoltageSDRam_P();
    }
}

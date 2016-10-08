package org.arig.eurobot.model.system;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.pi4j.system.SystemInfo;

import java.io.IOException;

/**
 * Created by gdepuille on 29/04/15.
 */

public class HardwareInfo {

    @JsonGetter
    public String getSerialNumber() throws IOException, InterruptedException {
        return SystemInfo.getSerial();
    }

    @JsonGetter
    public String getCPURevision() throws IOException, InterruptedException {
        return SystemInfo.getCpuRevision();
    }

    @JsonGetter
    public String getCPUArchi() throws IOException, InterruptedException {
        return SystemInfo.getCpuArchitecture();
    }

    @JsonGetter
    public String getCPUPart() throws IOException, InterruptedException {
        return SystemInfo.getCpuPart();
    }

    @JsonGetter
    public float getCPUTemp() throws IOException, InterruptedException {
        return SystemInfo.getCpuTemperature();
    }

    @JsonGetter
    public float getCPUVolt() throws IOException, InterruptedException {
        return SystemInfo.getCpuVoltage();
    }

    @JsonGetter
    public String getModelName() throws IOException, InterruptedException {
        return SystemInfo.getModelName();
    }

    @JsonGetter
    public String getProcessor() throws IOException, InterruptedException {
        return SystemInfo.getProcessor();
    }

    @JsonGetter
    public String getHardwareRevision() throws IOException, InterruptedException {
        return SystemInfo.getRevision();
    }

    @JsonGetter
    public String getHardware() throws IOException, InterruptedException {
        return SystemInfo.getHardware();
    }

    @JsonGetter
    public boolean isHardFloatABI() {
        return SystemInfo.isHardFloatAbi();
    }

    @JsonGetter
    public SystemInfo.BoardType getBoard() throws IOException, InterruptedException {
        return SystemInfo.getBoardType();
    }
}

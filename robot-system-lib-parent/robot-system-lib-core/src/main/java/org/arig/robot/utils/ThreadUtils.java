package org.arig.robot.utils;

import java.util.function.Supplier;

public class ThreadUtils {

    public static void sleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
        }
    }

    public static boolean waitUntil(Supplier<Boolean> pass, int intervalMs, int timeoutMs) {
        int remainingTime = timeoutMs;
        while (!pass.get() && remainingTime > 0) {
            ThreadUtils.sleep(intervalMs);
            remainingTime -= intervalMs;
        }
        return pass.get();
    }

}

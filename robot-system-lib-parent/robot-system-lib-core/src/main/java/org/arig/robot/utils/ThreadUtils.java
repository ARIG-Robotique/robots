package org.arig.robot.utils;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public class ThreadUtils {

    public static void sleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
        }
    }

    public static boolean waitUntil(BooleanSupplier pass, int intervalMs, int timeoutMs) {
        long startTime = System.currentTimeMillis();
        while (!pass.getAsBoolean() && System.currentTimeMillis() < startTime + timeoutMs) {
            ThreadUtils.sleep(intervalMs);
        }
        return pass.getAsBoolean();
    }

    public static <T> T waitUntil(Supplier<T> supplier, T invalidValue, int intervalMs, int timeoutMs) {
        long startTime = System.currentTimeMillis();
        T value;
        do {
            value = supplier.get();
            if (value != invalidValue) {
                break;
            }
            ThreadUtils.sleep(intervalMs);
        } while (System.currentTimeMillis() < startTime + timeoutMs);
        return value;
    }
}

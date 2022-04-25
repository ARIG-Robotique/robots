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
        int remainingTime = timeoutMs;
        while (!pass.getAsBoolean() && remainingTime > 0) {
            ThreadUtils.sleep(intervalMs);
            remainingTime -= intervalMs;
        }
        return pass.getAsBoolean();
    }

    public static <T> T waitUntil(Supplier<T> supplier, T invalidValue, int intervalMs, int timeoutMs) {
        int remainingTime = timeoutMs;
        T value = supplier.get();
        while (value == invalidValue && remainingTime > 0) {
            ThreadUtils.sleep(intervalMs);
            value = supplier.get();
            remainingTime -= intervalMs;
        }
        return value;
    }
}

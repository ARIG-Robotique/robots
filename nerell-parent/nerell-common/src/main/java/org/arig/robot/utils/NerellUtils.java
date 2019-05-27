package org.arig.robot.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.arig.robot.constants.IConstantesNerellConfig;
import org.arig.robot.model.ESide;
import org.arig.robot.model.Point;

import java.util.concurrent.CompletableFuture;

public enum NerellUtils {
    ;

    /**
     * Retourne l'angle à utiliser dans {@link org.arig.robot.system.TrajectoryManager#alignFrontToAvecDecalage(double, double, double)}
     * pour qu'on point tombe dans une pince
     */
    public static double getAngleDecallagePince(Point from, Point to, ESide side) {
        double distance = from.distance(to);

        double angle = Math.toDegrees(Math.asin(IConstantesNerellConfig.dstAtomeCentre / distance));

        return ESide.DROITE.equals(side) ? angle : -angle;
    }

    /**
     * Wrapper autour de CompletavleFuture#allOf pour récupérer le resultat
     */
    public static <A, B> CompletableFuture<CompoundFutureResult2<A, B>> all(CompletableFuture<A> a, CompletableFuture<B> b) {
        return CompletableFuture.allOf(a, b)
                .thenCompose((Void) ->
                        CompletableFuture.completedFuture(new CompoundFutureResult2<>(a.join(), b.join()))
                );
    }

    /**
     * Wrapper autour de CompletavleFuture#allOf pour récupérer le resultat
     */
    public static <A, B, C> CompletableFuture<CompoundFutureResult3<A, B, C>> all(CompletableFuture<A> a, CompletableFuture<B> b, CompletableFuture<C> c) {
        return CompletableFuture.allOf(a, b, c)
                .thenCompose((Void) ->
                        CompletableFuture.completedFuture(new CompoundFutureResult3<>(a.join(), b.join(), c.join()))
                );
    }

    @Getter
    @RequiredArgsConstructor
    public static class CompoundFutureResult2<A, B> {
        private final A a;
        private final B b;
    }

    @Getter
    @RequiredArgsConstructor
    public static class CompoundFutureResult3<A, B, C> {
        private final A a;
        private final B b;
        private final C c;
    }
}

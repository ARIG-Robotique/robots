package org.arig.robot.system;

public interface ICarouselManager {
    void init();

    void resetEncodeur();

    void stop();

    void process();

    void tourneIndex(int index);

    void tourne(long pulse);

    void setVitesse(long vitesse);

    void waitMouvement();

    boolean isPositionAtteint();
}

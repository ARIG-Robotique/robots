package org.arig.robot.system;

import org.arig.robot.model.enums.CouleurPalet;

import java.util.List;

public interface ICarouselManager {

    int VENTOUSE_GAUCHE = 0;
    int VENTOUSE_DROITE = 1;
    int LECTEUR = 2;
    int MAGASIN_DROIT = 3;
    int MAGASIN_GAUCHE = 4;

    void init();

    void resetEncodeur();

    void rawMotorSpeed(int speed);

    void stop();

    void process();

    void tourneIndex(int index);

    void tourne(long pulse);

    void setVitesse(long vitesse);

    void waitMouvement();

    boolean isPositionAtteint();

    boolean isFree(int index);

    CouleurPalet get(int index);

    boolean has(CouleurPalet couleur);

    long count(CouleurPalet couleur);

    int firstIndexOf(CouleurPalet couleur, int ref);

    void setColor(int index, CouleurPalet couleur);

    boolean store(int index, CouleurPalet palet);

    void unstore(int index);

    List<CouleurPalet> getAll();

    void vidange();
}

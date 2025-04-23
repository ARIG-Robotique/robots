package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.servos.Servo;
import org.arig.robot.system.servos.i2c.SD21Servos;

@Slf4j
public class NerellRobotServosService extends AbstractCommonRobotServosService {

    public static final byte TIROIR_AVANT_ID = 1;
    public static final byte BEC_AVANT_ID = 2;
    public static final byte ASCENSEUR_AVANT_ID = 3;
    public static final byte PINCE_AVANT_GAUCHE_ID = 4;
    public static final byte DOIGT_AVANT_GAUCHE_ID = 5;
    public static final byte PINCE_AVANT_DROIT_ID = 6;
    public static final byte DOIGT_AVANT_DROIT_ID = 7;
    public static final byte BLOCK_COLONNE_AVANT_GAUCHE_ID = 8;
    public static final byte BLOCK_COLONNE_AVANT_DROIT_ID = 9;

    public static final byte TIROIR_ARRIERE_ID = 10;
    public static final byte BEC_ARRIERE_ID = 11;
    public static final byte ASCENSEUR_ARRIERE_ID = 12;
    public static final byte PINCE_ARRIERE_GAUCHE_ID = 13;
    public static final byte DOIGT_ARRIERE_GAUCHE_ID = 14;
    public static final byte PINCE_ARRIERE_DROIT_ID = 15;
    public static final byte DOIGT_ARRIERE_DROIT_ID = 16;
    public static final byte BLOCK_COLONNE_ARRIERE_GAUCHE_ID = 17;
    public static final byte BLOCK_COLONNE_ARRIERE_DROIT_ID = 18;

    public NerellRobotServosService(SD21Servos servos) {
        super(servos);
    }
}

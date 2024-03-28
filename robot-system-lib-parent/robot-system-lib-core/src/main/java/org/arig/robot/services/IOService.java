package org.arig.robot.services;

public interface IOService {

    // --------------------------------------------------------- //
    // --------------------- INFOS TECHNIQUE ------------------- //
    // --------------------------------------------------------- //

    void refreshAllIO();
    boolean auOk();
    boolean puissanceServosOk();
    boolean puissanceMoteursOk();
    boolean tirette();

    // --------------------------------------------------------- //
    // -------------------------- INPUT ------------------------ //
    // --------------------------------------------------------- //

    // Numerique
    boolean in1_1();
    boolean in1_2();
    boolean in1_3();
    boolean in1_4();
    boolean in1_5();
    boolean in1_6();
    boolean in1_7();
    boolean in1_8();
    boolean in2_1();
    boolean in2_2();
    boolean in2_3();
    boolean in2_4();
    boolean in2_5();
    boolean in2_6();
    boolean in2_7();
    boolean in2_8();
    boolean in3_1();
    boolean in3_2();
    boolean in3_3();
    boolean in3_4();
    boolean in3_5();
    boolean in3_6();
    boolean in3_7();
    boolean in3_8();

    // --------------------------------------------------------- //
    // -------------------------- OUTPUT ----------------------- //
    // --------------------------------------------------------- //

    void enableAlimServos();
    void disableAlimServos();
    void enableAlimMoteurs();
    void disableAlimMoteurs();

}

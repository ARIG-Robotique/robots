package org.arig.robot.model;

public enum ETeam {
    UNKNOWN, JAUNE, BLEU;

    public String pathfinderMap() {
        return String.format("classpath:maps/sail_the_world-%s-nochenal.png", name());
    }
}

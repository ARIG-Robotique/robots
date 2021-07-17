package org.arig.robot.model;

public enum ETeam {
    JAUNE, BLEU;

    public String pathfinderMap(String robot) {
        return String.format("classpath:maps/%s/sail_the_world-%s-nochenal.png", robot, name());
    }
}

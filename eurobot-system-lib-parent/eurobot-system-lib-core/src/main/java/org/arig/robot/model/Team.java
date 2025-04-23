package org.arig.robot.model;

public enum Team {
    JAUNE, BLEU;

    public String pathfinderMap(String robot) {
        return String.format("classpath:maps/%s-%s.png", name(), robot);
    }
}

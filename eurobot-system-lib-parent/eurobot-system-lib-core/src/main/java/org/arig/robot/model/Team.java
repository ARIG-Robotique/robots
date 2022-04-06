package org.arig.robot.model;

public enum Team {
    JAUNE, VIOLET;

    public String pathfinderMap(String robot) {
        return String.format("classpath:maps/age_of_bots-%s-%s.png", name(), robot);
    }
}

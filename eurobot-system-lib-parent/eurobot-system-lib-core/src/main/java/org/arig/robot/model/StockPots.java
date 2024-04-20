package org.arig.robot.model;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class StockPots extends Point {

    public enum ID {
        BLEU_NORD,
        BLEU_MILIEU,
        BLEU_SUD,
        JAUNE_NORD,
        JAUNE_MILIEU,
        JAUNE_SUD
    }

    private final ID id;
    private final double entryAngle;
    private boolean present = true;

    public StockPots(ID id, int x, int y, double a) {
        super(x, y);
        this.id = id;
        this.entryAngle = a;
    }

    public void pris() {
        log.info("[RS] Stock pot {} pris", id);
        present = false;
    }

    public void absent() {
        log.info("[RS] Stock pot {} absent", id);
        present = false;
    }

}

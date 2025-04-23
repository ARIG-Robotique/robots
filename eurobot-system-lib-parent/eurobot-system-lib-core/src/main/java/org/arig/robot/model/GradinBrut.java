package org.arig.robot.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Accessors(fluent = true)
public class GradinBrut extends Point {

    @Getter
    @Accessors(fluent = true)
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public enum ID {
        JAUNE_RESERVE,
        JAUNE_HAUT_GAUCHE,
        JAUNE_MILIEU_CENTRE,
        JAUNE_BAS_GAUCHE,
        JAUNE_BAS_CENTRE,

        BLEU_RESERVE,
        BLEU_HAUT_DROITE,
        BLEU_MILIEU_CENTRE,
        BLEU_BAS_DROITE,
        BLEU_BAS_CENTRE
    }

    private final ID id;
    private final boolean bordure;
    private boolean present = true;
    private boolean bloque = false;

    public GradinBrut(ID id, int x, int y, boolean bordure) {
        super(x, y);
        this.id = id;
        this.bordure = bordure;
    }

    public void pris() {
        log.info("[RS] Raw tribune {} prise", id);
        present = false;
    }

    public void enableBloqking() {
        log.warn("[RS] Raw tribune {} bloqué", id);
        bloque = true;
    }
}

package org.arig.robot.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CouleurEchantillon {
    ROUGE(false, false),
    BLEU(false, false),
    VERT(false, false),
    ROCHER(false, true),
    ROCHER_ROUGE(false, true),
    ROCHER_BLEU(false, true),
    ROCHER_VERT(false, true),
    INCONNU(true, false);

    private final boolean needsLecture;
    private final boolean needsEchange;

    public CouleurEchantillon getReverseColor() {
        switch (this) {
            case ROUGE:
                return ROCHER_ROUGE;
            case BLEU:
                return ROCHER_BLEU;
            case VERT:
                return ROCHER_VERT;
            case ROCHER:
                return INCONNU;
            case ROCHER_ROUGE:
                return ROUGE;
            case ROCHER_BLEU:
                return BLEU;
            case ROCHER_VERT:
                return VERT;
            default:
                return INCONNU;
        }
    }

}

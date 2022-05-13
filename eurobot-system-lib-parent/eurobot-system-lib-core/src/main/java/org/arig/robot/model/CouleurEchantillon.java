package org.arig.robot.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.concurrent.atomic.AtomicReference;

@Getter
@AllArgsConstructor
public enum CouleurEchantillon {
    ROUGE(false, false),
    BLEU(false, false),
    VERT(false, false),
    ROCHER(false, false),
    ROCHER_ROUGE(false, false),
    ROCHER_BLEU(false, false),
    ROCHER_VERT(false, false),
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

    public static class Atomic extends AtomicReference<CouleurEchantillon> {
        public Atomic(CouleurEchantillon c) {
            super(c);
        }

        public boolean isNeedsLecture() {
            return get().isNeedsLecture();
        }

        public boolean isNeedsEchange() {
            return get().isNeedsEchange();
        }

        public boolean equals(CouleurEchantillon c) {
            return c == get();
        }

        public void reverseColor() {
            set(get().getReverseColor());
        }
    }

}

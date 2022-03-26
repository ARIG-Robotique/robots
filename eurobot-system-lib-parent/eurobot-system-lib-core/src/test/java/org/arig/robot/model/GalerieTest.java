package org.arig.robot.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class GalerieTest {

    private Galerie galerie;

    @Test
    void testScoreEmpty() {
        galerie = new Galerie();
        Assertions.assertEquals(0, galerie.score());
    }

    @Test
    void testRouge() {
        galerie = new Galerie();

        galerie.addRouge(CouleurEchantillon.ROUGE);
        Assertions.assertEquals(6, galerie.score());

        galerie.addRouge(CouleurEchantillon.VERT);
        Assertions.assertEquals(9, galerie.score());

        galerie.addRouge(CouleurEchantillon.BLEU);
        Assertions.assertEquals(12, galerie.score());

        galerie.addRouge(CouleurEchantillon.ROCHER);
        Assertions.assertEquals(15, galerie.score());
    }

    @Test
    void testRougeVert() {
        galerie = new Galerie();

        galerie.addRougeVert(CouleurEchantillon.ROUGE);
        Assertions.assertEquals(6, galerie.score());

        galerie.addRougeVert(CouleurEchantillon.VERT);
        Assertions.assertEquals(12, galerie.score());

        galerie.addRougeVert(CouleurEchantillon.BLEU);
        Assertions.assertEquals(15, galerie.score());

        galerie.addRougeVert(CouleurEchantillon.ROCHER);
        Assertions.assertEquals(18, galerie.score());
    }

    @Test
    void testVert() {
        galerie = new Galerie();

        galerie.addVert(CouleurEchantillon.ROUGE);
        Assertions.assertEquals(3, galerie.score());

        galerie.addVert(CouleurEchantillon.VERT);
        Assertions.assertEquals(9, galerie.score());

        galerie.addVert(CouleurEchantillon.BLEU);
        Assertions.assertEquals(12, galerie.score());

        galerie.addVert(CouleurEchantillon.ROCHER);
        Assertions.assertEquals(15, galerie.score());
    }

    @Test
    void testVertBleu() {
        galerie = new Galerie();

        galerie.addVertBleu(CouleurEchantillon.ROUGE);
        Assertions.assertEquals(3, galerie.score());

        galerie.addVertBleu(CouleurEchantillon.VERT);
        Assertions.assertEquals(9, galerie.score());

        galerie.addVertBleu(CouleurEchantillon.BLEU);
        Assertions.assertEquals(15, galerie.score());

        galerie.addVertBleu(CouleurEchantillon.ROCHER);
        Assertions.assertEquals(18, galerie.score());
    }

    @Test
    void testBleu() {
        galerie = new Galerie();

        galerie.addBleu(CouleurEchantillon.ROUGE);
        Assertions.assertEquals(3, galerie.score());

        galerie.addBleu(CouleurEchantillon.VERT);
        Assertions.assertEquals(6, galerie.score());

        galerie.addBleu(CouleurEchantillon.BLEU);
        Assertions.assertEquals(12, galerie.score());

        galerie.addBleu(CouleurEchantillon.ROCHER);
        Assertions.assertEquals(15, galerie.score());
    }
}

package org.arig.robot.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class CampementTest {

    private Campement campement;

    @Test
    void testScoreEmpty() {
        campement = new Campement();
        Assertions.assertEquals(0, campement.score());
    }

    @Test
    void testRouge() {
        campement = new Campement();

        campement.addRouge(CouleurEchantillon.ROUGE);
        Assertions.assertEquals(2, campement.score());

        campement.addRouge(CouleurEchantillon.VERT);
        Assertions.assertEquals(3, campement.score());

        campement.addRouge(CouleurEchantillon.BLEU);
        Assertions.assertEquals(4, campement.score());

        campement.addRouge(CouleurEchantillon.ROCHER);
        Assertions.assertEquals(5, campement.score());
    }

    @Test
    void testVert() {
        campement = new Campement();

        campement.addVert(CouleurEchantillon.ROUGE);
        Assertions.assertEquals(1, campement.score());

        campement.addVert(CouleurEchantillon.VERT);
        Assertions.assertEquals(3, campement.score());

        campement.addVert(CouleurEchantillon.BLEU);
        Assertions.assertEquals(4, campement.score());

        campement.addVert(CouleurEchantillon.ROCHER);
        Assertions.assertEquals(5, campement.score());
    }

    @Test
    void testBleu() {
        campement = new Campement();

        campement.addBleu(CouleurEchantillon.ROUGE);
        Assertions.assertEquals(1, campement.score());

        campement.addBleu(CouleurEchantillon.VERT);
        Assertions.assertEquals(2, campement.score());

        campement.addBleu(CouleurEchantillon.BLEU);
        Assertions.assertEquals(4, campement.score());

        campement.addBleu(CouleurEchantillon.ROCHER);
        Assertions.assertEquals(5, campement.score());
    }
}

package org.arig.robot.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GalerieTest {

    private Galerie galerie;

    @BeforeEach
    void setUp() {
        galerie = new Galerie();
    }

    @Test
    void testScoreEmpty() {
        Assertions.assertEquals(0, galerie.score());
    }

    @Test
    void testRouge() {
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
        galerie.addBleuVert(CouleurEchantillon.ROUGE);
        Assertions.assertEquals(3, galerie.score());

        galerie.addBleuVert(CouleurEchantillon.VERT);
        Assertions.assertEquals(9, galerie.score());

        galerie.addBleuVert(CouleurEchantillon.BLEU);
        Assertions.assertEquals(15, galerie.score());

        galerie.addBleuVert(CouleurEchantillon.ROCHER);
        Assertions.assertEquals(18, galerie.score());
    }

    @Test
    void testBleu() {
        galerie.addBleu(CouleurEchantillon.ROUGE);
        Assertions.assertEquals(3, galerie.score());

        galerie.addBleu(CouleurEchantillon.VERT);
        Assertions.assertEquals(6, galerie.score());

        galerie.addBleu(CouleurEchantillon.BLEU);
        Assertions.assertEquals(12, galerie.score());

        galerie.addBleu(CouleurEchantillon.ROCHER);
        Assertions.assertEquals(15, galerie.score());
    }

    @Test
    void testBestBleu() {
        Galerie.GaleriePosition result = galerie.bestPosition(CouleurEchantillon.BLEU, null);
        Assertions.assertEquals(Galerie.Etage.BAS, result.etage());
        Assertions.assertEquals(Galerie.Periode.BLEU, result.periode());
        galerie.addBleu(CouleurEchantillon.BLEU);
        Assertions.assertEquals(6, galerie.score());
        Assertions.assertFalse(galerie.complete());

        result = galerie.bestPosition(CouleurEchantillon.BLEU, null);
        Assertions.assertEquals(Galerie.Etage.HAUT, result.etage());
        Assertions.assertEquals(Galerie.Periode.BLEU, result.periode());
        galerie.addBleu(CouleurEchantillon.BLEU);
        Assertions.assertEquals(12, galerie.score());
        Assertions.assertFalse(galerie.complete());

        result = galerie.bestPosition(CouleurEchantillon.BLEU, null);
        Assertions.assertEquals(Galerie.Etage.BAS, result.etage());
        Assertions.assertEquals(Galerie.Periode.BLEU_VERT, result.periode());
        galerie.addBleuVert(CouleurEchantillon.BLEU);
        Assertions.assertEquals(18, galerie.score());
        Assertions.assertFalse(galerie.complete());

        result = galerie.bestPosition(CouleurEchantillon.BLEU, null);
        Assertions.assertEquals(Galerie.Etage.HAUT, result.etage());
        Assertions.assertEquals(Galerie.Periode.BLEU_VERT, result.periode());
        galerie.addBleuVert(CouleurEchantillon.BLEU);
        Assertions.assertEquals(24, galerie.score());
        Assertions.assertFalse(galerie.complete());

        result = galerie.bestPosition(CouleurEchantillon.BLEU, null);
        Assertions.assertEquals(Galerie.Etage.BAS, result.etage());
        Assertions.assertEquals(Galerie.Periode.ROUGE, result.periode());
        galerie.addRouge(CouleurEchantillon.BLEU);
        Assertions.assertEquals(27, galerie.score());
        Assertions.assertFalse(galerie.complete());

        result = galerie.bestPosition(CouleurEchantillon.BLEU, null);
        Assertions.assertEquals(Galerie.Etage.HAUT, result.etage());
        Assertions.assertEquals(Galerie.Periode.ROUGE, result.periode());
        galerie.addRouge(CouleurEchantillon.BLEU);
        Assertions.assertEquals(30, galerie.score());
        Assertions.assertFalse(galerie.complete());

        result = galerie.bestPosition(CouleurEchantillon.BLEU, null);
        Assertions.assertEquals(Galerie.Etage.BAS, result.etage());
        Assertions.assertEquals(Galerie.Periode.ROUGE_VERT, result.periode());
        galerie.addRougeVert(CouleurEchantillon.BLEU);
        Assertions.assertEquals(33, galerie.score());
        Assertions.assertFalse(galerie.complete());

        result = galerie.bestPosition(CouleurEchantillon.BLEU, null);
        Assertions.assertEquals(Galerie.Etage.HAUT, result.etage());
        Assertions.assertEquals(Galerie.Periode.ROUGE_VERT, result.periode());
        galerie.addRougeVert(CouleurEchantillon.BLEU);
        Assertions.assertEquals(36, galerie.score());
        Assertions.assertFalse(galerie.complete());

        result = galerie.bestPosition(CouleurEchantillon.BLEU, null);
        Assertions.assertEquals(Galerie.Etage.BAS, result.etage());
        Assertions.assertEquals(Galerie.Periode.VERT, result.periode());
        galerie.addVert(CouleurEchantillon.BLEU);
        Assertions.assertEquals(39, galerie.score());
        Assertions.assertFalse(galerie.complete());

        result = galerie.bestPosition(CouleurEchantillon.BLEU, null);
        Assertions.assertEquals(Galerie.Etage.HAUT, result.etage());
        Assertions.assertEquals(Galerie.Periode.VERT, result.periode());
        galerie.addVert(CouleurEchantillon.BLEU);
        Assertions.assertEquals(42, galerie.score());
        Assertions.assertTrue(galerie.complete());
    }

    @Test
    void testBestBleuCurrentBleuVert() {
        // Init
        galerie.addBleuVert(CouleurEchantillon.VERT);

        Galerie.GaleriePosition result = galerie.bestPosition(CouleurEchantillon.BLEU, Galerie.Periode.BLEU_VERT);
        Assertions.assertEquals(Galerie.Etage.HAUT, result.etage());
        Assertions.assertEquals(Galerie.Periode.BLEU_VERT, result.periode());
        galerie.addBleuVert(CouleurEchantillon.BLEU);
        Assertions.assertEquals(12, galerie.score());
        Assertions.assertFalse(galerie.complete());

        // Ici le bleu vert est full, on est censé donc rediriger vers le bleu.
        result = galerie.bestPosition(CouleurEchantillon.BLEU, result.periode());
        Assertions.assertEquals(Galerie.Etage.BAS, result.etage());
        Assertions.assertEquals(Galerie.Periode.BLEU, result.periode());
        galerie.addBleu(CouleurEchantillon.BLEU);
        Assertions.assertEquals(18, galerie.score());
        Assertions.assertFalse(galerie.complete());

        result = galerie.bestPosition(CouleurEchantillon.BLEU, result.periode());
        Assertions.assertEquals(Galerie.Etage.HAUT, result.etage());
        Assertions.assertEquals(Galerie.Periode.BLEU, result.periode());
        galerie.addBleu(CouleurEchantillon.BLEU);
        Assertions.assertEquals(24, galerie.score());
        Assertions.assertFalse(galerie.complete());

        // Ici le bleu est full, on est censé donc rediriger vers le rouge.
        result = galerie.bestPosition(CouleurEchantillon.BLEU, result.periode());
        Assertions.assertEquals(Galerie.Etage.BAS, result.etage());
        Assertions.assertEquals(Galerie.Periode.ROUGE, result.periode());
        galerie.addRouge(CouleurEchantillon.BLEU);
        Assertions.assertEquals(27, galerie.score());
        Assertions.assertFalse(galerie.complete());
    }

    @Test
    void testBestBleuCurrentRouge() {
        // Init
        galerie.addBleu(CouleurEchantillon.BLEU);
        galerie.addBleuVert(CouleurEchantillon.VERT);
        galerie.addRouge(CouleurEchantillon.ROUGE);

        // Nouvelle dépose
        Galerie.GaleriePosition result = galerie.bestPosition(CouleurEchantillon.BLEU, Galerie.Periode.ROUGE);
        Assertions.assertEquals(Galerie.Etage.HAUT, result.etage());
        Assertions.assertEquals(Galerie.Periode.BLEU, result.periode());
        galerie.addBleu(CouleurEchantillon.BLEU);
        Assertions.assertEquals(24, galerie.score());
        Assertions.assertFalse(galerie.complete());

        // Ici le bleu vert est full, on est censé donc rediriger vers le bleu.
        result = galerie.bestPosition(CouleurEchantillon.BLEU, result.periode());
        Assertions.assertEquals(Galerie.Etage.HAUT, result.etage());
        Assertions.assertEquals(Galerie.Periode.BLEU_VERT, result.periode());
        galerie.addBleuVert(CouleurEchantillon.BLEU);
        Assertions.assertEquals(30, galerie.score());
        Assertions.assertFalse(galerie.complete());

        result = galerie.bestPosition(CouleurEchantillon.BLEU, result.periode());
        Assertions.assertEquals(Galerie.Etage.HAUT, result.etage());
        Assertions.assertEquals(Galerie.Periode.ROUGE, result.periode());
        galerie.addRouge(CouleurEchantillon.BLEU);
        Assertions.assertEquals(33, galerie.score());
        Assertions.assertFalse(galerie.complete());

        // Ici le bleu est full, on est censé donc rediriger vers le rouge.
        result = galerie.bestPosition(CouleurEchantillon.BLEU, result.periode());
        Assertions.assertEquals(Galerie.Etage.BAS, result.etage());
        Assertions.assertEquals(Galerie.Periode.ROUGE_VERT, result.periode());
        galerie.addRougeVert(CouleurEchantillon.BLEU);
        Assertions.assertEquals(36, galerie.score());
        Assertions.assertFalse(galerie.complete());
    }

    @Test
    void testBestVert() {
        Galerie.GaleriePosition result = galerie.bestPosition(CouleurEchantillon.VERT, null);
        Assertions.assertEquals(Galerie.Etage.BAS, result.etage());
        Assertions.assertEquals(Galerie.Periode.VERT, result.periode());
        galerie.addVert(CouleurEchantillon.VERT);
        Assertions.assertEquals(6, galerie.score());
        Assertions.assertFalse(galerie.complete());

        result = galerie.bestPosition(CouleurEchantillon.VERT, null);
        Assertions.assertEquals(Galerie.Etage.HAUT, result.etage());
        Assertions.assertEquals(Galerie.Periode.VERT, result.periode());
        galerie.addVert(CouleurEchantillon.VERT);
        Assertions.assertEquals(12, galerie.score());
        Assertions.assertFalse(galerie.complete());

        result = galerie.bestPosition(CouleurEchantillon.VERT, null);
        Assertions.assertEquals(Galerie.Etage.BAS, result.etage());
        Assertions.assertEquals(Galerie.Periode.BLEU_VERT, result.periode());
        galerie.addBleuVert(CouleurEchantillon.VERT);
        Assertions.assertEquals(18, galerie.score());
        Assertions.assertFalse(galerie.complete());

        result = galerie.bestPosition(CouleurEchantillon.VERT, null);
        Assertions.assertEquals(Galerie.Etage.HAUT, result.etage());
        Assertions.assertEquals(Galerie.Periode.BLEU_VERT, result.periode());
        galerie.addBleuVert(CouleurEchantillon.VERT);
        Assertions.assertEquals(24, galerie.score());
        Assertions.assertFalse(galerie.complete());

        result = galerie.bestPosition(CouleurEchantillon.VERT, null);
        Assertions.assertEquals(Galerie.Etage.BAS, result.etage());
        Assertions.assertEquals(Galerie.Periode.ROUGE_VERT, result.periode());
        galerie.addRougeVert(CouleurEchantillon.VERT);
        Assertions.assertEquals(30, galerie.score());
        Assertions.assertFalse(galerie.complete());

        result = galerie.bestPosition(CouleurEchantillon.VERT, null);
        Assertions.assertEquals(Galerie.Etage.HAUT, result.etage());
        Assertions.assertEquals(Galerie.Periode.ROUGE_VERT, result.periode());
        galerie.addRougeVert(CouleurEchantillon.VERT);
        Assertions.assertEquals(36, galerie.score());
        Assertions.assertFalse(galerie.complete());

        result = galerie.bestPosition(CouleurEchantillon.VERT, null);
        Assertions.assertEquals(Galerie.Etage.BAS, result.etage());
        Assertions.assertEquals(Galerie.Periode.BLEU, result.periode());
        galerie.addBleu(CouleurEchantillon.VERT);
        Assertions.assertEquals(39, galerie.score());
        Assertions.assertFalse(galerie.complete());

        result = galerie.bestPosition(CouleurEchantillon.VERT, null);
        Assertions.assertEquals(Galerie.Etage.HAUT, result.etage());
        Assertions.assertEquals(Galerie.Periode.BLEU, result.periode());
        galerie.addBleu(CouleurEchantillon.VERT);
        Assertions.assertEquals(42, galerie.score());
        Assertions.assertFalse(galerie.complete());

        result = galerie.bestPosition(CouleurEchantillon.VERT, null);
        Assertions.assertEquals(Galerie.Etage.BAS, result.etage());
        Assertions.assertEquals(Galerie.Periode.ROUGE, result.periode());
        galerie.addRouge(CouleurEchantillon.VERT);
        Assertions.assertEquals(45, galerie.score());
        Assertions.assertFalse(galerie.complete());

        result = galerie.bestPosition(CouleurEchantillon.VERT, null);
        Assertions.assertEquals(Galerie.Etage.HAUT, result.etage());
        Assertions.assertEquals(Galerie.Periode.ROUGE, result.periode());
        galerie.addRouge(CouleurEchantillon.VERT);
        Assertions.assertEquals(48, galerie.score());
        Assertions.assertTrue(galerie.complete());
    }

    @Test
    void testBestRouge() {
        Galerie.GaleriePosition result = galerie.bestPosition(CouleurEchantillon.ROUGE, null);
        Assertions.assertEquals(Galerie.Etage.BAS, result.etage());
        Assertions.assertEquals(Galerie.Periode.ROUGE, result.periode());
        galerie.addRouge(CouleurEchantillon.ROUGE);
        Assertions.assertEquals(6, galerie.score());
        Assertions.assertFalse(galerie.complete());

        result = galerie.bestPosition(CouleurEchantillon.ROUGE, null);
        Assertions.assertEquals(Galerie.Etage.HAUT, result.etage());
        Assertions.assertEquals(Galerie.Periode.ROUGE, result.periode());
        galerie.addRouge(CouleurEchantillon.ROUGE);
        Assertions.assertEquals(12, galerie.score());
        Assertions.assertFalse(galerie.complete());

        result = galerie.bestPosition(CouleurEchantillon.ROUGE, null);
        Assertions.assertEquals(Galerie.Etage.BAS, result.etage());
        Assertions.assertEquals(Galerie.Periode.ROUGE_VERT, result.periode());
        galerie.addRougeVert(CouleurEchantillon.ROUGE);
        Assertions.assertEquals(18, galerie.score());
        Assertions.assertFalse(galerie.complete());

        result = galerie.bestPosition(CouleurEchantillon.ROUGE, null);
        Assertions.assertEquals(Galerie.Etage.HAUT, result.etage());
        Assertions.assertEquals(Galerie.Periode.ROUGE_VERT, result.periode());
        galerie.addRougeVert(CouleurEchantillon.ROUGE);
        Assertions.assertEquals(24, galerie.score());
        Assertions.assertFalse(galerie.complete());

        result = galerie.bestPosition(CouleurEchantillon.ROUGE, null);
        Assertions.assertEquals(Galerie.Etage.BAS, result.etage());
        Assertions.assertEquals(Galerie.Periode.BLEU, result.periode());
        galerie.addBleu(CouleurEchantillon.ROUGE);
        Assertions.assertEquals(27, galerie.score());
        Assertions.assertFalse(galerie.complete());

        result = galerie.bestPosition(CouleurEchantillon.ROUGE, null);
        Assertions.assertEquals(Galerie.Etage.HAUT, result.etage());
        Assertions.assertEquals(Galerie.Periode.BLEU, result.periode());
        galerie.addBleu(CouleurEchantillon.ROUGE);
        Assertions.assertEquals(30, galerie.score());
        Assertions.assertFalse(galerie.complete());

        result = galerie.bestPosition(CouleurEchantillon.ROUGE, null);
        Assertions.assertEquals(Galerie.Etage.BAS, result.etage());
        Assertions.assertEquals(Galerie.Periode.BLEU_VERT, result.periode());
        galerie.addBleuVert(CouleurEchantillon.ROUGE);
        Assertions.assertEquals(33, galerie.score());
        Assertions.assertFalse(galerie.complete());

        result = galerie.bestPosition(CouleurEchantillon.ROUGE, null);
        Assertions.assertEquals(Galerie.Etage.HAUT, result.etage());
        Assertions.assertEquals(Galerie.Periode.BLEU_VERT, result.periode());
        galerie.addBleuVert(CouleurEchantillon.ROUGE);
        Assertions.assertEquals(36, galerie.score());
        Assertions.assertFalse(galerie.complete());

        result = galerie.bestPosition(CouleurEchantillon.ROUGE, null);
        Assertions.assertEquals(Galerie.Etage.BAS, result.etage());
        Assertions.assertEquals(Galerie.Periode.VERT, result.periode());
        galerie.addVert(CouleurEchantillon.ROUGE);
        Assertions.assertEquals(39, galerie.score());
        Assertions.assertFalse(galerie.complete());

        result = galerie.bestPosition(CouleurEchantillon.ROUGE, null);
        Assertions.assertEquals(Galerie.Etage.HAUT, result.etage());
        Assertions.assertEquals(Galerie.Periode.VERT, result.periode());
        galerie.addVert(CouleurEchantillon.ROUGE);
        Assertions.assertEquals(42, galerie.score());
        Assertions.assertTrue(galerie.complete());
    }

    @Test
    public void testDoubleDepose() {
        Galerie.GaleriePosition result = galerie.bestPositionDoubleDepose(CouleurEchantillon.ROUGE, CouleurEchantillon.VERT, null);
        Assertions.assertEquals(Galerie.Etage.DOUBLE, result.etage());
        Assertions.assertEquals(Galerie.Periode.ROUGE_VERT, result.periode());
    }
}

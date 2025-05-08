package org.arig.robot.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

public class StockFaceTest {

  @Test
  public void testFaceVide() {
    StockFace stockFace = new StockFace();
    Assertions.assertTrue(stockFace.isEmpty());
    Assertions.assertFalse(stockFace.isInvalid());
  }

  @Test
  public void testFaceRemplie() {
    StockFace stockFace = new StockFace();
    stockFace.pinceGauche(true);
    stockFace.pinceDroite(true);
    stockFace.solGauche(true);
    stockFace.solDroite(true);
    stockFace.tiroirHaut(true);
    stockFace.tiroirBas(true);

    Assertions.assertFalse(stockFace.isEmpty());
    Assertions.assertFalse(stockFace.isInvalid());
    Assertions.assertEquals(2, stockFace.nbEtageConstructible());
  }

  @Test
  public void testFaceHybride() {
    StockFace stockFace = new StockFace();

    // Erreur de remplissage tirroir
    stockFace.pinceGauche(true);
    stockFace.pinceDroite(true);
    stockFace.solGauche(true);
    stockFace.solDroite(true);
    stockFace.tiroirHaut(false);
    stockFace.tiroirBas(true);

    Assertions.assertFalse(stockFace.isEmpty());
    Assertions.assertFalse(stockFace.isInvalid());
    Assertions.assertEquals(1, stockFace.nbEtageConstructible());

    // Erreur de remplissage sol
    stockFace.pinceGauche(true);
    stockFace.pinceDroite(true);
    stockFace.solGauche(false);
    stockFace.solDroite(false);
    stockFace.tiroirHaut(true);
    stockFace.tiroirBas(true);

    Assertions.assertFalse(stockFace.isEmpty());
    Assertions.assertFalse(stockFace.isInvalid());
    Assertions.assertEquals(1, stockFace.nbEtageConstructible());

    // Cas impossible, mais bon on test quand même des fois que l'on perde une colonne dans une pince dans le transport
    stockFace.pinceGauche(false);
    stockFace.pinceDroite(true);
    stockFace.solGauche(true);
    stockFace.solDroite(true);
    stockFace.tiroirHaut(true);
    stockFace.tiroirBas(true);
    Assertions.assertFalse(stockFace.isEmpty());
    Assertions.assertTrue(stockFace.isInvalid());
    Assertions.assertEquals(0, stockFace.nbEtageConstructible());
  }

}

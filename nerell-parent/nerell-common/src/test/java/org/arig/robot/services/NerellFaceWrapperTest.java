package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.NerellRobotStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@Slf4j
public class NerellFaceWrapperTest {

  private static final NerellRobotStatus rs = new NerellRobotStatus();
  private static final NerellFaceAvantService faceAvantService = new NerellFaceAvantService(rs, null, null, null);
  private static final NerellFaceArriereService faceArriereService = new NerellFaceArriereService(rs,null, null, null);
  private static final NerellFaceWrapper nerellFaceWrapper = new NerellFaceWrapper(rs, faceAvantService, faceArriereService);

  @BeforeEach
  public void setUp() {
    rs.faceAvant().clear();
    rs.faceArriere().clear();
  }

  @Test
  public void testGetFaceService_noFace() {
    Assertions.assertNull(nerellFaceWrapper.getFaceService(null));
  }

  @Test
  public void testGetFaceService_avant() {
    Assertions.assertEquals(faceAvantService, nerellFaceWrapper.getFaceService(NerellFaceWrapper.Face.AVANT));
  }

  @Test
  public void testGetFaceService_arriere() {
    Assertions.assertEquals(faceArriereService, nerellFaceWrapper.getFaceService(NerellFaceWrapper.Face.ARRIERE));
  }

  @Test
  public void testGetFaceVide_UsageDeuxFace() {
    rs.useTwoFaces(true);

    // Deux vides
    rs.faceArriere().pinceDroite(false);
    rs.faceAvant().pinceDroite(false);
    Assertions.assertEquals(NerellFaceWrapper.Face.AVANT, nerellFaceWrapper.getEmptyFace(rs.useTwoFaces()));

    // Avant non vide
    rs.faceArriere().pinceDroite(false);
    rs.faceAvant().pinceDroite(true);
    Assertions.assertEquals(NerellFaceWrapper.Face.ARRIERE, nerellFaceWrapper.getEmptyFace(rs.useTwoFaces()));

    // Arrière non vide
    rs.faceArriere().pinceDroite(true);
    rs.faceAvant().pinceDroite(false);
    Assertions.assertEquals(NerellFaceWrapper.Face.AVANT, nerellFaceWrapper.getEmptyFace(rs.useTwoFaces()));

    // Deux pleines
    rs.faceArriere().pinceDroite(true);
    rs.faceAvant().pinceDroite(true);
    Assertions.assertNull(nerellFaceWrapper.getEmptyFace(rs.useTwoFaces()));
  }

  @Test
  public void testGetFaceVide_UsageUneFace() {
    rs.useTwoFaces(false);

    // Deux vides
    rs.faceArriere().pinceDroite(false);
    rs.faceAvant().pinceDroite(false);
    Assertions.assertEquals(NerellFaceWrapper.Face.AVANT, nerellFaceWrapper.getEmptyFace(rs.useTwoFaces()));

    // Avant non vide
    rs.faceArriere().pinceDroite(false);
    rs.faceAvant().pinceDroite(true);
    Assertions.assertNull(nerellFaceWrapper.getEmptyFace(rs.useTwoFaces()));

    // Arrière non vide
    rs.faceArriere().pinceDroite(true);
    rs.faceAvant().pinceDroite(false);
    Assertions.assertEquals(NerellFaceWrapper.Face.AVANT, nerellFaceWrapper.getEmptyFace(rs.useTwoFaces()));

    // Deux pleines
    rs.faceArriere().pinceDroite(true);
    rs.faceAvant().pinceDroite(true);
    Assertions.assertNull(nerellFaceWrapper.getEmptyFace(rs.useTwoFaces()));
  }

  @Test
  public void testConstructionEtages_ToutVide() {
    Assertions.assertNull(nerellFaceWrapper.getConstructionFace(1));
    Assertions.assertNull(nerellFaceWrapper.getConstructionFace(2));
  }

  @Test
  public void testConstructionEtages_AvantSolTiroirBasArriereVide() {
    rs.faceAvant().solGauche(true).solDroite(true).tiroirBas(true);
    Assertions.assertEquals(NerellFaceWrapper.Face.AVANT, nerellFaceWrapper.getConstructionFace(1));
    Assertions.assertEquals(NerellFaceWrapper.Face.AVANT, nerellFaceWrapper.getConstructionFace(2));
  }

  @Test
  public void testConstructionEtages_AvantSolTiroirBasArriereSolTiroirBas() {
    rs.faceAvant().solGauche(true).solDroite(true).tiroirBas(true);
    rs.faceArriere().solGauche(true).solDroite(true).tiroirBas(true);
    Assertions.assertEquals(NerellFaceWrapper.Face.AVANT, nerellFaceWrapper.getConstructionFace(1));
    Assertions.assertEquals(NerellFaceWrapper.Face.AVANT, nerellFaceWrapper.getConstructionFace(2));
  }

  @Test
  public void testConstructionEtages_AvantFullArriereSolTiroirBas() {
    rs.faceAvant().solGauche(true).solDroite(true).pinceGauche(true).pinceDroite(true).tiroirBas(true).tiroirHaut(true);
    rs.faceArriere().solGauche(true).solDroite(true).tiroirBas(true);
    Assertions.assertEquals(NerellFaceWrapper.Face.ARRIERE, nerellFaceWrapper.getConstructionFace(1));
    Assertions.assertEquals(NerellFaceWrapper.Face.AVANT, nerellFaceWrapper.getConstructionFace(2));
  }

  @Test
  public void testConstructionEtages_AvantFullArriereFull() {
    rs.faceAvant().solGauche(true).solDroite(true).pinceGauche(true).pinceDroite(true).tiroirBas(true).tiroirHaut(true);
    rs.faceArriere().solGauche(true).solDroite(true).pinceGauche(true).pinceDroite(true).tiroirBas(true).tiroirHaut(true);
    Assertions.assertEquals(NerellFaceWrapper.Face.AVANT, nerellFaceWrapper.getConstructionFace(1));
    Assertions.assertEquals(NerellFaceWrapper.Face.AVANT, nerellFaceWrapper.getConstructionFace(2));
  }

  @Test
  public void testConstructionEtages_ArriereSolTiroirBasAvantVide() {
    rs.faceArriere().solGauche(true).solDroite(true).tiroirBas(true);
    Assertions.assertEquals(NerellFaceWrapper.Face.ARRIERE, nerellFaceWrapper.getConstructionFace(1));
    Assertions.assertEquals(NerellFaceWrapper.Face.ARRIERE, nerellFaceWrapper.getConstructionFace(2));
  }

  @Test
  public void testConstructionEtages_ArriereFullAvantSolTiroirBas() {
    rs.faceArriere().solGauche(true).solDroite(true).pinceGauche(true).pinceDroite(true).tiroirBas(true).tiroirHaut(true);
    rs.faceAvant().solGauche(true).solDroite(true).tiroirBas(true);
    Assertions.assertEquals(NerellFaceWrapper.Face.AVANT, nerellFaceWrapper.getConstructionFace(1));
    Assertions.assertEquals(NerellFaceWrapper.Face.ARRIERE, nerellFaceWrapper.getConstructionFace(2));
  }
}

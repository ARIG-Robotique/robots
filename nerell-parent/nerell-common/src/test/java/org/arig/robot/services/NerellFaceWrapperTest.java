package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.NerellFace;
import org.arig.robot.model.NerellRobotStatus;
import org.arig.robot.model.Position;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@Slf4j
public class NerellFaceWrapperTest {

  private static final NerellRobotStatus rs = new NerellRobotStatus();
  private static final Position currentPosition = new Position();
  private static final NerellFaceAvantService faceAvantService = new NerellFaceAvantService(rs, null, null, null);
  private static final NerellFaceArriereService faceArriereService = new NerellFaceArriereService(rs, null, null, null);
  private static final NerellFaceWrapper nerellFaceWrapper = new NerellFaceWrapper(currentPosition, rs, faceAvantService, faceArriereService);

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
    Assertions.assertEquals(faceAvantService, nerellFaceWrapper.getFaceService(NerellFace.AVANT));
  }

  @Test
  public void testGetFaceService_arriere() {
    Assertions.assertEquals(faceArriereService, nerellFaceWrapper.getFaceService(NerellFace.ARRIERE));
  }

  @Test
  public void testGetFaceVide_UsageDeuxFace() {
    rs.useTwoFaces(true);

    // Deux vides
    rs.faceArriere().pinceDroite(false);
    rs.faceAvant().pinceDroite(false);
    Assertions.assertEquals(NerellFace.AVANT, nerellFaceWrapper.getEmptyFace(rs.useTwoFaces()));

    // Avant non vide
    rs.faceArriere().pinceDroite(false);
    rs.faceAvant().pinceDroite(true);
    Assertions.assertEquals(NerellFace.ARRIERE, nerellFaceWrapper.getEmptyFace(rs.useTwoFaces()));

    // Arrière non vide
    rs.faceArriere().pinceDroite(true);
    rs.faceAvant().pinceDroite(false);
    Assertions.assertEquals(NerellFace.AVANT, nerellFaceWrapper.getEmptyFace(rs.useTwoFaces()));

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
    Assertions.assertEquals(NerellFace.AVANT, nerellFaceWrapper.getEmptyFace(rs.useTwoFaces()));

    // Avant non vide
    rs.faceArriere().pinceDroite(false);
    rs.faceAvant().pinceDroite(true);
    Assertions.assertNull(nerellFaceWrapper.getEmptyFace(rs.useTwoFaces()));

    // Arrière non vide
    rs.faceArriere().pinceDroite(true);
    rs.faceAvant().pinceDroite(false);
    Assertions.assertEquals(NerellFace.AVANT, nerellFaceWrapper.getEmptyFace(rs.useTwoFaces()));

    // Deux pleines
    rs.faceArriere().pinceDroite(true);
    rs.faceAvant().pinceDroite(true);
    Assertions.assertNull(nerellFaceWrapper.getEmptyFace(rs.useTwoFaces()));
  }
}

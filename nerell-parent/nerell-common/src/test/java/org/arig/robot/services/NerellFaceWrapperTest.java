package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.NerellConstantesConfig;
import org.arig.robot.model.Face;
import org.arig.robot.model.GradinBrut;
import org.arig.robot.model.NerellRobotStatus;
import org.arig.robot.model.Point;
import org.arig.robot.model.Position;
import org.arig.robot.utils.ConvertionRobotUnit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@Slf4j
public class NerellFaceWrapperTest {

  private static final ConvertionRobotUnit convRobot = new ConvertionRobotUnit(NerellConstantesConfig.countPerMm, NerellConstantesConfig.entraxe, true);
  private static final NerellRobotStatus rs = new NerellRobotStatus();
  private static final Position currentPosition = new Position();
  private static final NerellFaceAvantService faceAvantService = new NerellFaceAvantService(rs, null, null, null);
  private static final NerellFaceArriereService faceArriereService = new NerellFaceArriereService(rs, null, null, null);
  private static final NerellFaceWrapper nerellFaceWrapper = new NerellFaceWrapper(convRobot, currentPosition, rs, faceAvantService, faceArriereService);

  @BeforeEach
  void setUp() {
    rs.faceAvant().clear();
    rs.faceArriere().clear();
  }

  @Test
  void testGetFaceService_noFace() {
    Assertions.assertNull(nerellFaceWrapper.getFaceService(null));
  }

  @Test
  void testGetFaceService_avant() {
    Assertions.assertEquals(faceAvantService, nerellFaceWrapper.getFaceService(Face.AVANT));
  }

  @Test
  void testGetFaceService_arriere() {
    Assertions.assertEquals(faceArriereService, nerellFaceWrapper.getFaceService(Face.ARRIERE));
  }

  @Test
  void testGetFaceVide_UneFaceNonVide() {
    // Avant non vide JAUNE_MILIEU_CENTRE
    rs.faceArriere().pinceDroite(false);
    rs.faceAvant().pinceDroite(true);
    Assertions.assertEquals(Face.ARRIERE, nerellFaceWrapper.getEmptyFace(GradinBrut.ID.JAUNE_MILIEU_CENTRE));

    // Arriere non vide JAUNE_MILIEU_CENTRE
    rs.faceArriere().pinceDroite(true);
    rs.faceAvant().pinceDroite(false);
    Assertions.assertEquals(Face.AVANT, nerellFaceWrapper.getEmptyFace(GradinBrut.ID.JAUNE_MILIEU_CENTRE));

    // Avant non vide BLEU_BAS_DROITE
    rs.faceArriere().pinceDroite(false);
    rs.faceAvant().pinceDroite(true);
    Assertions.assertEquals(Face.ARRIERE, nerellFaceWrapper.getEmptyFace(GradinBrut.ID.BLEU_BAS_DROITE));

    // Arriere non vide BLEU_BAS_DROITE
    rs.faceArriere().pinceDroite(true);
    rs.faceAvant().pinceDroite(false);
    Assertions.assertEquals(Face.AVANT, nerellFaceWrapper.getEmptyFace(GradinBrut.ID.BLEU_BAS_DROITE));

    // Aucunes face vide
    rs.faceArriere().pinceDroite(true);
    rs.faceAvant().pinceDroite(true);
    Assertions.assertNull(nerellFaceWrapper.getEmptyFace(GradinBrut.ID.JAUNE_MILIEU_CENTRE));
  }

  @Test
  void testGetFaceVide_DeuxVidesMeilleureOrientation() {
    rs.faceArriere().pinceDroite(false);
    rs.faceAvant().pinceDroite(false);

    currentPosition.setAngle(convRobot.degToPulse(-15));
    Assertions.assertEquals(Face.AVANT, nerellFaceWrapper.getEmptyFace(GradinBrut.ID.JAUNE_BAS_CENTRE));

    currentPosition.setAngle(convRobot.degToPulse(10));
    Assertions.assertEquals(Face.ARRIERE, nerellFaceWrapper.getEmptyFace(GradinBrut.ID.JAUNE_BAS_CENTRE));

    currentPosition.setAngle(convRobot.degToPulse(-15));
    Assertions.assertEquals(Face.ARRIERE, nerellFaceWrapper.getEmptyFace(GradinBrut.ID.BLEU_RESERVE));

    currentPosition.setAngle(convRobot.degToPulse(10));
    Assertions.assertEquals(Face.AVANT, nerellFaceWrapper.getEmptyFace(GradinBrut.ID.BLEU_RESERVE));

    currentPosition.setAngle(convRobot.degToPulse(130));
    Assertions.assertEquals(Face.AVANT, nerellFaceWrapper.getEmptyFace(GradinBrut.ID.JAUNE_BAS_GAUCHE));

    currentPosition.setAngle(convRobot.degToPulse(50));
    Assertions.assertEquals(Face.ARRIERE, nerellFaceWrapper.getEmptyFace(GradinBrut.ID.JAUNE_BAS_GAUCHE));

    currentPosition.setAngle(convRobot.degToPulse(130));
    Assertions.assertEquals(Face.ARRIERE, nerellFaceWrapper.getEmptyFace(GradinBrut.ID.BLEU_HAUT_DROITE));

    currentPosition.setAngle(convRobot.degToPulse(50));
    Assertions.assertEquals(Face.AVANT, nerellFaceWrapper.getEmptyFace(GradinBrut.ID.BLEU_HAUT_DROITE));

    // Test prise centre si arrivée par en bas
    currentPosition.setPt(new Point(convRobot.mmToPulse(1500), convRobot.mmToPulse(700)));
    currentPosition.setAngle(convRobot.degToPulse(55));
    Assertions.assertEquals(Face.AVANT, nerellFaceWrapper.getEmptyFace(GradinBrut.ID.JAUNE_MILIEU_CENTRE));

    // Test prise centre si arrivée par en bas
    currentPosition.setPt(new Point(convRobot.mmToPulse(1500), convRobot.mmToPulse(700)));
    currentPosition.setAngle(convRobot.degToPulse(-145));
    Assertions.assertEquals(Face.ARRIERE, nerellFaceWrapper.getEmptyFace(GradinBrut.ID.JAUNE_MILIEU_CENTRE));

    // Test prise centre si arrivée par en haut
    currentPosition.setPt(new Point(convRobot.mmToPulse(1500), convRobot.mmToPulse(1200)));
    currentPosition.setAngle(convRobot.degToPulse(55));
    Assertions.assertEquals(Face.ARRIERE, nerellFaceWrapper.getEmptyFace(GradinBrut.ID.JAUNE_MILIEU_CENTRE));

    // Test prise centre si arrivée par en haut
    currentPosition.setPt(new Point(convRobot.mmToPulse(1500), convRobot.mmToPulse(1200)));
    currentPosition.setAngle(convRobot.degToPulse(-145));
    Assertions.assertEquals(Face.AVANT, nerellFaceWrapper.getEmptyFace(GradinBrut.ID.JAUNE_MILIEU_CENTRE));
  }
}

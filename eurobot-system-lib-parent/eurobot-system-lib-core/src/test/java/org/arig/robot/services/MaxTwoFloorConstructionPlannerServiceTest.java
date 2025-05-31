package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.ConstructionAction;
import org.arig.robot.model.ConstructionActionType;
import org.arig.robot.model.ConstructionArea;
import org.arig.robot.model.ConstructionFloorAction;
import org.arig.robot.model.ConstructionMoveAction;
import org.arig.robot.model.ConstructionPlanResult;
import org.arig.robot.model.Etage;
import org.arig.robot.model.EurobotStatus;
import org.arig.robot.model.Face;
import org.arig.robot.model.Rang;
import org.arig.robot.model.StockPosition;
import org.arig.robot.model.TestEurobotStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.List;

@Slf4j
public class MaxTwoFloorConstructionPlannerServiceTest {

  private final EurobotStatus rs = new TestEurobotStatus(true);
  private final ConstructionPlannerService planner = new ConstructionPlannerService(rs);

  private final ConstructionArea small = new ConstructionArea("1 rang");
  private final ConstructionArea big = new ConstructionArea("3 rangs", (byte) 3);

  @BeforeEach
  void setUp() {
    rs.limiter2Etages(true);
    rs.faceAvant().clear();
    rs.faceArriere().clear();
    small.clean();
    big.clean();
  }



  @ParameterizedTest
  @EnumSource(Face.class)
  void testPlanEmptyFaces(Face face) {
    for (ConstructionArea area : List.of(small, big)) {
      ConstructionPlanResult planResult = planner.plan(area, face);
      Assertions.assertEquals(0, planResult.actions().size());
      Assertions.assertEquals(0, planResult.newArea().score());
      Assertions.assertEquals(0, planResult.newArea().score() - area.score());
    }
  }

  @ParameterizedTest
  @EnumSource(Face.class)
  void testPlanFaceAvantOnly(Face face) {
    rs.faceAvant()
      .pinceDroite(true).pinceGauche(true).tiroirHaut(true)
      .solDroite(true).solGauche(true).tiroirBas(true);

    for (ConstructionArea area : List.of(small, big)) {
      ConstructionPlanResult planResult = planner.plan(area, face);
      List<ConstructionAction> actions = planResult.actions();
      Assertions.assertEquals(3, actions.size());
      Assertions.assertEquals(12, planResult.newArea().score());
      Assertions.assertEquals(12, planResult.newArea().score() - area.score());

      ConstructionMoveAction moveAction;
      ConstructionFloorAction floorAction;

      Assertions.assertInstanceOf(ConstructionMoveAction.class, actions.get(0));
      moveAction = (ConstructionMoveAction) actions.get(0);
      Assertions.assertEquals(ConstructionActionType.MOVE, moveAction.type());
      Assertions.assertEquals(Face.AVANT, moveAction.face());
      Assertions.assertEquals(Rang.RANG_1, moveAction.rang());

      Assertions.assertInstanceOf(ConstructionFloorAction.class, actions.get(1));
      floorAction = (ConstructionFloorAction) actions.get(1);
      Assertions.assertEquals(ConstructionActionType.PLACE_ONE, floorAction.type());
      Assertions.assertEquals(Face.AVANT, floorAction.face());
      Assertions.assertEquals(Etage.ETAGE_1, floorAction.etage());
      Assertions.assertEquals(StockPosition.TOP, floorAction.stockPosition());

      Assertions.assertInstanceOf(ConstructionFloorAction.class, actions.get(1));
      floorAction = (ConstructionFloorAction) actions.get(2);
      Assertions.assertEquals(ConstructionActionType.PLACE_ONE, floorAction.type());
      Assertions.assertEquals(Face.AVANT, floorAction.face());
      Assertions.assertEquals(Etage.ETAGE_2, floorAction.etage());
      Assertions.assertEquals(StockPosition.BOTTOM, floorAction.stockPosition());

    }
  }

  @ParameterizedTest
  @EnumSource(Face.class)
  void testPlanFaceAvantOnlySmallAreaRang1Etage1(Face face) {
    rs.faceAvant()
      .pinceDroite(true).pinceGauche(true).tiroirHaut(true)
      .solDroite(true).solGauche(true).tiroirBas(true);

    small.addGradin(Rang.RANG_1, Etage.ETAGE_1);

    ConstructionPlanResult planResult = planner.plan(small, face);
    List<ConstructionAction> actions = planResult.actions();
    Assertions.assertEquals(2, actions.size());
    Assertions.assertEquals(12, planResult.newArea().score());
    Assertions.assertEquals(8, planResult.newArea().score() - small.score());

    ConstructionMoveAction moveAction;
    ConstructionFloorAction floorAction;

    Assertions.assertInstanceOf(ConstructionMoveAction.class, actions.get(0));
    moveAction = (ConstructionMoveAction) actions.get(0);
    Assertions.assertEquals(ConstructionActionType.MOVE, moveAction.type());
    Assertions.assertEquals(Face.AVANT, moveAction.face());
    Assertions.assertEquals(Rang.RANG_1, moveAction.rang());

    Assertions.assertInstanceOf(ConstructionFloorAction.class, actions.get(1));
    floorAction = (ConstructionFloorAction) actions.get(1);
    Assertions.assertEquals(ConstructionActionType.PLACE_ONE, floorAction.type());
    Assertions.assertEquals(Face.AVANT, floorAction.face());
    Assertions.assertEquals(Etage.ETAGE_2, floorAction.etage());
    Assertions.assertEquals(StockPosition.TOP, floorAction.stockPosition());
  }

  @ParameterizedTest
  @EnumSource(Face.class)
  void testPlanFaceAvantOnlyBigAreaRang1Etage1(Face face) {
    rs.faceAvant()
      .pinceDroite(true).pinceGauche(true).tiroirHaut(true)
      .solDroite(true).solGauche(true).tiroirBas(true);

    big.addGradin(Rang.RANG_1, Etage.ETAGE_1);

    ConstructionPlanResult planResult = planner.plan(big, face);
    List<ConstructionAction> actions = planResult.actions();
    Assertions.assertEquals(4, actions.size());
    Assertions.assertEquals(16, planResult.newArea().score());
    Assertions.assertEquals(12, planResult.newArea().score() - big.score());

    ConstructionMoveAction moveAction;
    ConstructionFloorAction floorAction;

    Assertions.assertInstanceOf(ConstructionMoveAction.class, actions.get(0));
    moveAction = (ConstructionMoveAction) actions.get(0);
    Assertions.assertEquals(ConstructionActionType.MOVE, moveAction.type());
    Assertions.assertEquals(Face.AVANT, moveAction.face());
    Assertions.assertEquals(Rang.RANG_1, moveAction.rang());

    Assertions.assertInstanceOf(ConstructionFloorAction.class, actions.get(1));
    floorAction = (ConstructionFloorAction) actions.get(1);
    Assertions.assertEquals(ConstructionActionType.PLACE_ONE, floorAction.type());
    Assertions.assertEquals(Face.AVANT, floorAction.face());
    Assertions.assertEquals(Etage.ETAGE_2, floorAction.etage());
    Assertions.assertEquals(StockPosition.TOP, floorAction.stockPosition());

    Assertions.assertInstanceOf(ConstructionMoveAction.class, actions.get(2));
    moveAction = (ConstructionMoveAction) actions.get(2);
    Assertions.assertEquals(ConstructionActionType.MOVE, moveAction.type());
    Assertions.assertEquals(Face.AVANT, moveAction.face());
    Assertions.assertEquals(Rang.RANG_2, moveAction.rang());

    Assertions.assertInstanceOf(ConstructionFloorAction.class, actions.get(3));
    floorAction = (ConstructionFloorAction) actions.get(3);
    Assertions.assertEquals(ConstructionActionType.PLACE_ONE, floorAction.type());
    Assertions.assertEquals(Face.AVANT, floorAction.face());
    Assertions.assertEquals(Etage.ETAGE_1, floorAction.etage());
    Assertions.assertEquals(StockPosition.BOTTOM, floorAction.stockPosition());
  }

  @ParameterizedTest
  @EnumSource(Face.class)
  void testPlanFaceArriereOnly(Face face) {
    rs.faceArriere()
      .pinceDroite(true).pinceGauche(true).tiroirHaut(true)
      .solDroite(true).solGauche(true).tiroirBas(true);

    for (ConstructionArea area : List.of(small, big)) {
      ConstructionPlanResult planResult = planner.plan(area, face);
      List<ConstructionAction> actions = planResult.actions();
      Assertions.assertEquals(3, actions.size());
      Assertions.assertEquals(12, planResult.newArea().score());
      Assertions.assertEquals(12, planResult.newArea().score() - area.score());

      ConstructionMoveAction moveAction;
      ConstructionFloorAction floorAction;

      Assertions.assertInstanceOf(ConstructionMoveAction.class, actions.get(0));
      moveAction = (ConstructionMoveAction) actions.get(0);
      Assertions.assertEquals(ConstructionActionType.MOVE, moveAction.type());
      Assertions.assertEquals(Face.ARRIERE, moveAction.face());
      Assertions.assertEquals(Rang.RANG_1, moveAction.rang());

      Assertions.assertInstanceOf(ConstructionFloorAction.class, actions.get(1));
      floorAction = (ConstructionFloorAction) actions.get(1);
      Assertions.assertEquals(ConstructionActionType.PLACE_ONE, floorAction.type());
      Assertions.assertEquals(Face.ARRIERE, floorAction.face());
      Assertions.assertEquals(Etage.ETAGE_1, floorAction.etage());
      Assertions.assertEquals(StockPosition.TOP, floorAction.stockPosition());

      Assertions.assertInstanceOf(ConstructionFloorAction.class, actions.get(1));
      floorAction = (ConstructionFloorAction) actions.get(2);
      Assertions.assertEquals(ConstructionActionType.PLACE_ONE, floorAction.type());
      Assertions.assertEquals(Face.ARRIERE, floorAction.face());
      Assertions.assertEquals(Etage.ETAGE_2, floorAction.etage());
      Assertions.assertEquals(StockPosition.BOTTOM, floorAction.stockPosition());
    }
  }

  @Test
  void testPlanFacesFullSmallArea() {
    rs.faceAvant()
      .pinceDroite(true).pinceGauche(true).tiroirHaut(true)
      .solDroite(true).solGauche(true).tiroirBas(true);
    rs.faceArriere()
      .pinceDroite(true).pinceGauche(true).tiroirHaut(true)
      .solDroite(true).solGauche(true).tiroirBas(true);

    ConstructionPlanResult planResult = planner.plan(small);
    List<ConstructionAction> actions = planResult.actions();
    Assertions.assertEquals(3, actions.size());
    Assertions.assertEquals(12, planResult.newArea().score());
    Assertions.assertEquals(12, planResult.newArea().score() - small.score());

    ConstructionMoveAction moveAction;
    ConstructionFloorAction floorAction;

    Assertions.assertInstanceOf(ConstructionMoveAction.class, actions.get(0));
    moveAction = (ConstructionMoveAction) actions.get(0);
    Assertions.assertEquals(ConstructionActionType.MOVE, moveAction.type());
    Assertions.assertEquals(Face.AVANT, moveAction.face());
    Assertions.assertEquals(Rang.RANG_1, moveAction.rang());

    Assertions.assertInstanceOf(ConstructionFloorAction.class, actions.get(1));
    floorAction = (ConstructionFloorAction) actions.get(1);
    Assertions.assertEquals(ConstructionActionType.PLACE_ONE, floorAction.type());
    Assertions.assertEquals(Face.AVANT, floorAction.face());
    Assertions.assertEquals(Etage.ETAGE_1, floorAction.etage());
    Assertions.assertEquals(StockPosition.TOP, floorAction.stockPosition());

    Assertions.assertInstanceOf(ConstructionFloorAction.class, actions.get(1));
    floorAction = (ConstructionFloorAction) actions.get(2);
    Assertions.assertEquals(ConstructionActionType.PLACE_ONE, floorAction.type());
    Assertions.assertEquals(Face.AVANT, floorAction.face());
    Assertions.assertEquals(Etage.ETAGE_2, floorAction.etage());
    Assertions.assertEquals(StockPosition.BOTTOM, floorAction.stockPosition());
  }

  @Test
  void testPlanFacesFullSmallAreaOrientedAvant() {
    rs.faceAvant()
      .pinceDroite(true).pinceGauche(true).tiroirHaut(true)
      .solDroite(true).solGauche(true).tiroirBas(true);
    rs.faceArriere()
      .pinceDroite(true).pinceGauche(true).tiroirHaut(true)
      .solDroite(true).solGauche(true).tiroirBas(true);

    ConstructionPlanResult planResult = planner.plan(small, Face.AVANT);
    List<ConstructionAction> actions = planResult.actions();
    Assertions.assertEquals(3, actions.size());
    Assertions.assertEquals(12, planResult.newArea().score());
    Assertions.assertEquals(12, planResult.newArea().score() - small.score());

    ConstructionMoveAction moveAction;
    ConstructionFloorAction floorAction;

    Assertions.assertInstanceOf(ConstructionMoveAction.class, actions.get(0));
    moveAction = (ConstructionMoveAction) actions.get(0);
    Assertions.assertEquals(ConstructionActionType.MOVE, moveAction.type());
    Assertions.assertEquals(Face.AVANT, moveAction.face());
    Assertions.assertEquals(Rang.RANG_1, moveAction.rang());

    Assertions.assertInstanceOf(ConstructionFloorAction.class, actions.get(1));
    floorAction = (ConstructionFloorAction) actions.get(1);
    Assertions.assertEquals(ConstructionActionType.PLACE_ONE, floorAction.type());
    Assertions.assertEquals(Face.AVANT, floorAction.face());
    Assertions.assertEquals(Etage.ETAGE_1, floorAction.etage());
    Assertions.assertEquals(StockPosition.TOP, floorAction.stockPosition());

    Assertions.assertInstanceOf(ConstructionFloorAction.class, actions.get(1));
    floorAction = (ConstructionFloorAction) actions.get(2);
    Assertions.assertEquals(ConstructionActionType.PLACE_ONE, floorAction.type());
    Assertions.assertEquals(Face.AVANT, floorAction.face());
    Assertions.assertEquals(Etage.ETAGE_2, floorAction.etage());
    Assertions.assertEquals(StockPosition.BOTTOM, floorAction.stockPosition());
  }

  @Test
  void testPlanFacesFullSmallAreaOrientedArriere() {
    rs.faceAvant()
      .pinceDroite(true).pinceGauche(true).tiroirHaut(true)
      .solDroite(true).solGauche(true).tiroirBas(true);
    rs.faceArriere()
      .pinceDroite(true).pinceGauche(true).tiroirHaut(true)
      .solDroite(true).solGauche(true).tiroirBas(true);

    ConstructionPlanResult planResult = planner.plan(small, Face.ARRIERE);
    List<ConstructionAction> actions = planResult.actions();
    Assertions.assertEquals(3, actions.size());
    Assertions.assertEquals(12, planResult.newArea().score());
    Assertions.assertEquals(12, planResult.newArea().score() - small.score());

    ConstructionMoveAction moveAction;
    ConstructionFloorAction floorAction;

    Assertions.assertInstanceOf(ConstructionMoveAction.class, actions.get(0));
    moveAction = (ConstructionMoveAction) actions.get(0);
    Assertions.assertEquals(ConstructionActionType.MOVE, moveAction.type());
    Assertions.assertEquals(Face.ARRIERE, moveAction.face());
    Assertions.assertEquals(Rang.RANG_1, moveAction.rang());

    Assertions.assertInstanceOf(ConstructionFloorAction.class, actions.get(1));
    floorAction = (ConstructionFloorAction) actions.get(1);
    Assertions.assertEquals(ConstructionActionType.PLACE_ONE, floorAction.type());
    Assertions.assertEquals(Face.ARRIERE, floorAction.face());
    Assertions.assertEquals(Etage.ETAGE_1, floorAction.etage());
    Assertions.assertEquals(StockPosition.TOP, floorAction.stockPosition());

    Assertions.assertInstanceOf(ConstructionFloorAction.class, actions.get(1));
    floorAction = (ConstructionFloorAction) actions.get(2);
    Assertions.assertEquals(ConstructionActionType.PLACE_ONE, floorAction.type());
    Assertions.assertEquals(Face.ARRIERE, floorAction.face());
    Assertions.assertEquals(Etage.ETAGE_2, floorAction.etage());
    Assertions.assertEquals(StockPosition.BOTTOM, floorAction.stockPosition());
  }

  @Test
  void testPlanFacesFullBigArea() {
    rs.faceAvant()
      .pinceDroite(true).pinceGauche(true).tiroirHaut(true)
      .solDroite(true).solGauche(true).tiroirBas(true);
    rs.faceArriere()
      .pinceDroite(true).pinceGauche(true).tiroirHaut(true)
      .solDroite(true).solGauche(true).tiroirBas(true);

    ConstructionPlanResult planResult = planner.plan(big);
    List<ConstructionAction> actions = planResult.actions();
    Assertions.assertEquals(6, actions.size());
    Assertions.assertEquals(24, planResult.newArea().score());
    Assertions.assertEquals(24, planResult.newArea().score() - big.score());

    ConstructionMoveAction moveAction;
    ConstructionFloorAction floorAction;

    Assertions.assertInstanceOf(ConstructionMoveAction.class, actions.get(0));
    moveAction = (ConstructionMoveAction) actions.get(0);
    Assertions.assertEquals(ConstructionActionType.MOVE, moveAction.type());
    Assertions.assertEquals(Face.AVANT, moveAction.face());
    Assertions.assertEquals(Rang.RANG_1, moveAction.rang());

    Assertions.assertInstanceOf(ConstructionFloorAction.class, actions.get(1));
    floorAction = (ConstructionFloorAction) actions.get(1);
    Assertions.assertEquals(ConstructionActionType.PLACE_ONE, floorAction.type());
    Assertions.assertEquals(Face.AVANT, floorAction.face());
    Assertions.assertEquals(Etage.ETAGE_1, floorAction.etage());
    Assertions.assertEquals(StockPosition.TOP, floorAction.stockPosition());

    Assertions.assertInstanceOf(ConstructionFloorAction.class, actions.get(1));
    floorAction = (ConstructionFloorAction) actions.get(2);
    Assertions.assertEquals(ConstructionActionType.PLACE_ONE, floorAction.type());
    Assertions.assertEquals(Face.AVANT, floorAction.face());
    Assertions.assertEquals(Etage.ETAGE_2, floorAction.etage());
    Assertions.assertEquals(StockPosition.BOTTOM, floorAction.stockPosition());

    Assertions.assertInstanceOf(ConstructionMoveAction.class, actions.get(3));
    moveAction = (ConstructionMoveAction) actions.get(3);
    Assertions.assertEquals(ConstructionActionType.MOVE, moveAction.type());
    Assertions.assertEquals(Face.ARRIERE, moveAction.face());
    Assertions.assertEquals(Rang.RANG_2, moveAction.rang());

    Assertions.assertInstanceOf(ConstructionFloorAction.class, actions.get(4));
    floorAction = (ConstructionFloorAction) actions.get(4);
    Assertions.assertEquals(ConstructionActionType.PLACE_ONE, floorAction.type());
    Assertions.assertEquals(Face.ARRIERE, floorAction.face());
    Assertions.assertEquals(Etage.ETAGE_1, floorAction.etage());
    Assertions.assertEquals(StockPosition.TOP, floorAction.stockPosition());

    Assertions.assertInstanceOf(ConstructionFloorAction.class, actions.get(5));
    floorAction = (ConstructionFloorAction) actions.get(5);
    Assertions.assertEquals(ConstructionActionType.PLACE_ONE, floorAction.type());
    Assertions.assertEquals(Face.ARRIERE, floorAction.face());
    Assertions.assertEquals(Etage.ETAGE_2, floorAction.etage());
    Assertions.assertEquals(StockPosition.BOTTOM, floorAction.stockPosition());
  }

  @Test
  void testPlanFacesFullBigAreaOrientedAvant() {
    rs.faceAvant()
      .pinceDroite(true).pinceGauche(true).tiroirHaut(true)
      .solDroite(true).solGauche(true).tiroirBas(true);
    rs.faceArriere()
      .pinceDroite(true).pinceGauche(true).tiroirHaut(true)
      .solDroite(true).solGauche(true).tiroirBas(true);

    ConstructionPlanResult planResult = planner.plan(big, Face.AVANT);
    List<ConstructionAction> actions = planResult.actions();
    Assertions.assertEquals(6, actions.size());
    Assertions.assertEquals(24, planResult.newArea().score());
    Assertions.assertEquals(24, planResult.newArea().score() - big.score());

    ConstructionMoveAction moveAction;
    ConstructionFloorAction floorAction;

    Assertions.assertInstanceOf(ConstructionMoveAction.class, actions.get(0));
    moveAction = (ConstructionMoveAction) actions.get(0);
    Assertions.assertEquals(ConstructionActionType.MOVE, moveAction.type());
    Assertions.assertEquals(Face.AVANT, moveAction.face());
    Assertions.assertEquals(Rang.RANG_1, moveAction.rang());

    Assertions.assertInstanceOf(ConstructionFloorAction.class, actions.get(1));
    floorAction = (ConstructionFloorAction) actions.get(1);
    Assertions.assertEquals(ConstructionActionType.PLACE_ONE, floorAction.type());
    Assertions.assertEquals(Face.AVANT, floorAction.face());
    Assertions.assertEquals(Etage.ETAGE_1, floorAction.etage());
    Assertions.assertEquals(StockPosition.TOP, floorAction.stockPosition());

    Assertions.assertInstanceOf(ConstructionFloorAction.class, actions.get(1));
    floorAction = (ConstructionFloorAction) actions.get(2);
    Assertions.assertEquals(ConstructionActionType.PLACE_ONE, floorAction.type());
    Assertions.assertEquals(Face.AVANT, floorAction.face());
    Assertions.assertEquals(Etage.ETAGE_2, floorAction.etage());
    Assertions.assertEquals(StockPosition.BOTTOM, floorAction.stockPosition());

    Assertions.assertInstanceOf(ConstructionMoveAction.class, actions.get(3));
    moveAction = (ConstructionMoveAction) actions.get(3);
    Assertions.assertEquals(ConstructionActionType.MOVE, moveAction.type());
    Assertions.assertEquals(Face.ARRIERE, moveAction.face());
    Assertions.assertEquals(Rang.RANG_2, moveAction.rang());

    Assertions.assertInstanceOf(ConstructionFloorAction.class, actions.get(4));
    floorAction = (ConstructionFloorAction) actions.get(4);
    Assertions.assertEquals(ConstructionActionType.PLACE_ONE, floorAction.type());
    Assertions.assertEquals(Face.ARRIERE, floorAction.face());
    Assertions.assertEquals(Etage.ETAGE_1, floorAction.etage());
    Assertions.assertEquals(StockPosition.TOP, floorAction.stockPosition());

    Assertions.assertInstanceOf(ConstructionFloorAction.class, actions.get(5));
    floorAction = (ConstructionFloorAction) actions.get(5);
    Assertions.assertEquals(ConstructionActionType.PLACE_ONE, floorAction.type());
    Assertions.assertEquals(Face.ARRIERE, floorAction.face());
    Assertions.assertEquals(Etage.ETAGE_2, floorAction.etage());
    Assertions.assertEquals(StockPosition.BOTTOM, floorAction.stockPosition());
  }

  @Test
  void testPlanFacesFullBigAreaOrientedArriere() {
    rs.faceAvant()
      .pinceDroite(true).pinceGauche(true).tiroirHaut(true)
      .solDroite(true).solGauche(true).tiroirBas(true);
    rs.faceArriere()
      .pinceDroite(true).pinceGauche(true).tiroirHaut(true)
      .solDroite(true).solGauche(true).tiroirBas(true);

    ConstructionPlanResult planResult = planner.plan(big, Face.ARRIERE);
    List<ConstructionAction> actions = planResult.actions();
    Assertions.assertEquals(6, actions.size());
    Assertions.assertEquals(24, planResult.newArea().score());
    Assertions.assertEquals(24, planResult.newArea().score() - big.score());

    ConstructionMoveAction moveAction;
    ConstructionFloorAction floorAction;

    Assertions.assertInstanceOf(ConstructionMoveAction.class, actions.get(0));
    moveAction = (ConstructionMoveAction) actions.get(0);
    Assertions.assertEquals(ConstructionActionType.MOVE, moveAction.type());
    Assertions.assertEquals(Face.ARRIERE, moveAction.face());
    Assertions.assertEquals(Rang.RANG_1, moveAction.rang());

    Assertions.assertInstanceOf(ConstructionFloorAction.class, actions.get(1));
    floorAction = (ConstructionFloorAction) actions.get(1);
    Assertions.assertEquals(ConstructionActionType.PLACE_ONE, floorAction.type());
    Assertions.assertEquals(Face.ARRIERE, floorAction.face());
    Assertions.assertEquals(Etage.ETAGE_1, floorAction.etage());
    Assertions.assertEquals(StockPosition.TOP, floorAction.stockPosition());

    Assertions.assertInstanceOf(ConstructionFloorAction.class, actions.get(1));
    floorAction = (ConstructionFloorAction) actions.get(2);
    Assertions.assertEquals(ConstructionActionType.PLACE_ONE, floorAction.type());
    Assertions.assertEquals(Face.ARRIERE, floorAction.face());
    Assertions.assertEquals(Etage.ETAGE_2, floorAction.etage());
    Assertions.assertEquals(StockPosition.BOTTOM, floorAction.stockPosition());

    Assertions.assertInstanceOf(ConstructionMoveAction.class, actions.get(3));
    moveAction = (ConstructionMoveAction) actions.get(3);
    Assertions.assertEquals(ConstructionActionType.MOVE, moveAction.type());
    Assertions.assertEquals(Face.AVANT, moveAction.face());
    Assertions.assertEquals(Rang.RANG_2, moveAction.rang());

    Assertions.assertInstanceOf(ConstructionFloorAction.class, actions.get(4));
    floorAction = (ConstructionFloorAction) actions.get(4);
    Assertions.assertEquals(ConstructionActionType.PLACE_ONE, floorAction.type());
    Assertions.assertEquals(Face.AVANT, floorAction.face());
    Assertions.assertEquals(Etage.ETAGE_1, floorAction.etage());
    Assertions.assertEquals(StockPosition.TOP, floorAction.stockPosition());

    Assertions.assertInstanceOf(ConstructionFloorAction.class, actions.get(5));
    floorAction = (ConstructionFloorAction) actions.get(5);
    Assertions.assertEquals(ConstructionActionType.PLACE_ONE, floorAction.type());
    Assertions.assertEquals(Face.AVANT, floorAction.face());
    Assertions.assertEquals(Etage.ETAGE_2, floorAction.etage());
    Assertions.assertEquals(StockPosition.BOTTOM, floorAction.stockPosition());
  }
}

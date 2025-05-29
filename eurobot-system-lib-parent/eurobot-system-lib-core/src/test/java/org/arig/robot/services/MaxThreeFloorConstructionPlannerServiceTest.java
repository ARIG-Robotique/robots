package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.Construction2FloorAction;
import org.arig.robot.model.ConstructionAction;
import org.arig.robot.model.ConstructionActionType;
import org.arig.robot.model.ConstructionArea;
import org.arig.robot.model.ConstructionFloorAction;
import org.arig.robot.model.ConstructionMoveAction;
import org.arig.robot.model.ConstructionPlanResult;
import org.arig.robot.model.ConstructionTake2Action;
import org.arig.robot.model.Etage;
import org.arig.robot.model.EurobotStatus;
import org.arig.robot.model.Face;
import org.arig.robot.model.Rang;
import org.arig.robot.model.StockPosition;
import org.arig.robot.model.TestEurobotStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

@Slf4j
public class MaxThreeFloorConstructionPlannerServiceTest {

  private final EurobotStatus rs = new TestEurobotStatus(true);
  private final ConstructionPlannerService planner = new ConstructionPlannerService(rs);

  private final ConstructionArea small = new ConstructionArea("1 rang");
  private final ConstructionArea big = new ConstructionArea("3 rangs", (byte) 3);

  @BeforeEach
  void setUp() {
    rs.limiter2Etages(false);
    rs.faceAvant().clear();
    rs.faceArriere().clear();
    small.clean();
    big.clean();
  }

  @Test
  void testPlanEmptyFaces() {
    for (ConstructionArea area : List.of(small, big)) {
      ConstructionPlanResult planResult = planner.plan(area);
      Assertions.assertEquals(0, planResult.actions().size());
      Assertions.assertEquals(0, planResult.newArea().score());
      Assertions.assertEquals(0, planResult.newArea().score() - area.score());
    }
  }

  @Test
  void testPlanFaceAvantOnly() {
    rs.faceAvant()
      .pinceDroite(true).pinceGauche(true).tiroirHaut(true)
      .solDroite(true).solGauche(true).tiroirBas(true);

    for (ConstructionArea area : List.of(small, big)) {
      ConstructionPlanResult planResult = planner.plan(area);
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

      Assertions.assertInstanceOf(ConstructionFloorAction.class, actions.get(2));
      floorAction = (ConstructionFloorAction) actions.get(2);
      Assertions.assertEquals(ConstructionActionType.PLACE_ONE, floorAction.type());
      Assertions.assertEquals(Face.AVANT, floorAction.face());
      Assertions.assertEquals(Etage.ETAGE_2, floorAction.etage());
      Assertions.assertEquals(StockPosition.BOTTOM, floorAction.stockPosition());
    }
  }

  @Test
  void testPlanFaceArriereOnly() {
    rs.faceArriere()
      .pinceDroite(true).pinceGauche(true).tiroirHaut(true)
      .solDroite(true).solGauche(true).tiroirBas(true);

    for (ConstructionArea area : List.of(small, big)) {
      ConstructionPlanResult planResult = planner.plan(area);
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

      Assertions.assertInstanceOf(ConstructionFloorAction.class, actions.get(2));
      floorAction = (ConstructionFloorAction) actions.get(2);
      Assertions.assertEquals(ConstructionActionType.PLACE_ONE, floorAction.type());
      Assertions.assertEquals(Face.ARRIERE, floorAction.face());
      Assertions.assertEquals(Etage.ETAGE_2, floorAction.etage());
      Assertions.assertEquals(StockPosition.BOTTOM, floorAction.stockPosition());
    }
  }

  @Test
  void testPlanFacesFullBigAreaEmpty() {
    rs.faceAvant()
      .pinceDroite(true).pinceGauche(true).tiroirHaut(true)
      .solDroite(true).solGauche(true).tiroirBas(true);
    rs.faceArriere()
      .pinceDroite(true).pinceGauche(true).tiroirHaut(true)
      .solDroite(true).solGauche(true).tiroirBas(true);

    ConstructionPlanResult planResult = planner.plan(big);
    List<ConstructionAction> actions = planResult.actions();
    Assertions.assertEquals(10, actions.size());
    Assertions.assertEquals(32, planResult.newArea().score());
    Assertions.assertEquals(32, planResult.newArea().score() - big.score());

    ConstructionMoveAction moveAction;
    ConstructionFloorAction floorAction;
    Construction2FloorAction twoFloorAction;
    ConstructionTake2Action take2Action;

    Assertions.assertInstanceOf(ConstructionMoveAction.class, actions.get(0));
    moveAction = (ConstructionMoveAction) actions.get(0);
    Assertions.assertEquals(ConstructionActionType.MOVE, moveAction.type());
    Assertions.assertEquals(Face.AVANT, moveAction.face());
    Assertions.assertEquals(Rang.RANG_1, moveAction.rang());

    Assertions.assertInstanceOf(ConstructionFloorAction.class, actions.get(1));
    floorAction = (ConstructionFloorAction) actions.get(1);
    Assertions.assertEquals(ConstructionActionType.PLACE_ONE, floorAction.type());
    Assertions.assertEquals(Face.AVANT, floorAction.face());
    Assertions.assertEquals(Rang.RANG_1, floorAction.rang());
    Assertions.assertEquals(Etage.ETAGE_1, floorAction.etage());
    Assertions.assertEquals(StockPosition.TOP, floorAction.stockPosition());

    Assertions.assertInstanceOf(ConstructionMoveAction.class, actions.get(2));
    moveAction = (ConstructionMoveAction) actions.get(2);
    Assertions.assertEquals(ConstructionActionType.MOVE, moveAction.type());
    Assertions.assertEquals(Face.ARRIERE, moveAction.face());
    Assertions.assertEquals(Rang.RANG_2, moveAction.rang());

    Assertions.assertInstanceOf(ConstructionFloorAction.class, actions.get(3));
    floorAction = (ConstructionFloorAction) actions.get(3);
    Assertions.assertEquals(ConstructionActionType.PLACE_ONE, floorAction.type());
    Assertions.assertEquals(Face.ARRIERE, floorAction.face());
    Assertions.assertEquals(Rang.RANG_2, floorAction.rang());
    Assertions.assertEquals(Etage.ETAGE_1, floorAction.etage());
    Assertions.assertEquals(StockPosition.TOP, floorAction.stockPosition());

    Assertions.assertInstanceOf(ConstructionFloorAction.class, actions.get(4));
    floorAction = (ConstructionFloorAction) actions.get(4);
    Assertions.assertEquals(ConstructionActionType.PLACE_ONE, floorAction.type());
    Assertions.assertEquals(Face.ARRIERE, floorAction.face());
    Assertions.assertEquals(Rang.RANG_2, floorAction.rang());
    Assertions.assertEquals(Etage.ETAGE_2, floorAction.etage());
    Assertions.assertEquals(StockPosition.BOTTOM, floorAction.stockPosition());

    Assertions.assertInstanceOf(ConstructionTake2Action.class, actions.get(5));
    take2Action = (ConstructionTake2Action) actions.get(5);
    Assertions.assertEquals(ConstructionActionType.TAKE_TWO, take2Action.type());
    Assertions.assertEquals(Face.ARRIERE, take2Action.face());
    Assertions.assertEquals(Rang.RANG_2, take2Action.rang());
    Assertions.assertEquals(Etage.ETAGE_1, take2Action.etage());

    Assertions.assertInstanceOf(ConstructionMoveAction.class, actions.get(6));
    moveAction = (ConstructionMoveAction) actions.get(6);
    Assertions.assertEquals(ConstructionActionType.MOVE, moveAction.type());
    Assertions.assertEquals(Face.ARRIERE, moveAction.face());
    Assertions.assertEquals(Rang.RANG_1, moveAction.rang());

    Assertions.assertInstanceOf(Construction2FloorAction.class, actions.get(7));
    twoFloorAction = (Construction2FloorAction) actions.get(7);
    Assertions.assertEquals(ConstructionActionType.PLACE_TWO, twoFloorAction.type());
    Assertions.assertEquals(Face.ARRIERE, twoFloorAction.face());
    Assertions.assertEquals(Rang.RANG_1, twoFloorAction.rang());
    Assertions.assertEquals(Etage.ETAGE_2, twoFloorAction.etage());

    Assertions.assertInstanceOf(ConstructionMoveAction.class, actions.get(8));
    moveAction = (ConstructionMoveAction) actions.get(8);
    Assertions.assertEquals(ConstructionActionType.MOVE, moveAction.type());
    Assertions.assertEquals(Face.AVANT, moveAction.face());
    Assertions.assertEquals(Rang.RANG_2, moveAction.rang());

    Assertions.assertInstanceOf(ConstructionFloorAction.class, actions.get(9));
    floorAction = (ConstructionFloorAction) actions.get(9);
    Assertions.assertEquals(ConstructionActionType.PLACE_ONE, floorAction.type());
    Assertions.assertEquals(Face.AVANT, floorAction.face());
    Assertions.assertEquals(Rang.RANG_2, floorAction.rang());
    Assertions.assertEquals(Etage.ETAGE_1, floorAction.etage());
    Assertions.assertEquals(StockPosition.BOTTOM_FAST, floorAction.stockPosition());
  }

  @Test
  void testPlanFacesFullBigArea2ndDepose() {
    rs.faceAvant()
      .pinceDroite(true).pinceGauche(true).tiroirHaut(true)
      .solDroite(true).solGauche(true).tiroirBas(true);
    rs.faceArriere()
      .pinceDroite(true).pinceGauche(true).tiroirHaut(true)
      .solDroite(true).solGauche(true).tiroirBas(true);
    big.addGradin(Rang.RANG_1, Etage.ETAGE_1, true);
    big.addGradin(Rang.RANG_1, Etage.ETAGE_2, true);
    big.addGradin(Rang.RANG_1, Etage.ETAGE_3, true);
    big.addGradin(Rang.RANG_2, Etage.ETAGE_1, true);

    ConstructionPlanResult planResult = planner.plan(big);
    List<ConstructionAction> actions = planResult.actions();
    Assertions.assertEquals(9, actions.size());
    Assertions.assertEquals(68, planResult.newArea().score());
    Assertions.assertEquals(36, planResult.newArea().score() - big.score());

    ConstructionMoveAction moveAction;
    ConstructionFloorAction floorAction;
    Construction2FloorAction twoFloorAction;
    ConstructionTake2Action take2Action;

    Assertions.assertInstanceOf(ConstructionMoveAction.class, actions.get(0));
    moveAction = (ConstructionMoveAction) actions.get(0);
    Assertions.assertEquals(ConstructionActionType.MOVE, moveAction.type());
    Assertions.assertEquals(Face.AVANT, moveAction.face());
    Assertions.assertEquals(Rang.RANG_3, moveAction.rang());

    Assertions.assertInstanceOf(ConstructionFloorAction.class, actions.get(1));
    floorAction = (ConstructionFloorAction) actions.get(1);
    Assertions.assertEquals(ConstructionActionType.PLACE_ONE, floorAction.type());
    Assertions.assertEquals(Face.AVANT, floorAction.face());
    Assertions.assertEquals(Rang.RANG_3, floorAction.rang());
    Assertions.assertEquals(Etage.ETAGE_1, floorAction.etage());
    Assertions.assertEquals(StockPosition.TOP, floorAction.stockPosition());

    Assertions.assertInstanceOf(ConstructionFloorAction.class, actions.get(2));
    floorAction = (ConstructionFloorAction) actions.get(2);
    Assertions.assertEquals(ConstructionActionType.PLACE_ONE, floorAction.type());
    Assertions.assertEquals(Face.AVANT, floorAction.face());
    Assertions.assertEquals(Rang.RANG_3, floorAction.rang());
    Assertions.assertEquals(Etage.ETAGE_2, floorAction.etage());
    Assertions.assertEquals(StockPosition.BOTTOM, floorAction.stockPosition());

    Assertions.assertInstanceOf(ConstructionTake2Action.class, actions.get(3));
    take2Action = (ConstructionTake2Action) actions.get(3);
    Assertions.assertEquals(ConstructionActionType.TAKE_TWO, take2Action.type());
    Assertions.assertEquals(Face.AVANT, take2Action.face());
    Assertions.assertEquals(Rang.RANG_3, take2Action.rang());
    Assertions.assertEquals(Etage.ETAGE_1, take2Action.etage());

    Assertions.assertInstanceOf(ConstructionMoveAction.class, actions.get(4));
    moveAction = (ConstructionMoveAction) actions.get(4);
    Assertions.assertEquals(ConstructionActionType.MOVE, moveAction.type());
    Assertions.assertEquals(Face.AVANT, moveAction.face());
    Assertions.assertEquals(Rang.RANG_2, moveAction.rang());

    Assertions.assertInstanceOf(Construction2FloorAction.class, actions.get(5));
    twoFloorAction = (Construction2FloorAction) actions.get(5);
    Assertions.assertEquals(ConstructionActionType.PLACE_TWO, twoFloorAction.type());
    Assertions.assertEquals(Face.AVANT, twoFloorAction.face());
    Assertions.assertEquals(Rang.RANG_2, twoFloorAction.rang());
    Assertions.assertEquals(Etage.ETAGE_2, twoFloorAction.etage());

    Assertions.assertInstanceOf(ConstructionMoveAction.class, actions.get(6));
    moveAction = (ConstructionMoveAction) actions.get(6);
    Assertions.assertEquals(ConstructionActionType.MOVE, moveAction.type());
    Assertions.assertEquals(Face.ARRIERE, moveAction.face());
    Assertions.assertEquals(Rang.RANG_3, moveAction.rang());

    Assertions.assertInstanceOf(ConstructionFloorAction.class, actions.get(7));
    floorAction = (ConstructionFloorAction) actions.get(7);
    Assertions.assertEquals(ConstructionActionType.PLACE_ONE, floorAction.type());
    Assertions.assertEquals(Face.ARRIERE, floorAction.face());
    Assertions.assertEquals(Rang.RANG_3, floorAction.rang());
    Assertions.assertEquals(Etage.ETAGE_1, floorAction.etage());
    Assertions.assertEquals(StockPosition.TOP, floorAction.stockPosition());

    Assertions.assertInstanceOf(ConstructionFloorAction.class, actions.get(8));
    floorAction = (ConstructionFloorAction) actions.get(8);
    Assertions.assertEquals(ConstructionActionType.PLACE_ONE, floorAction.type());
    Assertions.assertEquals(Face.ARRIERE, floorAction.face());
    Assertions.assertEquals(Rang.RANG_3, floorAction.rang());
    Assertions.assertEquals(Etage.ETAGE_2, floorAction.etage());
    Assertions.assertEquals(StockPosition.BOTTOM, floorAction.stockPosition());
  }
}

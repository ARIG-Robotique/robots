package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.system.servos.AbstractServos;

@Slf4j
public abstract class AbstractCommonPamiServosService extends AbstractServosService {

  public static final String HAND = "Hand";

  protected static final String POS_FERME = "Fermé";
  protected static final String POS_OUVERT_1 = "Ouvert 1";
  protected static final String POS_OUVERT_2 = "Ouvert 2";

  protected AbstractCommonPamiServosService(AbstractServos servoDevice, AbstractServos... servoDevices) {
    super(servoDevice, servoDevices);
  }

  /* **************************************** */
  /* Méthode pour le positionnement d'origine */
  /* **************************************** */

  public void homes(boolean endMatch) {
    handFerme(false);
  }

  //*******************************************//
  //* Déplacements de groupe                  *//
  //*******************************************//

  //*******************************************//
  //* Déplacements de servo                   *//
  //*******************************************//

  public void handFerme(boolean wait) {
    setPosition(HAND, POS_FERME, wait);
  }

  public void handOuvert1(boolean wait) {
    setPosition(HAND, POS_OUVERT_1, wait);
  }

  public boolean isOuvert1() {
    return isInPosition(HAND, POS_OUVERT_1);
  }

  public void handOuvert2(boolean wait) {
    setPosition(HAND, POS_OUVERT_2, wait);
  }
}

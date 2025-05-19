package org.arig.robot.system.vacuum;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractARIGVacuumController {

  protected static final int NB_PUMPS = 4;

  protected final VacuumPumpState[] states = {
    VacuumPumpState.DISABLED,
    VacuumPumpState.DISABLED,
    VacuumPumpState.DISABLED,
    VacuumPumpState.DISABLED
  };

  protected final VacuumPumpData[] pumpData = {
    new VacuumPumpData(),
    new VacuumPumpData(),
    new VacuumPumpData(),
    new VacuumPumpData()
  };

  public void reset() {
    disableAll();
  }

  public abstract void readAllValues();

  public abstract void readData(byte pompeNb);

  public VacuumPumpData getData(int pompeNb) {
    if (checkPompe(pompeNb)) {
      return pumpData[pompeNb - 1];
    }

    log.warn("Valeur par défaut pour la pompe {}", pompeNb);
    return new VacuumPumpData();
  }

  /**
   * Passe toute la gestion du vide en `DISABLED`.
   * Voir #disable(byte)
   */
  public void disableAll() {
    for (int idx = 0; idx < NB_PUMPS; idx++) {
      states[idx] = VacuumPumpState.DISABLED;
    }

    sendToController();
  }

  public void onAll() {
    for (int idx = 0; idx < NB_PUMPS; idx++) {
      states[idx] = VacuumPumpState.ON;
    }

    sendToController();
  }

  public void forceOnAll() {
    for (int idx = 0; idx < NB_PUMPS; idx++) {
      states[idx] = VacuumPumpState.ON_FORCE;
    }

    sendToController();
  }

  public void offAll() {
    for (int idx = 0; idx < NB_PUMPS; idx++) {
      states[idx] = VacuumPumpState.OFF;
    }

    sendToController();
  }

  /**
   * Passage à l'état `DISABLED` d'une gestion de vide.
   * Etat de haute impédance pour l'electrovanne et la pome à vide.
   * <p>
   * La lecture des IO (ADC, TOR) est inactive.
   *
   * @param pompesNb Numéro de pompe a piloter
   */
  public void disable(int... pompesNb) {
    changeState(VacuumPumpState.DISABLED, pompesNb);
  }

  /**
   * La gestion du vide pour le circuit `pompeNb` est actif. Le capteur TOR déclenche la prise.
   * La conversion analogique / numérique est en marche et l'état de présence sera calculé.
   *
   * @param pompesNb Numéro de pompe a piloter
   */
  public void on(int... pompesNb) {
    changeState(VacuumPumpState.ON, pompesNb);
  }

  /**
   * La gestion du vide pour le circuit `pompeNb` est actif. Le capteur TOR ne déclenche pas la prise, la pompe à
   * vide est active tous le temps.
   * La conversion analogique / numérique est en marche et l'état de présence sera calculé.
   *
   * @param pompesNb Numéro de pompe a piloter
   */
  public void onForce(int... pompesNb) {
    changeState(VacuumPumpState.ON_FORCE, pompesNb);
  }

  /**
   * La gestion du vide pour le circuit `pompeNb` est inactif.
   * Lors du changement d'état vers ce mode l'électrovanne est ouverte afin d'injecter de l'air dans le circuit.
   *
   * @param pompesNb Numéro de pompe a piloter
   */
  public void off(int... pompesNb) {
    changeState(VacuumPumpState.OFF, pompesNb);
  }

  public abstract void printVersion();

  protected abstract void sendToController();

  protected abstract byte[] readFromController(byte register, int size);

  protected boolean checkPompe(final int pompeNb) {
    final boolean result = pompeNb >= 1 && pompeNb <= NB_PUMPS;
    if (!result) {
      log.warn("Numéro de pompe invalide : {}", pompeNb);
    }
    return result;
  }

  private void changeState(VacuumPumpState newState, int... pompesNb) {
    boolean hasChanged = false;
    for (int pompeNb : pompesNb) {
      if (checkPompe(pompeNb)) {
        log.info("Modification de la pompe {} -> {}", pompeNb, newState.name());
        if (states[pompeNb - 1] != newState) {
          states[pompeNb - 1] = newState;
          hasChanged = true;
        }
      }
    }

    if (hasChanged) {
      sendToController();
    }
  }
}

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

    protected final VacuumPumpData [] pumpData = {
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
        for (int idx = 0 ; idx < NB_PUMPS ; idx++) {
            states[idx] = VacuumPumpState.DISABLED;
        }

        sendToController();
    }

    public void onAll() {
        for (int idx = 0 ; idx < NB_PUMPS ; idx++) {
            states[idx] = VacuumPumpState.ON;
        }

        sendToController();
    }

    public void offAll() {
        for (int idx = 0 ; idx < NB_PUMPS ; idx++) {
            states[idx] = VacuumPumpState.OFF;
        }

        sendToController();
    }

    /**
     * Passage à l'état `DISABLED` d'une gestion de vide.
     * Etat de haute impédance pour l'electrovanne et la pome à vide.
     *
     * La lecture des IO (ADC, TOR) est inactive.
     * @param pompeNb Numéro de pompe a piloter
     */
    public void disable(int pompeNb) {
        if (checkPompe(pompeNb)) {
            log.info("Désactivation de la pompe {}", pompeNb);
            if (states[pompeNb - 1] != VacuumPumpState.DISABLED) {
                states[pompeNb - 1] = VacuumPumpState.DISABLED;
                sendToController();
            }
        }
    }

    /**
     * La gestion du vide pour le circuit `pompeNb` est actif. Le capteur TOR déclenche la prise.
     * La conversion analogique / numérique est en marche et l'état de présence sera calculé.
     * @param pompeNb Numéro de pompe a piloter
     */
    public void on(int pompeNb) {
        if (checkPompe(pompeNb)) {
            log.info("Activation de la pompe {}", pompeNb);
            if (states[pompeNb - 1] != VacuumPumpState.ON) {
                states[pompeNb - 1] = VacuumPumpState.ON;
                sendToController();
            }
        }
    }

    /**
     * La gestion du vide pour le circuit `pompeNb` est inactif.
     * Lors du changement d'état vers ce mode l'électrovanne est ouverte afin d'injecter de l'air dans le circuit.
     * @param pompeNb Numéro de pompe a piloter
     */
    public void off(int pompeNb) {
        if (checkPompe(pompeNb)) {
            log.info("Désactivation de la pompe {}", pompeNb);
            if (states[pompeNb - 1] != VacuumPumpState.OFF) {
                states[pompeNb - 1] = VacuumPumpState.OFF;
                sendToController();
            }
        }
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

}

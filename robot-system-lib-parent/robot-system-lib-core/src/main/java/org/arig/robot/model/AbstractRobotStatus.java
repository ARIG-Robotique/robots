package org.arig.robot.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@Data
@Accessors(fluent = true)
public abstract class AbstractRobotStatus<T extends Enum<T>> {

    private final int matchTimeMs;

    private final Class<T> journalEventEnum;

    @Getter
    protected final List<EventLog<T>> journal = Collections.synchronizedList(new ArrayList<>());

    public AbstractRobotStatus(final int matchTimeMs, Class<T> journalEventEnum) {
        this.matchTimeMs = matchTimeMs;
        this.journalEventEnum = journalEventEnum;
    }

    public void clearJournal() {
        journal.clear();
    }

    private boolean simulateur = false;

    @Setter(AccessLevel.NONE)
    private boolean forceMonitoring = false;

    public void enableForceMonitoring() {
        log.warn("Activation du monitoring en dehors du match");
        forceMonitoring = true;
    }

    public void disableForceMonitoring() {
        log.warn("Desactivation du monitoring en dehors du match");
        forceMonitoring = false;
    }

    @Setter(AccessLevel.NONE)
    private boolean asservEnabled = false;

    public void enableAsserv() {
        log.info("Activation asservissement");
        asservEnabled = true;
    }

    public void disableAsserv() {
        log.info("Désactivation asservissement");
        asservEnabled = false;
    }

    @Setter(AccessLevel.NONE)
    private boolean captureEnabled = false;

    public void enableCapture() {
        log.info("Activation capture");
        captureEnabled = true;
    }

    public void disableCapture() {
        log.info("Désactivation capture");
        captureEnabled = false;
    }

    @Setter(AccessLevel.NONE)
    private boolean avoidanceEnabled = false;

    public void enableAvoidance() {
        log.info("Activation evittement");
        avoidanceEnabled = true;
    }

    public void disableAvoidance() {
        log.info("Désactivation evittement");
        avoidanceEnabled = false;
    }

    @Setter(AccessLevel.NONE)
    private boolean matchEnabled = false;

    public void enableMatch() {
        matchEnabled = true;
    }

    public void disableMatch() {
        matchEnabled = false;
    }

    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private StopWatch matchTime = new StopWatch();

    public void startMatch() {
        matchTime.start();

        log.info("Démarrage du match");

        // Activation
        this.enableMatch();
    }

    public void stopMatch() {
        matchTime.stop();

        // Arrêt de l'asservissement et des moteurs, et tout et tout
        this.disableAsserv();
        this.disableAvoidance();
        this.disableMatch();
        this.disableCalageBordure();
    }

    public boolean matchRunning() {
        return getElapsedTime() < matchTimeMs;
    }

    public long getElapsedTime() {
        return matchTime.getTime();
    }

    public long getRemainingTime() {
        return Math.max(0, matchTimeMs - getElapsedTime());
    }

    @Setter(AccessLevel.NONE)
    private boolean calageBordure = false;

    public void enableCalageBordure() {
        log.info("Activation calage bordure");
        calageBordure = true;
    }

    public void disableCalageBordure() {
        log.info("Désactivation calage bordure");
        calageBordure = false;
    }

    private String currentAction = null;

    public abstract int calculerPoints();

    public abstract Map<String, ?> gameStatus();

    public abstract Map<String, Integer> scoreStatus();

    public abstract void writeStatus(ObjectOutputStream os) throws IOException;

    public abstract void readStatus(ObjectInputStream is) throws IOException;

    public abstract void integrateJournal(List<EventLog<T>> journal, boolean self);

    public byte[] serializeStatus() {
        try (
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos);
        ) {
            writeStatus(oos);
            return baos.toByteArray();
        } catch (IOException e) {
            log.warn(e.getMessage());
            return null;
        }
    }

    public void deserializeStatus(byte[] data) {
        try (
                ByteArrayInputStream bais = new ByteArrayInputStream(data);
                ObjectInputStream ois = new ObjectInputStream(bais);
        ) {
            readStatus(ois);

            // nouveaux event depuis qu'on a envoyé le journal à l'autre robot
            if (!journal.isEmpty()) {
                integrateJournal(journal, true);
            }
        } catch (IOException e) {
            log.warn(e.getMessage());
        }
    }

    public byte[] serializeJournal() {
        try (
                final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                final ObjectOutputStream oos = new ObjectOutputStream(baos);
        ) {
            final List<EventLog<T>> journalCopy = new ArrayList<>(journal);
            journal.clear();

            oos.writeByte(journalCopy.size());
            for (EventLog<?> event : journalCopy) {
                oos.writeByte(event.getEvent().ordinal());
                oos.writeByte(event.getValue());
            }

            return baos.toByteArray();
        } catch (IOException e) {
            log.warn(e.getMessage());
            return null;
        }
    }

    public void deserializeJournal(final byte[] data) {
        try (
                final ByteArrayInputStream bais = new ByteArrayInputStream(data);
                final ObjectInputStream ois = new ObjectInputStream(bais);
        ) {
            final List<EventLog<T>> journal = new ArrayList<>();

            byte length = ois.readByte();
            for (byte i = 0; i < length; i++) {
                byte event = ois.readByte();
                byte value = ois.readByte();

                journal.add(new EventLog<>(journalEventEnum.getEnumConstants()[event], value));
            }

            integrateJournal(journal, false);
        } catch (IOException e) {
            log.warn(e.getMessage());
        }
    }

}

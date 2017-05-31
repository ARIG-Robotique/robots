package org.arig.robot.system.process;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.function.Consumer;

/**
 * Classe permettant de redirigé le flux en provenance d'un process externe dans un logger.
 *
 * @author gdepuille on 11/04/17.
 */
class StreamGobbler implements Runnable {

    private InputStream inputStream;
    private Consumer<String> consumeInputLine;

    public StreamGobbler(final InputStream inputStream, final Consumer<String> consumeInputLine) {
        this.inputStream = inputStream;
        this.consumeInputLine = consumeInputLine;
    }

    public void run() {
        new BufferedReader(new InputStreamReader(inputStream)).lines().forEach(consumeInputLine);
    }
}

package org.arig.robot.clr;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.Ordonanceur;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrdonanceurRunner implements CommandLineRunner {

    @Override
    public void run(final String... args) throws Exception {
        new Thread(() -> {
            try {
                //Ordonanceur.getInstance().run();
            } catch (Exception e) {
                log.error("Problème d'ordonancement", e);
            }
        }).start();
    }
}

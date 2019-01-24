package org.arig.robot.clr;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.Ordonanceur;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class StartUpRunner implements CommandLineRunner {

    private Ordonanceur ordonanceur;

    public StartUpRunner(Ordonanceur ordonanceur) {
        this.ordonanceur = ordonanceur;
    }

    @Override
    public void run(final String... args) throws Exception {
        log.info("Démarrage de l'ordonanceur");
        ordonanceur.run();
    }
}

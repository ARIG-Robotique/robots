package org.arig.robot.clr;

import org.arig.robot.Ordonanceur;
import org.arig.robot.model.RobotStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class SimulateurRunner implements CommandLineRunner {

    @Autowired
    private RobotStatus rs;

    @Override
    public void run(final String... args) throws Exception {
        rs.setSimulateur();

        Ordonanceur.getInstance().run();
    }
}

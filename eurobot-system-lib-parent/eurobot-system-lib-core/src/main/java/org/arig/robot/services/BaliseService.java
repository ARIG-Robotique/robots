package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.EurobotStatus;
import org.arig.robot.model.balise.StatutBalise;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class BaliseService extends AbstractBaliseService<StatutBalise> {

    @Autowired
    private EurobotStatus rs;

    private StatutBalise statut;

    public void updateStatus() {
        statut = balise.getStatut();

        if (statut != null && statut.detectionOk()) {
            updatePanneauxSolaire();
            updatePlantes();
        }
    }

    private void updatePanneauxSolaire() {

    }

    private void updatePlantes() {

    }
}

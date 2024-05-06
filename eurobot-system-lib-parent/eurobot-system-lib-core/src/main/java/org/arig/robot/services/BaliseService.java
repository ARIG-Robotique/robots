package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.communication.socket.balise.DataQueryData;
import org.arig.robot.communication.socket.balise.DataResponse;
import org.arig.robot.model.CouleurPanneauSolaire;
import org.arig.robot.model.EurobotStatus;
import org.arig.robot.model.balise.BaliseData;
import org.arig.robot.model.balise.Data3D;
import org.arig.robot.model.balise.enums.Data3DTeam;
import org.arig.robot.model.balise.enums.Data3DType;
import org.arig.robot.model.balise.enums.FiltreBalise;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Slf4j
public class BaliseService extends AbstractBaliseService<BaliseData> {

    @Autowired
    private EurobotStatus rs;

    private List<Data3D> data3D;

    public void updateData() {
        DataResponse response = (DataResponse) balise.getData(new DataQueryData<>(FiltreBalise.SOLAR_PANEL));

        if (response == null) {
            isOK = false;
            return;
        }

        if (response.isError() || !isOK || response.getData().getData3D() == null) {
            return;
        }

        data3D = response.getData().getData3D();

        if (data3D != null && !data3D.isEmpty()) {
            updatePanneauxSolaire();
            updatePlantesEtPots();
        }
    }

    private void updatePanneauxSolaire() {
        data3D.stream()
            .filter((object) -> object.getType() == Data3DType.SOLAR_PANEL)
            .forEach(solarPanel -> {
                if (solarPanel.getTeam() == Data3DTeam.INCONNUE) return;
                int numero = Integer.parseInt(solarPanel.getName().name().split("_")[2]) + 1;
                CouleurPanneauSolaire couleur = CouleurPanneauSolaire.valueOf(solarPanel.getTeam().name());
                long millis = rs.getElapsedTime() - solarPanel.getAge();
                rs.panneauxSolaire().refreshFromCamera(numero, couleur, millis);
            });
    }

    private void updatePlantesEtPots() {
    }

}

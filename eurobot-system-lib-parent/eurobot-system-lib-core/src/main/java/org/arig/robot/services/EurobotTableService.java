package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.EurobotStatus;
import org.arig.robot.utils.TableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EurobotTableService {

    @Autowired
    private EurobotStatus rs;

    @Autowired
    protected TableUtils tableUtils;

    public Integer estimateBoueeFromPosition() {
        for (int i = 1; i <= 16; i++) {
            if (rs.boueePresente(i)) {
                final double distance = tableUtils.distance(rs.boueePt(i));
                // la distance minimale entre deux bouées est 186mm
                if (distance < 90) {
                    return i;
                }
            }
        }

        return null;
    }

}

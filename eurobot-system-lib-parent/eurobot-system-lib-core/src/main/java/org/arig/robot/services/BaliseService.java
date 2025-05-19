package org.arig.robot.services;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.communication.socket.balise.DataQueryData;
import org.arig.robot.communication.socket.balise.DataResponse;
import org.arig.robot.communication.socket.balise.ZoneQueryData;
import org.arig.robot.communication.socket.balise.ZoneResponse;
import org.arig.robot.model.EurobotStatus;
import org.arig.robot.model.Point;
import org.arig.robot.model.Team;
import org.arig.robot.model.balise.BaliseData;
import org.arig.robot.model.balise.Data3D;
import org.arig.robot.model.balise.enums.Data3DName;
import org.arig.robot.model.balise.enums.Data3DType;
import org.arig.robot.model.balise.enums.FiltreBalise;
import org.arig.robot.model.balise.enums.ZoneMines;
import org.arig.robot.utils.TableUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class BaliseService extends AbstractBaliseService<BaliseData> {

  @Autowired
  private EurobotStatus rs;

  @Autowired
  private TableUtils tableUtils;

  private List<Data3D> data3D;

  public void updateData() {
    DataResponse response = (DataResponse)
      balise.getData(new DataQueryData<>(
          FiltreBalise.ROBOT,
          FiltreBalise.RAW_TRIBUNE
        )
      );

    if (response == null) {
      isOK = false;
      return;
    }

    if (response.isError() || response.getData().getData3D() == null) {
      return;
    }

    data3D = response.getData().getData3D();

    updatePositionAdverse();
    updateStocksRawTribune();
    updateMines();
  }


  private void updateStocksRawTribune() {
        /*
        data3D.stream()
                .filter(object -> object.getType() == Data3DType.RAW_TRIBUNE)
                .filter(plantStock -> plantStock.getMetadata().getNumPlantes() != null
                        && plantStock.getMetadata().getNumPlantes() <= 2)
                .forEach(plantStock -> {
                    RawTribune.ID stockRawTribuneId = Data3DName.getStockPlantesID(plantStock.getName());
                    if (stockRawTribuneId == null) {
                        return;
                    }
                    if (!rs.plantes().stock(stockPlantesID).isEmpty()) {
                        rs.plantes().priseStock(stockPlantesID, StockPlantes.Status.EMPTY);
                    }
                });
         */
  }

  private void updatePositionAdverse() {
    List<Point> positions = data3D.stream()
      .filter(object -> object.getType() == Data3DType.ROBOT)
      .filter(robot -> {
        Team robotTeam = Data3DName.getRobotTeam(robot.getName());
        return robotTeam != null && robotTeam != rs.team()
          && robot.getAge() < 1000
          && tableUtils.isInTable(new Point(robot.getX(), robot.getY()));
      })
      .map(robot -> new Point(robot.getX(), robot.getY()))
      .collect(Collectors.toList());

    rs.adversaryPosition(positions);
  }

  private void updateMines() {
    List<ZoneQueryData.Zone> zones = Arrays.stream(ZoneMines.values())
      .filter(zone -> zone.getTeam() == null || zone.getTeam() == rs.team())
      .map(ZoneMines::toQueryZone)
      .toList();

    ZoneResponse response = balise.getMines(new ZoneQueryData(zones));

    if (response == null) {
      isOK = false;
      return;
    }

    if (response.isError() || response.getData().getZones() == null) {
      return;
    }

    List<ZoneMines> zoneBloquees = response.getData().getZones().stream()
      .map(zoneString -> {
        if (!rs.mines().contains(ZoneMines.valueOf(zoneString))) {
          log.info("[RS] Zone {} miné", zoneString);
        }

        return ZoneMines.valueOf(zoneString);
      })
      .toList();

    rs.mines().forEach(mine -> {
      if (!response.getData().getZones().contains(mine.name())) {
        log.info("[RS] Zone {} plus miné", mine);
      }
    });

    rs.mines(zoneBloquees);
  }
}

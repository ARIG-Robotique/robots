package org.arig.robot.config.spring;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.model.ecran.EcranConfig;
import org.arig.robot.model.ecran.EcranMatchInfo;
import org.arig.robot.model.ecran.EcranParams;
import org.arig.robot.model.ecran.EcranPhoto;
import org.arig.robot.model.ecran.EcranState;
import org.arig.robot.services.PamiEcranService;
import org.arig.robot.system.capteurs.socket.IEcran;
import org.arig.robot.system.process.EcranProcess;
import org.springframework.boot.logging.LogLevel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;

@Slf4j
@Configuration
public class PamiUtilsCommonContext extends PamiCommonContext {



  @Override
  public IEcran<EcranConfig, EcranState> ecran() {
    return new IEcran<>() {
      @Override
      public void end() {

      }

      @Override
      public boolean setParams(EcranParams params) {
        return false;
      }

      @Override
      public EcranConfig configInfos() {
        return new EcranConfig();
      }

      @Override
      public boolean updateState(EcranState data) {
        return true;
      }

      @Override
      public boolean updateMatch(EcranMatchInfo data) {
        return true;
      }

      @Override
      public void updatePhoto(EcranPhoto photo) {

      }
    };
  }

  @Bean
  public PamiEcranService pamiEcranService() {
    return new PamiEcranService() {
      @Override
      public void updateStateInfo(EcranState stateInfos) {
      }

      @Override
      public void process() {
      }

      @Override
      public void displayMessage(String message, LogLevel logLevel) {
        switch (logLevel) {
          case INFO -> log.info(message);
          case WARN -> log.warn(message);
          case ERROR -> log.error(message);
        }
      }
    };
  }
}

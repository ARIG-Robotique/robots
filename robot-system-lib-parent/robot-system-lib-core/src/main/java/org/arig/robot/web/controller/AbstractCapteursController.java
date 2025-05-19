package org.arig.robot.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.ConstantesConfig;
import org.arig.robot.services.AbstractEnergyService;
import org.arig.robot.services.IOService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequestMapping(AbstractCapteursController.ROOT_PATH)
@Profile(ConstantesConfig.profileMonitoring)
public abstract class AbstractCapteursController implements InitializingBean {

  protected static final String ROOT_PATH = "/capteurs";

  @Autowired
  private IOService ioService;

  @Autowired
  private AbstractEnergyService energyService;

  protected final Map<String, BooleanValue> numeriqueInfos = new LinkedHashMap<>();

  protected final Map<String, DoubleValue> analogiqueInfos = new LinkedHashMap<>();

  protected final Map<String, StringValue> textInfos = new LinkedHashMap<>();

  protected final Map<String, StringValue> couleursInfos = new LinkedHashMap<>();

  @Override
  public void afterPropertiesSet() throws Exception {
    numeriqueInfos.put("Arret d'urgence", ioService::auOk);
    numeriqueInfos.put("Alim. Servos", energyService::checkServos);
    numeriqueInfos.put("Alim. Moteurs", energyService::checkMoteurs);
    numeriqueInfos.put("Tirette", ioService::tirette);

    analogiqueInfos.put("Tension Servos", energyService::tensionServos);
    analogiqueInfos.put("Tension Moteurs", energyService::tensionMoteurs);
  }

  @GetMapping
  public final Map<String, Map<String, ?>> all() {
    Map<String, Map<String, ?>> all = new HashMap<>();
    all.put("numerique", numerique());
    all.put("analogique", analogique());
    all.put("text", text());
    all.put("couleurs", couleurs());
    return all;
  }

  @GetMapping(value = "/numerique")
  public final Map<String, Boolean> numerique() {
    return extractValue(numeriqueInfos);
  }

  @GetMapping(value = "/analogique")
  public final Map<String, Double> analogique() {
    return extractValue(analogiqueInfos);
  }

  @GetMapping(value = "/text")
  public final Map<String, String> text() {
    return extractValue(textInfos);
  }

  @GetMapping(value = "/couleurs")
  public final Map<String, String> couleurs() {
    return extractValue(couleursInfos);
  }

  private <R> Map<String, R> extractValue(Map<String, ? extends GenericValue<R>> src) {
    return src.entrySet().stream()
      .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().value()));
  }

  @FunctionalInterface
  protected interface GenericValue<T> {
    T value();
  }

  protected interface BooleanValue extends GenericValue<Boolean> {
  }

  protected interface DoubleValue extends GenericValue<Double> {
  }

  protected interface StringValue extends GenericValue<String> {
  }
}

package org.arig.robot.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesConfig;
import org.arig.robot.services.IIOService;
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
@RequestMapping("/capteurs")
@Profile(IConstantesConfig.profileMonitoring)
public abstract class AbstractCapteursController implements InitializingBean {

    @Autowired
    private IIOService ioService;

    protected final Map<String, BooleanValue> numeriqueInfos = new LinkedHashMap<>();

    protected final Map<String, DoubleValue> analogiqueInfos = new LinkedHashMap<>();

    protected final Map<String, StringValue> textInfos = new LinkedHashMap<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        numeriqueInfos.put("Arret d'urgence", ioService::auOk);
        numeriqueInfos.put("Alim. Puissance 5V", ioService::alimPuissance5VOk);
        numeriqueInfos.put("Alim. Puissance 12V", ioService::alimPuissance12VOk);
        numeriqueInfos.put("Tirette", ioService::tirette);
    }

    @GetMapping
    public final Map<String, Map<String, ?>> all() {
        Map<String, Map<String, ?>> all = new HashMap<>();
        all.put("numerique", numerique());
        all.put("analogique", analogique());
        all.put("text", text());
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

    private <R> Map<String, R> extractValue(Map<String, ? extends GenericValue<R>> src) {
        return src.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().value()));
    }

    @FunctionalInterface
    protected interface GenericValue<T> {
        T value();
    }

    protected interface BooleanValue extends GenericValue<Boolean> {}
    protected interface DoubleValue extends GenericValue<Double> {}
    protected interface StringValue extends GenericValue<String> {}
}

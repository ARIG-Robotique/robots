package org.arig.robot.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesConfig;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author gdepuille on 14/10/16.
 */
@Slf4j
@RequestMapping("/capteurs")
@Profile(IConstantesConfig.profileMonitoring)
public abstract class AbstractCapteursController {

    protected abstract Map<String, BooleanValue> numeriqueInfos();
    protected abstract Map<String, DoubleValue> analogiqueInfos();
    protected abstract Map<String, StringValue> textInfos();

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
        return extractValue(numeriqueInfos());
    }

    @GetMapping(value = "/analogique")
    public final Map<String, Double> analogique() {
        return extractValue(analogiqueInfos());
    }

    @GetMapping(value = "/text")
    public final Map<String, String> text() {
        return extractValue(textInfos());
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

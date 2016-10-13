package org.arig.robot.web.controller;

import lombok.extern.slf4j.Slf4j;
import org.arig.robot.constants.IConstantesConfig;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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

    @RequestMapping(method = RequestMethod.GET)
    public final Map<String, Map<String, ?>> all() {
        Map<String, Map<String, ?>> all = new HashMap<>();
        all.put("numerique", numerique());
        all.put("analogique", analogique());
        all.put("text", text());
        return all;
    }

    @RequestMapping(value = "/numerique", method = RequestMethod.GET)
    public final Map<String, Boolean> numerique() {
        return extractValue(numeriqueInfos());
    }

    @RequestMapping(value = "/analogique", method = RequestMethod.GET)
    public final Map<String, Double> analogique() {
        return extractValue(analogiqueInfos());
    }

    @RequestMapping(value = "/text", method = RequestMethod.GET)
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

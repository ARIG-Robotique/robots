package org.arig.robot.system.capteurs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.exception.I2CException;
import org.arig.robot.filters.average.IAverage;
import org.arig.robot.filters.average.PassThroughValueAverage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;

import java.util.concurrent.Future;

/**
 * Classe de gestion des capteurs Sharp IR Analogique GP2D12 au travers de l'I2C ADC de gravitech
 *
 * @author gdepuille on 14/05/17.
 */
@Slf4j
public class GP2D12 {

    public static final int INVALID_VALUE = -1;
    private static final short MAX_RAW_VALUE = 300; // Valeur sur 10 bits (env 18 cm)
    private static final short MIN_RAW_VALUE = 90;  // Valeur sur 10 bits (env 72 cm)

    @Autowired
    private I2CAdcAnalogInput analogReader;

    private final byte capteurId;
    private final String name;

    @Setter
    private IAverage<Integer> avgRaw12Bit = new PassThroughValueAverage<>();
    @Setter
    private IAverage<Integer> avgRaw10Bit = new PassThroughValueAverage<>();
    @Setter
    private IAverage<Double> avgCm = new PassThroughValueAverage<>();

    public GP2D12(final byte capteurId) {
        this(capteurId, "UNKNOWN");
    }

    public GP2D12(final byte capteurId, final String name) {
        this.capteurId = capteurId;
        this.name = name;
    }

    @Async
    public Future<GP2D12Values> readValue() {
        if (log.isDebugEnabled()) {
            log.debug("Lecture du GP2D {} : #{}", name, capteurId);
        }
        final GP2D12Values result = new GP2D12Values();
        result.setRaw12BitValue(INVALID_VALUE);
        result.setRaw10BitValue(INVALID_VALUE);
        result.setMmValue(INVALID_VALUE);
        try {
            int raw12bit = analogReader.readCapteurValue(capteurId);
            int raw10bit = convertTo10BitValue(raw12bit);
            double rawMm = convertToCmFrom10Bit(raw10bit) * 10;
            if (raw10bit >= MIN_RAW_VALUE && raw10bit <= MAX_RAW_VALUE) {
                int avgRaw12Bit = this.avgRaw12Bit.filter(raw12bit);
                int avgRaw10Bit = this.avgRaw10Bit.filter(raw10bit);
                double avgMm = this.avgCm.filter(rawMm);
                result.setRaw12BitValue(avgRaw12Bit);
                result.setRaw10BitValue(avgRaw10Bit);
                result.setMmValue(avgMm);
            }
        } catch (I2CException e) {
            log.error("Erreur de lecture du GP2D {} : #{} - {}", name, capteurId, e.toString());
        }

        return new AsyncResult<>(result);
    }

    private int convertTo10BitValue(int value) {
        short min10Bit = 0, min12Bit = 0, max10Bit = 1023, max12Bit = 4095;

        // Une bonne règle de trois des familles
        return (value - min12Bit) * (max10Bit - min10Bit) / (max12Bit - min12Bit) + min12Bit;
    }

    private double convertToCmFrom10Bit(int value) {
        // Conversion en cm
        return (6787.0 / (value - 3.0)) - 4.0; // http://www.acroname.com/robotics/info/articles/irlinear/irlinear.html
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class GP2D12Values {
        private int raw12BitValue;
        private int raw10BitValue;
        private double mmValue;
    }
}

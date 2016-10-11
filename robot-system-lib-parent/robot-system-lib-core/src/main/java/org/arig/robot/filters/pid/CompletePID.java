package org.arig.robot.filters.pid;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.arig.robot.monitoring.IMonitoringWrapper;
import org.influxdb.dto.Point;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.TimeUnit;

/**
 * source http://brettbeauregard.com/blog/2011/04/improving-the-beginners-pid-introduction/
 *
 * Created by gdepuille on 10/03/15.
 */
@Slf4j
public class CompletePID implements IPidFilter {

    @Autowired
    private IMonitoringWrapper monitoringWrapper;

    @Getter
    @Setter(AccessLevel.PRIVATE)
    private double input, output, setPoint, error;

    private double iTerm, lastInput;

    private double kp, ki, kd;

    private int sampleTime = 1000; //1 sec

    private double outMin, outMax;

    private boolean inAuto = false;

    private final String name;

    @Setter
    private PidType controllerDirection = PidType.DIRECT;

    public CompletePID(final String name) {
        outMax = Double.MAX_VALUE;
        outMin = -Double.MAX_VALUE;
        this.name = name;
    }

    public void setMode(PidMode mode) {
        log.info("Set mode {}", mode.name());
        boolean newAuto = (mode == PidMode.AUTOMATIC);
        if(newAuto == !inAuto) {
            /* we just went from manual to auto*/
            initialise();
        }
        inAuto = newAuto;
    }

    @Override
    public void setTunings(double kp, double ki, double kd) {
        if (kp < 0 || ki < 0 || kd < 0) {
            return;
        }

        log.info("Configuration des paramètres PID ( Kp = {} ; Ki = {} ; Kd = {} )", kp, ki, kd);

        double SampleTimeInSec = ((double) sampleTime) / 1000;
        this.kp = kp;
        this.ki = ki * SampleTimeInSec;
        this.kd = kd / SampleTimeInSec;

        if(controllerDirection == PidType.REVERSE) {
            this.kp = (0 - this.kp);
            this.ki = (0 - this.ki);
            this.kd = (0 - this.kd);
        }

        log.info("Paramètres PID réel en fonction du temps ( Kp = {} ; Ki = {} ; Kd = {} )", this.kp, this.ki, this.kd);
    }

    public void initialise() {
        lastInput = input;
        iTerm = output;
        if(iTerm > outMax) iTerm = outMax;
        else if(iTerm < outMin) iTerm = outMin;
    }

    @Override
    public void reset() {
        input = 0;
        error = 0;
        output = 0;
        initialise();
    }

    @Override
    public double compute(double consigne, double mesure) {
        setSetPoint(consigne);
        setInput(mesure);

        if (!inAuto) {
            sendMonitoring();
            return output;
        }

        /* Compute all the working error variables */
        error = setPoint - input;
        iTerm += (ki * error);
        if(iTerm > outMax) iTerm = outMax;
        else if(iTerm < outMin) iTerm = outMin;
        double dInput = (input - lastInput);

        /* Compute PID output */
        output = kp * error + iTerm - kd * dInput;
        if(output > outMax) output = outMax;
        else if(output < outMin) output = outMin;

        /* Remember some variables for next time */
        lastInput = input;

        sendMonitoring();
        return output;
    }

    public void setSampleTime(int newSampleTime) {
        if (newSampleTime > 0) {
            log.info("Configuration du pas temporel {} ms", newSampleTime);
            double ratio  = (double)newSampleTime / (double) sampleTime;
            ki *= ratio;
            kd /= ratio;
            sampleTime = newSampleTime;
        }
    }

    public void setOutputLimits(double min, double max) {
        if(min > max) {
            return;
        }

        log.info("Configuration des limites output. Min = {}, Max = {}", min, max);
        outMin = min;
        outMax = max;

        if(output > outMax) {
            output = outMax;
        } else if(output < outMin) {
            output = outMin;
        }

        if(iTerm > outMax) {
            iTerm = outMax;
        } else if(iTerm < outMin) {
            iTerm = outMin;
        }
    }

    @Override
    public double getErrorSum() {
        return iTerm;
    }

    private void sendMonitoring() {
        // Construction du monitoring
        Point serie = Point.measurement(name)
                .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                .addField("setPoint", getSetPoint())
                .addField("input", getInput())
                .addField("error", getError())
                .addField("errorSum", getErrorSum())
                .addField("output", getOutput())
                .build();

        monitoringWrapper.write(serie);
    }
}

package org.arig.robot.system.capteurs;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mythril on 21/12/13.
 */
public class RaspiBoard2007NoMux extends AbstractBoard2007NoMux<Pin> {

    private final GpioController gpio;

    private Map<Integer, Boolean> values = new HashMap<>();

    public RaspiBoard2007NoMux(GpioController gpio) {
        super();
        this.gpio = gpio;
    }

    @PostConstruct
    public void check() {
        Assert.notNull(gpio, "Le controller GPIO doit être spécifié.");
    }

    @Override
    protected void registerInputCapteur(final int capteurId, Pin pin, boolean pullUp) {
        GpioPinDigitalInput p = gpio.provisionDigitalInputPin(pin, (pullUp) ? PinPullResistance.PULL_UP : PinPullResistance.OFF);

        // Enregistrement de la valeur courante.
        values.put(capteurId, p.getState().isHigh());

        // Ajout d'un listener pour capter les changements d'etats
        p.addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                values.put(capteurId, event.getState().isHigh());
            }
        });
    }

    @Override
    protected boolean readCapteur(final int capteurId, Pin pin) {
        return values.get(capteurId);
    }
}

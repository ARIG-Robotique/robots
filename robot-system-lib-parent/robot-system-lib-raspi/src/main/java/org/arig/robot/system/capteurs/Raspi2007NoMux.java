package org.arig.robot.system.capteurs;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

/**
 * @author gdepuille on 21/12/13.
 */
public class Raspi2007NoMux extends Abstract2007NoMux<Pin> implements InitializingBean {

    private final GpioController gpio;

    private Map<Integer, Boolean> values = new HashMap<>();

    public Raspi2007NoMux(GpioController gpio) {
        super();
        this.gpio = gpio;
    }

    public void afterPropertiesSet() {
        Assert.notNull(gpio, "Le controller GPIO doit être spécifié.");
    }

    @Override
    protected void registerInputCapteur(final int capteurId, Pin pin, boolean pullUp) {
        GpioPinDigitalInput p = gpio.provisionDigitalInputPin(pin, (pullUp) ? PinPullResistance.PULL_UP : PinPullResistance.OFF);

        // Enregistrement de la valeur courante.
        values.put(capteurId, p.getState().isHigh());

        // Ajout d'un listener pour capter les changements d'etats
        final GpioPinListenerDigital listener = (event) -> values.put(capteurId, event.getState().isHigh());
        p.addListener(listener);
    }

    @Override
    protected boolean readCapteur(final int capteurId, Pin pin) {
        return values.get(capteurId);
    }
}

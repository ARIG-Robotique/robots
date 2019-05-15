package org.arig.robot.nerell.utils.shell;

import com.pi4j.gpio.extension.pca.PCA9685GpioProvider;
import com.pi4j.gpio.extension.pca.PCA9685Pin;
import com.pi4j.io.gpio.Pin;
import lombok.AllArgsConstructor;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.Optional;

@ShellComponent
@ShellCommandGroup("PCA9685")
@AllArgsConstructor
public class PCA9685Commands {

    private final PCA9685GpioProvider pca9685GpioProvider;

    @ShellMethod("Test channels PCA9685")
    public void pcaChannel(@NotNull @Min(0) @Max(4095) int value, @NotNull int channel) {
        Optional<Pin> pin = Arrays.stream(PCA9685Pin.ALL)
                .filter(pcaPin -> pcaPin.getAddress() == channel)
                .findFirst();

        pin.ifPresent(p -> pca9685GpioProvider.setPwm(p, 0, value));
    }
}

package org.arig.robot.config.spring;

import org.arig.robot.constants.IConstantesConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@ComponentScan("org.arig.robot.ui")
@Profile(IConstantesConfig.profileUI)
public class JavaFxContext {
}

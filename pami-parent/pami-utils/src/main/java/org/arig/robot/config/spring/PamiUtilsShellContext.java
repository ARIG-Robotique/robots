package org.arig.robot.config.spring;

import org.arig.robot.constants.ConstantesConfig;
import org.arig.robot.pami.utils.PamiShellInputReader;
import org.jline.reader.LineReader;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.shell.jline.PromptProvider;

@Configuration
@ComponentScan("org.arig.robot.pami.utils")
public class PamiUtilsShellContext {

    @Bean
    public PromptProvider myPromptProvider() {
        String pamiId = System.getProperty(ConstantesConfig.keyPamiId);
        return () -> new AttributedString(pamiId + "-utils:> ", AttributedStyle.DEFAULT.background(AttributedStyle.YELLOW));
    }

    @Bean
    public PamiShellInputReader shellInputReader(@Lazy LineReader lineReader) {
        return new PamiShellInputReader(lineReader);
    }
}

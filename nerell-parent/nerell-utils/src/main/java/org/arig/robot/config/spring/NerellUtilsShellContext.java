package org.arig.robot.config.spring;

import org.arig.robot.nerell.utils.shell.ShellInputReader;
import org.jline.reader.LineReader;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.shell.jline.PromptProvider;

@Configuration
@ComponentScan("org.arig.robot.nerell.utils")
public class NerellUtilsShellContext {

    @Bean
    public PromptProvider myPromptProvider() {
        return () -> new AttributedString("nerell-utils:> ", AttributedStyle.DEFAULT.background(AttributedStyle.YELLOW));
    }

    @Bean
    public ShellInputReader shellInputReader(@Lazy LineReader lineReader) {
        return new ShellInputReader(lineReader);
    }
}

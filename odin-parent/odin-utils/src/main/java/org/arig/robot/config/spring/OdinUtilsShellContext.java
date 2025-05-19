package org.arig.robot.config.spring;

import org.arig.robot.odin.utils.OdinShellInputReader;
import org.jline.reader.LineReader;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.shell.jline.PromptProvider;

@Configuration
@ComponentScan("org.arig.robot.odin.utils")
public class OdinUtilsShellContext {

  @Bean
  public PromptProvider myPromptProvider() {
    return () -> new AttributedString("odin-utils:> ", AttributedStyle.DEFAULT.background(AttributedStyle.YELLOW));
  }

  @Bean
  public OdinShellInputReader shellInputReader(@Lazy LineReader lineReader) {
    return new OdinShellInputReader(lineReader);
  }
}

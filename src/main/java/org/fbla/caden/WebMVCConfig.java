package org.fbla.caden;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * needed for spring boot to run
 */

@Configuration
@EnableWebMvc
@ComponentScan

public class WebMVCConfig {

}

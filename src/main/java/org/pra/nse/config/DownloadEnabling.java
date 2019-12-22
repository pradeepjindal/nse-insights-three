package org.pra.nse.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConditionalOnProperty(name = "spring.enable.downloading")
public class DownloadEnabling {
}

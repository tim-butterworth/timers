package com.science.time.timer

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties("server")
class ConfigurationProvider {
    lateinit var url: String
    lateinit var instanceName: String
}

package com.ou.nhahang.dat_ban_nha_hang.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@ConfigurationProperties(prefix = "goong")
@Getter
@Setter
public class GoongConfig {
    private String apiKey;
    private String baseUrl;
}

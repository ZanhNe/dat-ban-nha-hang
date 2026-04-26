package com.ou.nhahang.dat_ban_nha_hang.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "vnpay")
@Data
public class VNPayConfig {
    private String payUrl;
    private String tmnCode;
    private String hashSecret;
    private String returnUrl;
    private String version;
    private String command;
}

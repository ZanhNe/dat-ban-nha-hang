package com.ou.nhahang.dat_ban_nha_hang.service.port;

import java.time.LocalDateTime;
import java.util.Map;

public interface IVNPayGateway {
    String createPaymentUrl(Long amount, Long transactionId, LocalDateTime expireTime, String currency,
            String ipAddress);

    Map<String, String> handleWebhook(Map<String, String> params);
}

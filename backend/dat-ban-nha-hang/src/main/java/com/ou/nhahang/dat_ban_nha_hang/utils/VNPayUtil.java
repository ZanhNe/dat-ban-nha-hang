package com.ou.nhahang.dat_ban_nha_hang.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import jakarta.servlet.http.HttpServletRequest;

public class VNPayUtil {
    public static String hmacSHA512(String key, String data) {
        try {
            Mac sha512Hmac = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            sha512Hmac.init(secretKey);
            byte[] hashBytes = sha512Hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi băm mã VNPay", e);
        }
    }

    public static String hashAllFields(String secretKey, Map<String, String> fields) {
        Map<String, String> sortedFields = new TreeMap<>(fields);

        StringBuilder hashData = new StringBuilder();

        for (Map.Entry<String, String> entry : sortedFields.entrySet()) {
            String fieldName = entry.getKey();
            String fieldValue = entry.getValue();

            // Chỉ lấy các trường có giá trị và không phải là trường SecureHash
            if ((fieldValue != null) && (fieldValue.length() > 0)
                    && !fieldName.equals("vnp_SecureHash")
                    && !fieldName.equals("vnp_SecureHashType")) {

                try {
                    hashData.append(URLEncoder.encode(fieldName, StandardCharsets.UTF_8.toString()));
                    hashData.append('=');
                    hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8.toString()));
                    hashData.append('&');
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException("Lỗi băm mã VNPay", e);
                }
            }
        }

        // 3. Cắt bỏ dấu & cuối cùng
        String result = hashData.toString();
        if (result.endsWith("&")) {
            result = result.substring(0, result.length() - 1);
        }

        // 4. Thực hiện băm HMAC-SHA512 với Secret Key
        return hmacSHA512(secretKey, result);
    }

    public static String getIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty()) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
    }
}
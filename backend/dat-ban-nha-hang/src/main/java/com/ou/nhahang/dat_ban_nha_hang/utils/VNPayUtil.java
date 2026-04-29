package com.ou.nhahang.dat_ban_nha_hang.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
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
        // 1. Dùng TreeMap để tự động sắp xếp key theo bảng chữ cái (Alpha-bet)
        Map<String, String> sortedFields = new TreeMap<>();
        for (Map.Entry<String, String> entry : fields.entrySet()) {
            String fieldName = entry.getKey();
            String fieldValue = entry.getValue();

            // Tuyệt đối không đưa vnp_SecureHash và vnp_SecureHashType vào chuỗi băm
            if (fieldValue != null && fieldValue.length() > 0
                    && !fieldName.equals("vnp_SecureHash")
                    && !fieldName.equals("vnp_SecureHashType")) {
                sortedFields.put(fieldName, fieldValue);
            }
        }

        // 2. Nối chuỗi dữ liệu
        StringBuilder hashData = new StringBuilder();
        Iterator<Map.Entry<String, String>> itr = sortedFields.entrySet().iterator();

        while (itr.hasNext()) {
            Map.Entry<String, String> entry = itr.next();
            try {
                // Encode value theo chuẩn US_ASCII của VNPay
                String encodedValue = URLEncoder.encode(entry.getValue(), StandardCharsets.US_ASCII.toString());

                hashData.append(entry.getKey());
                hashData.append('=');
                hashData.append(encodedValue);

                if (itr.hasNext()) {
                    hashData.append('&');
                }
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("Lỗi mã hóa dữ liệu VNPay", e);
            }
        }
        System.out.println("2. MY HASH DATA: " + hashData.toString());
        // 3. Băm HMAC-SHA512
        return hmacSHA512(secretKey, hashData.toString());
    }

    public static String getIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty()) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
    }
}
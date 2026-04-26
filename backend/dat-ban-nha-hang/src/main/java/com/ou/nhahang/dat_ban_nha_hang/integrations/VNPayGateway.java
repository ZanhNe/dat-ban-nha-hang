package com.ou.nhahang.dat_ban_nha_hang.integrations;

import com.ou.nhahang.dat_ban_nha_hang.config.VNPayConfig;
import com.ou.nhahang.dat_ban_nha_hang.entity.Booking;
import com.ou.nhahang.dat_ban_nha_hang.entity.Payment;
import com.ou.nhahang.dat_ban_nha_hang.entity.Booking.BookingStatus;
import com.ou.nhahang.dat_ban_nha_hang.entity.Transaction;
import com.ou.nhahang.dat_ban_nha_hang.entity.Transaction.TransactionStatus;
// import com.ou.nhahang.dat_ban_nha_hang.exception.ResourceNotFoundException;
import com.ou.nhahang.dat_ban_nha_hang.repository.BookingRepository;
import com.ou.nhahang.dat_ban_nha_hang.repository.PaymentRepository;
import com.ou.nhahang.dat_ban_nha_hang.repository.TransactionRepository;

import java.util.Date;
// import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;

import com.ou.nhahang.dat_ban_nha_hang.service.port.IVNPayGateway;
import com.ou.nhahang.dat_ban_nha_hang.utils.VNPayUtil;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VNPayGateway implements IVNPayGateway {

    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final VNPayConfig vnpayConfig;
    private final TransactionRepository transactionRepository;

    @Override
    public String createPaymentUrl(Long amount, Long transactionId, LocalDateTime expireTime, String currency,
            String ipAddress) {
        String vnp_Version = vnpayConfig.getVersion();
        String vnp_Command = vnpayConfig.getCommand();
        String vnp_TmnCode = vnpayConfig.getTmnCode();
        String vnp_Amount = String.valueOf(amount * 100);
        String vnp_IpAddr = ipAddress;
        String vnp_CurrCode = currency;
        String vnp_ReturnUrl = vnpayConfig.getReturnUrl();

        String vnp_ExpireDate = expireTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        // Tạo mã giao dịch duy nhất
        String vnp_TxnRef = transactionId.toString();

        // Tạo URL params
        Map<String, String> vnpParams = new TreeMap<>();
        vnpParams.put("vnp_Version", vnp_Version);
        vnpParams.put("vnp_Command", vnp_Command);
        vnpParams.put("vnp_TmnCode", vnp_TmnCode);
        vnpParams.put("vnp_Amount", vnp_Amount);
        vnpParams.put("vnp_CreateDate", formatDateTime(new Date()));
        vnpParams.put("vnp_ExpireDate", vnp_ExpireDate);
        vnpParams.put("vnp_CurrCode", vnp_CurrCode);
        vnpParams.put("vnp_IpAddr", vnp_IpAddr);
        vnpParams.put("vnp_Locale", "vn");
        vnpParams.put("vnp_OrderInfo", "Thanh toan cho giao dich: " + transactionId);
        vnpParams.put("vnp_ReturnUrl", vnp_ReturnUrl);
        vnpParams.put("vnp_TxnRef", vnp_TxnRef);

        // Sắp xếp và tạo chữ ký
        String sortedQueryString = buildQueryString(vnpParams);
        String vnp_SecureHash = VNPayUtil.hmacSHA512(vnpayConfig.getHashSecret(), sortedQueryString);
        sortedQueryString += "&vnp_SecureHash=" + vnp_SecureHash;

        return vnpayConfig.getPayUrl() + "?" + sortedQueryString;
    }

    @Override
    @Transactional
    public Map<String, String> handleWebhook(Map<String, String> params) {
        String vnp_SecureHash = params.remove("vnp_SecureHash");
        String signValue = VNPayUtil.hashAllFields(vnpayConfig.getHashSecret(), params); // Hàm băm lại tham số

        if (!vnp_SecureHash.equals(signValue)) {
            return Map.of("RspCode", "97", "Message", "Invalid Checksum");
        }

        // 2. Lấy dữ liệu và KHÓA bản ghi (Pessimistic Lock)
        Long txnId = Long.parseLong(params.get("vnp_TxnRef"));
        Optional<Transaction> txnOptional = transactionRepository.findByIdForUpdate(txnId);
        Booking booking = (Booking) txnOptional.get().getPaymentSource();

        if (txnOptional.isEmpty()) {
            return Map.of("RspCode", "01", "Message", "Order not found");
        }

        Transaction txn = txnOptional.get();

        // 3. Kiểm tra số tiền (VNPay gửi amount * 100)
        long vnpAmount = Long.parseLong(params.get("vnp_Amount")) / 100;
        if (txn.getAmount() != vnpAmount) {
            return Map.of("RspCode", "04", "Message", "Invalid Amount");
        }

        // 4. Kiểm tra trạng thái và cập nhật
        if (txn.getTransactionStatus() != TransactionStatus.PENDING) {
            // Chỗ này cần phải xử lý refund tiền về cho khách (trong thực tế)
            return Map.of("RspCode", "02", "Message", "Order already confirmed");
        }

        String responseCode = params.get("vnp_ResponseCode");
        if ("00".equals(responseCode)) {
            txn.setTransactionStatus(TransactionStatus.AUTHORIZED);
            booking.setStatus(BookingStatus.CONFIRMED);
            Payment payment = Payment.builder()
                    .transaction(txn)
                    .build();
            paymentRepository.save(payment);

        } else {
            txn.setTransactionStatus(TransactionStatus.FAILED);
            booking.setStatus(BookingStatus.FAILED);
            Payment payment = Payment.builder()
                    .paymentStatus(Payment.PaymentStatus.FAILED)
                    .transaction(txn)
                    .build();
            paymentRepository.save(payment);
        }
        transactionRepository.save(txn);
        bookingRepository.save(booking);
        return Map.of("RspCode", "00", "Message", "Confirm Success");

    }

    private String buildQueryString(Map<String, String> params) {
        return params.entrySet().stream()
                .map(e -> {
                    try {
                        return String.format("%s=%s",
                                URLEncoder.encode(e.getKey(), StandardCharsets.US_ASCII.toString()),
                                URLEncoder.encode(e.getValue(), StandardCharsets.US_ASCII.toString()));
                    } catch (Exception ex) {
                        return "";
                    }
                })
                .filter(s -> !s.isEmpty())
                .collect(Collectors.joining("&"));
    }

    private String formatDateTime(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        return formatter.format(date);
    }
}

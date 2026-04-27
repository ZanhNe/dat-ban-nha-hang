package com.ou.nhahang.dat_ban_nha_hang.service;

import com.ou.nhahang.dat_ban_nha_hang.dto.request.CashierCompletePaymentRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.*;
import org.springframework.data.domain.Page;
import com.ou.nhahang.dat_ban_nha_hang.dto.request.CashierGetServedSessionsRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.request.CashierGetPayingSessionsRequestDTO;

public interface ICashierService {
    Page<CashierSessionListResponseDTO> getServedSessions(Long cashierId, CashierGetServedSessionsRequestDTO request);

    Page<CashierSessionListResponseDTO> getPayingSessions(Long cashierId, CashierGetPayingSessionsRequestDTO request);

    CashierSessionDetailResponseDTO getSessionDetailForPayment(Long cashierId, Long sessionId);

    CashierInitiatePaymentResponseDTO initiatePayment(Long cashierId, Long sessionId);

    CashierCompletePaymentResponseDTO completePayment(Long cashierId, Long sessionId,
            CashierCompletePaymentRequestDTO request);
}

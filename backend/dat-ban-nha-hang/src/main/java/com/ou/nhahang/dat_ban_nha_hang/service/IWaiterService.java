package com.ou.nhahang.dat_ban_nha_hang.service;

import com.ou.nhahang.dat_ban_nha_hang.dto.request.WaiterCancelOrderRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.request.WaiterConfirmOrderRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.request.WaiterUpdateFoodItemStatusRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.*;
import org.springframework.data.domain.Page;
import com.ou.nhahang.dat_ban_nha_hang.dto.request.WaiterGetAvailableSessionsRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.request.WaiterGetMySessionsRequestDTO;

public interface IWaiterService {
        Page<WaiterSessionListResponseDTO> getAvailableSessions(Long waiterId,
                        WaiterGetAvailableSessionsRequestDTO request);

        Page<WaiterSessionListResponseDTO> getMyServingSessions(Long waiterId, WaiterGetMySessionsRequestDTO request);

        WaiterAssignSessionResponseDTO assignSession(Long waiterId, Long sessionId);

        WaiterSessionDetailResponseDTO getSessionDetail(Long waiterId, Long sessionId);

        WaiterMenuResponseDTO getMenu(Long waiterId);

        WaiterCreateOrderResponseDTO createFoodOrder(Long waiterId, Long sessionId);

        WaiterFoodOrderDetailResponseDTO getFoodOrderDetail(Long waiterId, Long orderId);

        WaiterConfirmOrderResponseDTO confirmFoodOrder(Long waiterId, Long orderId,
                        WaiterConfirmOrderRequestDTO request);

        WaiterUpdateFoodItemStatusResponseDTO updateFoodItemStatus(Long waiterId, Long itemId,
                        WaiterUpdateFoodItemStatusRequestDTO request);

        WaiterFoodOrderDetailResponseDTO completeFoodOrder(Long waiterId, Long orderId);

        WaiterCancelOrderResponseDTO cancelFoodOrder(Long waiterId, Long orderId, WaiterCancelOrderRequestDTO request);

        WaiterServeCompleteResponseDTO completeServiceSession(Long waiterId, Long sessionId);
}
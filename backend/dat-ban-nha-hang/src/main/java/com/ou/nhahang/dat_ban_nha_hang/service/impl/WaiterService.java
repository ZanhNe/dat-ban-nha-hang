package com.ou.nhahang.dat_ban_nha_hang.service.impl;

import com.ou.nhahang.dat_ban_nha_hang.dto.request.WaiterGetAvailableSessionsRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.request.WaiterGetMySessionsRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.request.WaiterCancelOrderRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.request.WaiterConfirmOrderRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.request.WaiterUpdateFoodItemStatusRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.*;
import com.ou.nhahang.dat_ban_nha_hang.entity.*;
import com.ou.nhahang.dat_ban_nha_hang.exception.BusinessException;
import com.ou.nhahang.dat_ban_nha_hang.exception.ResourceNotFoundException;
import com.ou.nhahang.dat_ban_nha_hang.repository.*;
import com.ou.nhahang.dat_ban_nha_hang.service.IWaiterService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WaiterService implements IWaiterService {

    private final RestaurantTableSessionRepository tableSessionRepository;
    private final FoodOrderRepository foodOrderRepository;
    private final FoodItemRepository foodItemRepository;
    private final FoodDescriptionRepository foodDescriptionRepository;
    private final FoodOptionRepository foodOptionRepository;
    private final MenuRepository menuRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    private Restaurant getWorkplace(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhân viên"));
        if (user.getWorkplace() == null) {
            throw new BusinessException("Nhân viên chưa được phân công nhà hàng");
        }
        return user.getWorkplace();
    }

    private User getWaiter(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhân viên"));
    }

    @Override
    public Page<WaiterSessionListResponseDTO> getAvailableSessions(Long waiterId, WaiterGetAvailableSessionsRequestDTO request) {
        Restaurant workplace = getWorkplace(waiterId);
        RestaurantTableSession.TableSessionStatus sessionStatus;
        try {
            sessionStatus = RestaurantTableSession.TableSessionStatus.valueOf(request.status().toUpperCase());
        } catch (IllegalArgumentException e) {
            sessionStatus = RestaurantTableSession.TableSessionStatus.ACTIVE;
        }

        Pageable pageable = PageRequest.of(request.page(), request.limit());
        Page<RestaurantTableSession> sessions = tableSessionRepository.findByRestaurantIdAndStatus(workplace.getId(),
                sessionStatus, request.unassigned(), pageable);
        
        return sessions.map(session -> WaiterSessionListResponseDTO.builder()
                .sessionId(session.getId())
                .tableLabels(session.getBooking().getTables().stream().map(RestaurantTable::getName).toList())
                .customerName(session.getBooking().getBookingUser().getFullName())
                .numberOfPeople(session.getBooking().getNumberOfPeople().intValue())
                .status(session.getStatus().name())
                .createdAt(session.getCreatedAt())
                .build());
    }

    @Override
    public Page<WaiterSessionListResponseDTO> getMyServingSessions(Long waiterId, WaiterGetMySessionsRequestDTO request) {
        Restaurant workplace = getWorkplace(waiterId);
        Pageable pageable = PageRequest.of(request.page(), request.limit());
        Page<RestaurantTableSession> sessions = tableSessionRepository.findByRestaurantIdAndStatusAndWaiterId(
                workplace.getId(), RestaurantTableSession.TableSessionStatus.SERVING, waiterId, pageable);

        return sessions.map(session -> WaiterSessionListResponseDTO.builder()
                .sessionId(session.getId())
                .tableLabels(session.getBooking().getTables().stream().map(RestaurantTable::getName).toList())
                .customerName(session.getBooking().getBookingUser().getFullName())
                .numberOfPeople(session.getBooking().getNumberOfPeople().intValue())
                .status(session.getStatus().name())
                .createdAt(session.getCreatedAt())
                .build());
    }

    @Override
    @Transactional
    public WaiterAssignSessionResponseDTO assignSession(Long waiterId, Long sessionId) {
        Restaurant workplace = getWorkplace(waiterId);
        RestaurantTableSession session = tableSessionRepository.findByIdAndRestaurantId(sessionId, workplace.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy phiên bàn hoặc phiên bàn không thuộc nhà hàng của bạn"));

        if (session.getStatus() != RestaurantTableSession.TableSessionStatus.ACTIVE) {
            throw new BusinessException("Chỉ có thể nhận phiên bàn ở trạng thái ACTIVE");
        }
        if (session.getWaiter() != null) {
            throw new BusinessException("Phiên bàn đã có người phục vụ");
        }

        User waiter = getWaiter(waiterId);
        session.setWaiter(waiter);
        session.setStatus(RestaurantTableSession.TableSessionStatus.SERVING);
        session.getBooking().setStatus(Booking.BookingStatus.SERVING);

        tableSessionRepository.save(session);
        bookingRepository.save(session.getBooking());

        return WaiterAssignSessionResponseDTO.builder()
                .sessionId(session.getId())
                .status(session.getStatus().name())
                .waiterId(waiterId)
                .build();
    }

    @Override
    public WaiterSessionDetailResponseDTO getSessionDetail(Long waiterId, Long sessionId) {
        Restaurant workplace = getWorkplace(waiterId);
        RestaurantTableSession session = tableSessionRepository.findByIdAndRestaurantId(sessionId, workplace.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy phiên bàn hoặc phiên bàn không thuộc nhà hàng của bạn"));

        return WaiterSessionDetailResponseDTO.builder()
                .sessionId(session.getId())
                .tables(session.getBooking().getTables().stream()
                        .map(table -> WaiterSessionDetailResponseDTO.TableDTO.builder()
                                .tableId(table.getId())
                                .label(table.getName())
                                .build())
                        .toList())
                .numberOfPeople(session.getBooking().getNumberOfPeople().intValue())
                .status(session.getStatus().name())
                .foodOrders(session.getFoodOrders().stream()
                        .map(order -> WaiterSessionDetailResponseDTO.FoodOrderSummaryDTO.builder()
                                .orderId(order.getId())
                                .status(order.getStatus().name())
                                .itemsCount(order.getFoodItems() != null ? order.getFoodItems().size() : 0)
                                .createdAt(order.getCreatedAt())
                                .build())
                        .toList())
                .build();
    }

    @Override
    public WaiterMenuResponseDTO getMenu(Long waiterId) {
        Restaurant workplace = getWorkplace(waiterId);
        List<Menu> menus = menuRepository.findByRestaurantId(workplace.getId());
        if (menus.isEmpty()) {
            throw new ResourceNotFoundException("Không tìm thấy thực đơn của nhà hàng");
        }
        Menu menu = menus.get(0);

        List<WaiterMenuResponseDTO.FoodGroupDTO> foodGroupDTOs = menu.getFoodGroups().stream().map(group -> {
            List<WaiterMenuResponseDTO.FoodDescriptionDTO> foodDTOs = group.getFoodDescriptions().stream().map(food -> {
                List<WaiterMenuResponseDTO.OptionGroupDTO> optionGroupDTOs = food.getOptionGroups().stream()
                        .map(optGroup -> {
                            List<WaiterMenuResponseDTO.OptionDTO> optionDTOs = optGroup.getOptions().stream()
                                    .map(opt -> WaiterMenuResponseDTO.OptionDTO.builder()
                                            .optionId(opt.getId())
                                            .name(opt.getName())
                                            .price(opt.getPrice())
                                            .build())
                                    .toList();

                            return WaiterMenuResponseDTO.OptionGroupDTO.builder()
                                    .optionGroupId(optGroup.getId())
                                    .name(optGroup.getName())
                                    .options(optionDTOs)
                                    .build();
                        }).toList();

                return WaiterMenuResponseDTO.FoodDescriptionDTO.builder()
                        .foodDescriptionId(food.getId())
                        .name(food.getName())
                        .price(food.getPrice())
                        .optionGroups(optionGroupDTOs)
                        .build();
            }).toList();

            return WaiterMenuResponseDTO.FoodGroupDTO.builder()
                    .groupId(group.getId())
                    .name(group.getName())
                    .foods(foodDTOs)
                    .build();
        }).toList();

        return WaiterMenuResponseDTO.builder()
                .menuId(menu.getId())
                .menuName(menu.getName())
                .foodGroups(foodGroupDTOs)
                .build();
    }

    @Override
    @Transactional
    public WaiterCreateOrderResponseDTO createFoodOrder(Long waiterId, Long sessionId) {
        Restaurant workplace = getWorkplace(waiterId);
        RestaurantTableSession session = tableSessionRepository.findByIdAndRestaurantId(sessionId, workplace.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy phiên bàn hoặc phiên bàn không thuộc nhà hàng của bạn"));

        if (session.getWaiter() == null || !session.getWaiter().getId().equals(waiterId)) {
            throw new BusinessException("Bạn không được phân công phục vụ phiên bàn này");
        }

        if (session.getStatus() != RestaurantTableSession.TableSessionStatus.SERVING) {
            throw new BusinessException("Chỉ có thể tạo order cho phiên bàn đang phục vụ (SERVING)");
        }

        FoodOrder order = FoodOrder.builder()
                .tableSession(session)
                .totalPrice(0L)
                .build();

        order = foodOrderRepository.save(order);

        return WaiterCreateOrderResponseDTO.builder()
                .orderId(order.getId())
                .status(order.getStatus().name())
                .build();
    }

    @Override
    public WaiterFoodOrderDetailResponseDTO getFoodOrderDetail(Long waiterId, Long orderId) {
        Restaurant workplace = getWorkplace(waiterId);
        FoodOrder order = foodOrderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy order"));

        if (!order.getTableSession().getBooking().getRestaurant().getId().equals(workplace.getId())) {
            throw new BusinessException("Không có quyền truy cập order của nhà hàng khác");
        }

        return WaiterFoodOrderDetailResponseDTO.builder()
                .orderId(order.getId())
                .status(order.getStatus().name())
                .items(order.getFoodItems().stream()
                        .map(item -> WaiterFoodOrderDetailResponseDTO.FoodItemDetailDTO.builder()
                                .itemId(item.getId())
                                .foodName(item.getFoodDescription().getName())
                                .quantity(item.getQuantity())
                                .selectedOptions(item.getSelectedOptions().stream().map(FoodOption::getName).toList())
                                .status(item.getStatus().name())
                                .build())
                        .toList())
                .build();
    }

    @Override
    @Transactional
    public WaiterConfirmOrderResponseDTO confirmFoodOrder(Long waiterId, Long orderId,
            WaiterConfirmOrderRequestDTO request) {
        Restaurant workplace = getWorkplace(waiterId);
        FoodOrder order = foodOrderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy order"));

        if (order.getTableSession().getWaiter() == null
                || !order.getTableSession().getWaiter().getId().equals(waiterId)) {
            throw new BusinessException("Bạn không được phân công phục vụ order này");
        }

        if (order.getStatus() != FoodOrder.FoodOrderStatus.TAKING_ORDER) {
            throw new BusinessException("Chỉ có thể gửi bếp cho order đang ghi món (TAKING_ORDER)");
        }

        User waiter = getWaiter(waiterId);

        int itemsCount = 0;
        for (WaiterConfirmOrderRequestDTO.FoodItemRequestDTO itemRequest : request.items()) {
            FoodDescription desc = foodDescriptionRepository.findById(itemRequest.foodDescriptionId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Không tìm thấy món ăn ID " + itemRequest.foodDescriptionId()));

            List<FoodOption> options = new ArrayList<>();
            if (itemRequest.optionIds() != null) {
                for (Long optionId : itemRequest.optionIds()) {
                    FoodOption opt = foodOptionRepository.findById(optionId)
                            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tùy chọn ID " + optionId));
                    options.add(opt);
                }
            }

            FoodItem foodItem = FoodItem.builder()
                    .foodOrder(order)
                    .foodDescription(desc)
                    .quantity(itemRequest.quantity())
                    .status(FoodItem.FoodItemStatus.PENDING)
                    .selectedOptions(options)
                    .waiter(waiter)
                    .build();

            foodItemRepository.save(foodItem);
            itemsCount++;
        }

        order.setStatus(FoodOrder.FoodOrderStatus.CONFIRMED);
        order = foodOrderRepository.save(order);

        return WaiterConfirmOrderResponseDTO.builder()
                .orderId(order.getId())
                .status(order.getStatus().name())
                .itemsCount(itemsCount)
                .build();
    }

    @Override
    @Transactional
    public WaiterUpdateFoodItemStatusResponseDTO updateFoodItemStatus(Long waiterId, Long itemId,
            WaiterUpdateFoodItemStatusRequestDTO request) {
        Restaurant workplace = getWorkplace(waiterId);
        FoodItem item = foodItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy món ăn"));

        if (item.getFoodOrder().getTableSession().getWaiter() == null
                || !item.getFoodOrder().getTableSession().getWaiter().getId().equals(waiterId)) {
            throw new BusinessException("Bạn không được phân công phục vụ món này");
        }

        FoodItem.FoodItemStatus newStatus;
        try {
            newStatus = FoodItem.FoodItemStatus.valueOf(request.status().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Trạng thái món ăn không hợp lệ");
        }

        if (newStatus != FoodItem.FoodItemStatus.SERVED && newStatus != FoodItem.FoodItemStatus.CANCELLED) {
            throw new BusinessException("Chỉ được cập nhật trạng thái SERVED hoặc CANCELLED");
        }

        if (item.getStatus() != FoodItem.FoodItemStatus.PENDING) {
            throw new BusinessException("Chỉ có thể cập nhật món ăn đang ở trạng thái PENDING");
        }

        item.setStatus(newStatus);
        foodItemRepository.save(item);

        return WaiterUpdateFoodItemStatusResponseDTO.builder()
                .itemId(item.getId())
                .status(item.getStatus().name())
                .build();
    }

    @Override
    @Transactional
    public WaiterFoodOrderDetailResponseDTO completeFoodOrder(Long waiterId, Long orderId) {
        Restaurant workplace = getWorkplace(waiterId);
        FoodOrder order = foodOrderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy order"));

        if (order.getTableSession().getWaiter() == null
                || !order.getTableSession().getWaiter().getId().equals(waiterId)) {
            throw new BusinessException("Bạn không được phân công phục vụ order này");
        }

        if (order.getStatus() != FoodOrder.FoodOrderStatus.CONFIRMED) {
            throw new BusinessException("Chỉ có thể hoàn tất order đã gửi bếp (CONFIRMED)");
        }

        // Kiểm tra nếu toàn bộ food item đều đã được phục vụ hoặc hủy thì mới cho phép
        boolean allFinished = order.getFoodItems().stream()
                .allMatch(item -> item.getStatus() == FoodItem.FoodItemStatus.SERVED
                        || item.getStatus() == FoodItem.FoodItemStatus.CANCELLED);

        if (!allFinished) {
            throw new BusinessException("Chưa hoàn tất phục vụ tất cả các món ăn trong order");
        }

        order.setStatus(FoodOrder.FoodOrderStatus.COMPLETED);
        foodOrderRepository.save(order);

        return getFoodOrderDetail(waiterId, orderId);
    }

    @Override
    @Transactional
    public WaiterCancelOrderResponseDTO cancelFoodOrder(Long waiterId, Long orderId,
            WaiterCancelOrderRequestDTO request) {
        Restaurant workplace = getWorkplace(waiterId);
        FoodOrder order = foodOrderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy order"));

        if (order.getTableSession().getWaiter() == null
                || !order.getTableSession().getWaiter().getId().equals(waiterId)) {
            throw new BusinessException("Bạn không được phân công phục vụ order này");
        }

        if (order.getStatus() == FoodOrder.FoodOrderStatus.COMPLETED
                || order.getStatus() == FoodOrder.FoodOrderStatus.CLOSED) {
            throw new BusinessException("Không thể hủy order đã hoàn tất hoặc đóng");
        }

        order.setStatus(FoodOrder.FoodOrderStatus.CANCELLED);
        for (FoodItem item : order.getFoodItems()) {
            item.setStatus(FoodItem.FoodItemStatus.CANCELLED);
            foodItemRepository.save(item);
        }

        foodOrderRepository.save(order);

        return WaiterCancelOrderResponseDTO.builder()
                .orderId(order.getId())
                .status(order.getStatus().name())
                .build();
    }

    @Override
    @Transactional
    public WaiterServeCompleteResponseDTO completeServiceSession(Long waiterId, Long sessionId) {
        Restaurant workplace = getWorkplace(waiterId);
        RestaurantTableSession session = tableSessionRepository.findByIdAndRestaurantId(sessionId, workplace.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy phiên bàn hoặc phiên bàn không thuộc nhà hàng của bạn"));

        if (session.getWaiter() == null || !session.getWaiter().getId().equals(waiterId)) {
            throw new BusinessException("Bạn không được phân công phục vụ phiên bàn này");
        }

        if (session.getStatus() != RestaurantTableSession.TableSessionStatus.SERVING) {
            throw new BusinessException("Chỉ có thể hoàn tất phiên bàn đang phục vụ");
        }

        session.setStatus(RestaurantTableSession.TableSessionStatus.SERVED);
        session.getBooking().setStatus(Booking.BookingStatus.SERVED);

        tableSessionRepository.save(session);
        bookingRepository.save(session.getBooking());

        return WaiterServeCompleteResponseDTO.builder()
                .sessionId(session.getId())
                .status(session.getStatus().name())
                .build();
    }
}

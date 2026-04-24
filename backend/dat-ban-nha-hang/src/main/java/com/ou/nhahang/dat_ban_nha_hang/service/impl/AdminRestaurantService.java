package com.ou.nhahang.dat_ban_nha_hang.service.impl;

import com.ou.nhahang.dat_ban_nha_hang.dto.request.AdminRestaurantRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.AdminRestaurantDetailResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.AdminRestaurantListItemResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.entity.LegalDoc;
import com.ou.nhahang.dat_ban_nha_hang.entity.Restaurant;
import com.ou.nhahang.dat_ban_nha_hang.entity.User;
import com.ou.nhahang.dat_ban_nha_hang.exception.BusinessException;
import com.ou.nhahang.dat_ban_nha_hang.exception.ResourceNotFoundException;
import com.ou.nhahang.dat_ban_nha_hang.repository.RestaurantRepository;
import com.ou.nhahang.dat_ban_nha_hang.service.IAdminRestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminRestaurantService implements IAdminRestaurantService {

    private final RestaurantRepository restaurantRepository;

    private Restaurant getRestaurantOrThrow(Long restaurantId) {
        return restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhà hàng"));
    }

    private AdminRestaurantListItemResponseDTO mapToListItem(Restaurant r) {
        User manager = r.getManager();
        return AdminRestaurantListItemResponseDTO.builder()
                .restaurantId(r.getId())
                .restaurantName(r.getName())
                .managerName(manager != null ? manager.getFullName() : null)
                .status(r.getStatus() != null ? r.getStatus().name() : null)
                .createdAt(r.getCreatedAt())
                .build();
    }

    private AdminRestaurantDetailResponseDTO mapToDetail(Restaurant r) {
        User manager = r.getManager();
        AdminRestaurantDetailResponseDTO.ManagerDTO managerDTO = manager == null ? null
                : AdminRestaurantDetailResponseDTO.ManagerDTO.builder()
                .userId(manager.getId())
                .fullName(manager.getFullName())
                .phone(manager.getPhone())
                .build();

        List<AdminRestaurantDetailResponseDTO.LegalDocDTO> legalDocs = r.getLegalDocs() == null ? List.of()
                : r.getLegalDocs().stream()
                .map(this::mapLegalDoc)
                .toList();

        return AdminRestaurantDetailResponseDTO.builder()
                .restaurantId(r.getId())
                .restaurantName(r.getName())
                .status(r.getStatus() != null ? r.getStatus().name() : null)
                .manager(managerDTO)
                .legalDocs(legalDocs)
                .build();
    }

    private AdminRestaurantDetailResponseDTO.LegalDocDTO mapLegalDoc(LegalDoc doc) {
        return AdminRestaurantDetailResponseDTO.LegalDocDTO.builder()
                .docId(doc.getId())
                .docUrl(doc.getFile())
                .build();
    }

    @Override
    @Transactional
    public void approveRestaurant(Long restaurantId, AdminRestaurantRequestDTO.Approval request) {
        Restaurant restaurant = getRestaurantOrThrow(restaurantId);

        if ("APPROVED".equalsIgnoreCase(request.status())) {
            // Hiện entity không có enum APPROVED, map APPROVED -> OPENING
            restaurant.setStatus(Restaurant.RestaurantStatus.OPENING);
            restaurantRepository.save(restaurant);
            return;
        }

        if ("REJECTED".equalsIgnoreCase(request.status())) {
            if (request.rejectReason() == null || request.rejectReason().isBlank()) {
                throw new BusinessException("rejectReason là bắt buộc khi từ chối");
            }
            restaurant.setStatus(Restaurant.RestaurantStatus.REJECTED);
            restaurantRepository.save(restaurant);
            return;
        }

        throw new BusinessException("Trạng thái phê duyệt không hợp lệ");
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AdminRestaurantListItemResponseDTO> getRestaurants(int page, int limit, String status, String search) {
        Pageable pageable = PageRequest.of(page, limit);
        Restaurant.RestaurantStatus st = null;
        if (status != null && !status.isBlank()) {
            if ("APPROVED".equalsIgnoreCase(status)) {
                st = Restaurant.RestaurantStatus.OPENING;
            } else {
                st = Restaurant.RestaurantStatus.valueOf(status);
            }
        }

        Page<Restaurant> dataPage = restaurantRepository.adminSearchRestaurants(st,
                (search == null || search.isBlank()) ? null : search,
                pageable);
        return dataPage.map(this::mapToListItem);
    }

    @Override
    @Transactional(readOnly = true)
    public AdminRestaurantDetailResponseDTO getRestaurantDetail(Long restaurantId) {
        Restaurant restaurant = getRestaurantOrThrow(restaurantId);
        return mapToDetail(restaurant);
    }

    @Override
    @Transactional
    public void updateRestaurantStatus(Long restaurantId, AdminRestaurantRequestDTO.UpdateStatus request) {
        Restaurant restaurant = getRestaurantOrThrow(restaurantId);
        restaurant.setStatus(Restaurant.RestaurantStatus.valueOf(request.status()));
        restaurantRepository.save(restaurant);
    }

    @Override
    @Transactional
    public void updateRestaurantCommission(Long restaurantId, AdminRestaurantRequestDTO.UpdateCommission request) {
        Restaurant restaurant = getRestaurantOrThrow(restaurantId);
        restaurant.setCommissionType(Restaurant.CommissionType.valueOf(request.commissionType()));
        restaurant.setBaseCommissionValue(request.baseCommissionValue());
        restaurantRepository.save(restaurant);
    }
}


package com.ou.nhahang.dat_ban_nha_hang.service.impl;

import com.ou.nhahang.dat_ban_nha_hang.dto.request.AdminRestaurantRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.request.AdminRestaurantSearchRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.AdminRestaurantDetailResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.AdminRestaurantListItemResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.entity.LegalDoc;
import com.ou.nhahang.dat_ban_nha_hang.entity.Restaurant;
import com.ou.nhahang.dat_ban_nha_hang.entity.User;
import com.ou.nhahang.dat_ban_nha_hang.exception.BusinessException;
import com.ou.nhahang.dat_ban_nha_hang.exception.ResourceNotFoundException;
import com.ou.nhahang.dat_ban_nha_hang.repository.RestaurantRepository;
import com.ou.nhahang.dat_ban_nha_hang.repository.RoleRepository;
import com.ou.nhahang.dat_ban_nha_hang.repository.UserRepository;
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
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    private Restaurant getRestaurantOrThrow(Long restaurantId) {
        return restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhà hàng"));
    }

    private Restaurant.RestaurantStatus parseRestaurantStatus(String rawStatus) {
        try {
            return Restaurant.RestaurantStatus.valueOf(rawStatus.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new BusinessException("Trạng thái nhà hàng không hợp lệ");
        }
    }

    private void ensureValidCommission(AdminRestaurantRequestDTO.UpdateCommission request) {
        if ("PERCENTAGE".equalsIgnoreCase(request.commissionType()) && request.baseCommissionValue() > 100) {
            throw new BusinessException("Hoa hồng theo phần trăm phải nằm trong khoảng 0-100");
        }
    }

    private void detachCurrentManagerIfNeeded(Restaurant restaurant, User newManager) {
        User currentManager = restaurant.getManager();
        if (currentManager == null || currentManager.getId().equals(newManager.getId())) {
            return;
        }

        if (currentManager.getWorkplace() != null && currentManager.getWorkplace().getId().equals(restaurant.getId())) {
            currentManager.setWorkplace(null);
            userRepository.save(currentManager);
        }
    }

    private AdminRestaurantListItemResponseDTO mapToListItem(Restaurant r) {
        User manager = r.getManager();
        return AdminRestaurantListItemResponseDTO.builder()
                .restaurantId(r.getId())
                .restaurantName(r.getName())
                .managerName(manager != null ? manager.getFullName() : null)
                .status(r.getStatus() != null ? r.getStatus().name() : null)
                .commissionType(r.getCommissionType() != null ? r.getCommissionType().name() : null)
                .baseCommissionValue(r.getBaseCommissionValue())
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
                .logo(r.getLogo())
                .description(r.getDescription())
                .address(r.getAddress())
                .commissionType(r.getCommissionType() != null ? r.getCommissionType().name() : null)
                .baseCommissionValue(r.getBaseCommissionValue())
                .createdAt(r.getCreatedAt())
                .manager(managerDTO)
                .legalDocs(legalDocs)
                .build();
    }

    private AdminRestaurantDetailResponseDTO.LegalDocDTO mapLegalDoc(LegalDoc doc) {
        return AdminRestaurantDetailResponseDTO.LegalDocDTO.builder()
                .docId(doc.getId())
                .docUrl(doc.getFile())
                .docName(doc.getName())
                .docType(doc.getType() != null ? doc.getType().name() : null)
                .docStatus(doc.getStatus() != null ? doc.getStatus().name() : null)
                .expireDate(doc.getExpireDate())
                .build();
    }

    @Override
    @Transactional
    public void approveRestaurant(Long restaurantId, AdminRestaurantRequestDTO.Approval request) {
        Restaurant restaurant = getRestaurantOrThrow(restaurantId);

        if ("APPROVED".equalsIgnoreCase(request.status())) {
            restaurant.setStatus(Restaurant.RestaurantStatus.OPENING);

            // Nâng cấp khách hàng (manager) thành MANAGER ROLE
            User manager = restaurant.getManager();
            if (manager != null) {
                var managerRole = roleRepository.findByName("MANAGER")
                        .orElseThrow(() -> new BusinessException("Role MANAGER không tồn tại"));
                manager.getRoles().clear();
                manager.getRoles().add(managerRole);
                manager.setWorkplace(restaurant);
                userRepository.save(manager);
            }

            restaurantRepository.save(restaurant);
            return;
        }

        if ("REJECTED".equalsIgnoreCase(request.status())) {
            restaurant.setManager(null);
            restaurant.setStatus(Restaurant.RestaurantStatus.REJECTED);
            restaurantRepository.save(restaurant);
            return;
        }

        throw new BusinessException("Trạng thái phê duyệt không hợp lệ");
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AdminRestaurantListItemResponseDTO> getRestaurants(AdminRestaurantSearchRequestDTO request) {
        Pageable pageable = PageRequest.of(request.page(), request.limit());
        Restaurant.RestaurantStatus st = null;
        if (request.status() != null && !request.status().isBlank()) {
            if ("APPROVED".equalsIgnoreCase(request.status())) {
                st = Restaurant.RestaurantStatus.OPENING;
            } else {
                st = parseRestaurantStatus(request.status());
            }
        }

        Page<Restaurant> dataPage = restaurantRepository.adminSearchRestaurants(st,
                (request.search() == null || request.search().isBlank()) ? null : request.search(),
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
        restaurant.setStatus(parseRestaurantStatus(request.status()));
        restaurantRepository.save(restaurant);
    }

    @Override
    @Transactional
    public void updateRestaurantCommission(Long restaurantId, AdminRestaurantRequestDTO.UpdateCommission request) {
        Restaurant restaurant = getRestaurantOrThrow(restaurantId);
        ensureValidCommission(request);
        restaurant.setCommissionType(Restaurant.CommissionType.valueOf(request.commissionType()));
        restaurant.setBaseCommissionValue(request.baseCommissionValue());
        restaurantRepository.save(restaurant);
    }

    @Override
    @Transactional
    public void assignManager(Long restaurantId, Long userId) {
        Restaurant restaurant = getRestaurantOrThrow(restaurantId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));

        // Thăng cấp user -> MANAGER (set 1 role MANAGER)
        var managerRole = roleRepository.findByName("MANAGER")
                .orElseThrow(() -> new BusinessException("Role MANAGER không tồn tại"));
        detachCurrentManagerIfNeeded(restaurant, user);
        user.getRoles().clear();
        user.getRoles().add(managerRole);
        user.setWorkplace(restaurant);

        restaurant.setManager(user);

        userRepository.save(user);
        restaurantRepository.save(restaurant);
    }
}

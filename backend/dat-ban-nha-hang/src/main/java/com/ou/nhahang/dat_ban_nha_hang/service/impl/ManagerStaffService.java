package com.ou.nhahang.dat_ban_nha_hang.service.impl;

import com.ou.nhahang.dat_ban_nha_hang.dto.request.ManagerStaffRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.ManagerStaffResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.entity.Restaurant;
import com.ou.nhahang.dat_ban_nha_hang.entity.Role;
import com.ou.nhahang.dat_ban_nha_hang.entity.User;
import com.ou.nhahang.dat_ban_nha_hang.exception.BusinessException;
import com.ou.nhahang.dat_ban_nha_hang.exception.ResourceNotFoundException;
import com.ou.nhahang.dat_ban_nha_hang.repository.RestaurantRepository;
import com.ou.nhahang.dat_ban_nha_hang.repository.RoleRepository;
import com.ou.nhahang.dat_ban_nha_hang.repository.UserRepository;
import com.ou.nhahang.dat_ban_nha_hang.service.IManagerStaffService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ManagerStaffService implements IManagerStaffService {
    private static final Set<String> ALLOWED_STAFF_ROLES = Set.of("RECEPTIONIST", "WAITER", "CASHIER");

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RestaurantRepository restaurantRepository;
    private final PasswordEncoder passwordEncoder;

    private Restaurant getRestaurantIfManager(Long restaurantId, Long managerId) {
        return restaurantRepository.findByIdAndManagerId(restaurantId, managerId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhà hàng"));
    }

    private Restaurant getManagerWorkplaceOrThrow(Long managerId) {
        User manager = userRepository.findById(managerId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Manager"));
        if (manager.getWorkplace() == null) {
            throw new BusinessException("Manager chưa được gán nhà hàng làm việc");
        }
        return manager.getWorkplace();
    }

    private Role getRoleByNameOrThrow(String roleName) {
        String normalized = roleName.toUpperCase();
        return roleRepository.findByName(normalized)
                .orElseThrow(() -> new ResourceNotFoundException("Role không tồn tại"));
    }

    private Role getAssignableStaffRoleOrThrow(Long roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new BusinessException("Role không tồn tại"));
        if (!ALLOWED_STAFF_ROLES.contains(role.getName())) {
            throw new BusinessException("Manager chỉ được gán role RECEPTIONIST, WAITER hoặc CASHIER");
        }
        return role;
    }

    private ManagerStaffResponseDTO mapToDTO(User user) {
        List<ManagerStaffResponseDTO.RoleResponse> roles = user.getRoles().stream()
                .map(r -> new ManagerStaffResponseDTO.RoleResponse(r.getId(), "ROLE_" + r.getName()))
                .collect(Collectors.toList());

        return ManagerStaffResponseDTO.builder()
                .userId(user.getId())
                .fullName(user.getFullName())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .roles(roles)
                .status(user.getStatus().name())
                .build();
    }

    @Override
    @Transactional
    public ManagerStaffResponseDTO updateStaff(Long staffId, Long managerId,
            ManagerStaffRequestDTO.UpdateStaff requestDTO) {
        User staff = userRepository.findById(staffId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhân viên"));

        if (staff.getWorkplace() == null) {
            throw new BusinessException("Nhân viên không thuộc nhà hàng nào");
        }

        getRestaurantIfManager(staff.getWorkplace().getId(), managerId); // Xác thực manager có thuộc nhà hàng không

        if (!staff.getEmail().equals(requestDTO.email()) && userRepository.existsByEmail(requestDTO.email())) {
            throw new BusinessException("Email đã được sử dụng cho tài khoản khác");
        }
        if (!staff.getPhone().equals(requestDTO.phone()) && userRepository.existsByPhone(requestDTO.phone())) {
            throw new BusinessException("Số điện thoại đã được sử dụng cho tài khoản khác");
        }

        Role role = getAssignableStaffRoleOrThrow(requestDTO.roleId());

        staff.setFullName(requestDTO.fullName());
        staff.setEmail(requestDTO.email());
        staff.setPhone(requestDTO.phone());
        staff.setStatus(User.UserStatus.valueOf(requestDTO.status().toUpperCase()));

        staff.getRoles().clear();
        staff.getRoles().add(role);

        User updatedStaff = userRepository.save(staff);
        return mapToDTO(updatedStaff);
    }

    @Override
    @Transactional
    public void deleteStaff(Long staffId, Long managerId) {
        User staff = userRepository.findById(staffId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhân viên"));

        if (staff.getWorkplace() == null) {
            throw new BusinessException("Nhân viên không thuộc nhà hàng nào");
        }

        getRestaurantIfManager(staff.getWorkplace().getId(), managerId);

        userRepository.delete(staff);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ManagerStaffResponseDTO> getStaffs(Long managerId,
            ManagerStaffRequestDTO.GetStaffs requestDTO) {
        Restaurant restaurant = getManagerWorkplaceOrThrow(managerId);
        Pageable pageable = PageRequest.of(requestDTO.page(), requestDTO.limit());

        return userRepository.findByWorkplaceId(restaurant.getId(), pageable).map(this::mapToDTO);
    }

    @Override
    @Transactional
    public ManagerStaffResponseDTO createStaff(Long managerId,
            ManagerStaffRequestDTO.CreateStaff requestDTO) {
        Restaurant restaurant = getManagerWorkplaceOrThrow(managerId);

        if (userRepository.existsByUsername(requestDTO.username())) {
            throw new BusinessException("Tên đăng nhập đã tồn tại");
        }
        if (requestDTO.email() != null && !requestDTO.email().isBlank()
                && userRepository.existsByEmail(requestDTO.email())) {
            throw new BusinessException("Email đã được sử dụng");
        }
        if (userRepository.existsByPhone(requestDTO.phone())) {
            throw new BusinessException("Số điện thoại đã được sử dụng");
        }

        Role role = getAssignableStaffRoleOrThrow(requestDTO.roleId());
        HashSet<Role> roles = new HashSet<>();
        roles.add(role);

        String email = (requestDTO.email() == null || requestDTO.email().isBlank())
                ? requestDTO.username() + ".res" + restaurant.getId() + "@nhahang.local"
                : requestDTO.email();

        User staff = User.builder()
                .username(requestDTO.username())
                .password(passwordEncoder.encode(requestDTO.password()))
                .fullName(requestDTO.fullName())
                .email(email)
                .phone(requestDTO.phone())
                .status(User.UserStatus.ACTIVE)
                .address("")
                .workplace(restaurant)
                .roles(roles)
                .build();

        User saved = userRepository.save(staff);
        return mapToDTO(saved);
    }

    @Override
    @Transactional
    public void kickStaff(Long staffId, Long managerId) {
        Restaurant restaurant = getManagerWorkplaceOrThrow(managerId);
        User staff = userRepository.findById(staffId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhân viên"));

        if (staff.getWorkplace() == null || !restaurant.getId().equals(staff.getWorkplace().getId())) {
            throw new BusinessException("Nhân viên không thuộc nhà hàng của bạn");
        }

        Role customerRole = getRoleByNameOrThrow("CUSTOMER");
        staff.getRoles().clear();
        staff.getRoles().add(customerRole);
        staff.setWorkplace(null);
        userRepository.save(staff);
    }
}

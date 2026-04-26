package com.ou.nhahang.dat_ban_nha_hang.service.impl;

import com.ou.nhahang.dat_ban_nha_hang.dto.request.ManagerStaffRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.request.ManagerStaffManagementRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.ManagerStaffManagementResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.ManagerStaffResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.entity.Restaurant;
import com.ou.nhahang.dat_ban_nha_hang.entity.Role;
import com.ou.nhahang.dat_ban_nha_hang.entity.User;
import com.ou.nhahang.dat_ban_nha_hang.exception.BusinessException;
import com.ou.nhahang.dat_ban_nha_hang.repository.RestaurantRepository;
import com.ou.nhahang.dat_ban_nha_hang.repository.RoleRepository;
import com.ou.nhahang.dat_ban_nha_hang.repository.UserRepository;
import com.ou.nhahang.dat_ban_nha_hang.service.IManagerStaffService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ManagerStaffService implements IManagerStaffService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RestaurantRepository restaurantRepository;
    private final PasswordEncoder passwordEncoder;

    private Restaurant getRestaurantIfManager(Long restaurantId, Long managerId) {
        return restaurantRepository.findByIdAndManagerId(restaurantId, managerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "Bạn không có quyền quản lý nhà hàng này"));
    }

    private Restaurant getManagerWorkplaceOrThrow(Long managerId) {
        User manager = userRepository.findById(managerId)
                .orElseThrow(() -> new BusinessException("Không tìm thấy Manager"));
        if (manager.getWorkplace() == null) {
            throw new BusinessException("Manager chưa được gán nhà hàng làm việc");
        }
        return manager.getWorkplace();
    }

    private Role getRoleByNameOrThrow(String roleName) {
        String normalized = roleName.replace("ROLE_", "").toUpperCase();
        return roleRepository.findByName(normalized)
                .orElseThrow(() -> new BusinessException("Role không tồn tại"));
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

    private ManagerStaffManagementResponseDTO mapToManagementDTO(User user) {
        // assume single-role for staff management view
        String role = user.getRoles() == null || user.getRoles().isEmpty()
                ? null
                : user.getRoles().iterator().next().getName();
        return ManagerStaffManagementResponseDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(role)
                .status(user.getStatus().name())
                .workplaceRestaurantId(user.getWorkplace() != null ? user.getWorkplace().getId() : null)
                .build();
    }

    @Override
    public Page<ManagerStaffResponseDTO> getStaffs(Long restaurantId, Long managerId,
            ManagerStaffRequestDTO.GetStaffs requestDTO) {
        getRestaurantIfManager(restaurantId, managerId);
        Pageable pageable = PageRequest.of(requestDTO.page(), requestDTO.limit());
        return userRepository.findByWorkplaceId(restaurantId, pageable).map(this::mapToDTO);
    }

    @Override
    @Transactional
    public ManagerStaffResponseDTO createStaff(Long restaurantId, Long managerId,
            ManagerStaffRequestDTO.CreateStaff requestDTO) {
        Restaurant restaurant = getRestaurantIfManager(restaurantId, managerId);

        if (userRepository.existsByUsername(requestDTO.username())) {
            throw new BusinessException("Tên đăng nhập đã tồn tại");
        }
        if (userRepository.existsByEmail(requestDTO.email())) {
            throw new BusinessException("Email đã được sử dụng");
        }
        if (userRepository.existsByPhone(requestDTO.phone())) {
            throw new BusinessException("Số điện thoại đã được sử dụng");
        }

        Role role = roleRepository.findById(requestDTO.roleId())
                .orElseThrow(() -> new BusinessException("Role không tồn tại"));

        HashSet<Role> roles = new HashSet<>();
        roles.add(role);

        User staff = User.builder()
                .username(requestDTO.username())
                .password(passwordEncoder.encode(requestDTO.password()))
                .fullName(requestDTO.fullName())
                .email(requestDTO.email())
                .phone(requestDTO.phone())
                .status(User.UserStatus.ACTIVE)
                .address("")
                .workplace(restaurant)
                .roles(roles)
                .build();

        User savedStaff = userRepository.save(staff);
        return mapToDTO(savedStaff);
    }

    @Override
    @Transactional
    public ManagerStaffResponseDTO updateStaff(Long staffId, Long managerId,
            ManagerStaffRequestDTO.UpdateStaff requestDTO) {
        User staff = userRepository.findById(staffId)
                .orElseThrow(() -> new BusinessException("Không tìm thấy nhân viên"));

        if (staff.getWorkplace() == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Nhân viên không thuộc nhà hàng nào");
        }

        getRestaurantIfManager(staff.getWorkplace().getId(), managerId); // Verify authority

        if (!staff.getEmail().equals(requestDTO.email()) && userRepository.existsByEmail(requestDTO.email())) {
            throw new BusinessException("Email đã được sử dụng cho tài khoản khác");
        }
        if (!staff.getPhone().equals(requestDTO.phone()) && userRepository.existsByPhone(requestDTO.phone())) {
            throw new BusinessException("Số điện thoại đã được sử dụng cho tài khoản khác");
        }

        Role role = roleRepository.findById(requestDTO.roleId())
                .orElseThrow(() -> new BusinessException("Role không tồn tại"));

        staff.setFullName(requestDTO.fullName());
        staff.setEmail(requestDTO.email());
        staff.setPhone(requestDTO.phone());
        staff.setStatus(User.UserStatus.valueOf(requestDTO.status().toUpperCase()));

        HashSet<Role> roles = new HashSet<>();
        roles.add(role);
        staff.setRoles(roles);

        User updatedStaff = userRepository.save(staff);
        return mapToDTO(updatedStaff);
    }

    @Override
    @Transactional
    public void deleteStaff(Long staffId, Long managerId) {
        User staff = userRepository.findById(staffId)
                .orElseThrow(() -> new BusinessException("Không tìm thấy nhân viên"));

        if (staff.getWorkplace() == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Nhân viên không thuộc nhà hàng nào");
        }

        getRestaurantIfManager(staff.getWorkplace().getId(), managerId); // Verify authority

        userRepository.delete(staff);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ManagerStaffManagementResponseDTO> getStaffsByManager(Long managerId,
            ManagerStaffManagementRequestDTO.ListStaffs requestDTO) {
        Restaurant restaurant = getManagerWorkplaceOrThrow(managerId);
        Pageable pageable = PageRequest.of(requestDTO.page(), requestDTO.limit());

        Page<User> page = userRepository.findByWorkplaceId(restaurant.getId(), pageable);
        if (requestDTO.role() == null) {
            return page.map(this::mapToManagementDTO);
        }

        String roleName = requestDTO.role().toUpperCase();
        // filter in-memory to avoid adding more complex queries now
        List<ManagerStaffManagementResponseDTO> filtered = page.getContent().stream()
                .filter(u -> u.getRoles() != null && u.getRoles().stream()
                        .anyMatch(r -> roleName.equalsIgnoreCase(r.getName())))
                .map(this::mapToManagementDTO)
                .toList();
        return new org.springframework.data.domain.PageImpl<>(filtered, pageable, page.getTotalElements());
    }

    @Override
    @Transactional
    public ManagerStaffManagementResponseDTO createStaffByManager(Long managerId,
            ManagerStaffManagementRequestDTO.CreateStaff requestDTO) {
        Restaurant restaurant = getManagerWorkplaceOrThrow(managerId);

        if (userRepository.existsByUsername(requestDTO.username())) {
            throw new BusinessException("Tên đăng nhập đã tồn tại");
        }
        if (requestDTO.email() != null && !requestDTO.email().isBlank() && userRepository.existsByEmail(requestDTO.email())) {
            throw new BusinessException("Email đã được sử dụng");
        }
        if (userRepository.existsByPhone(requestDTO.phone())) {
            throw new BusinessException("Số điện thoại đã được sử dụng");
        }

        Role role = getRoleByNameOrThrow(requestDTO.role());
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
        return mapToManagementDTO(saved);
    }

    @Override
    @Transactional
    public void kickStaff(Long staffId, Long managerId) {
        Restaurant restaurant = getManagerWorkplaceOrThrow(managerId);
        User staff = userRepository.findById(staffId)
                .orElseThrow(() -> new BusinessException("Không tìm thấy nhân viên"));

        if (staff.getWorkplace() == null || !restaurant.getId().equals(staff.getWorkplace().getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Nhân viên không thuộc nhà hàng của bạn");
        }

        Role customerRole = getRoleByNameOrThrow("CUSTOMER");
        HashSet<Role> roles = new HashSet<>();
        roles.add(customerRole);
        staff.setRoles(roles);
        staff.setWorkplace(null);
        userRepository.save(staff);
    }
}

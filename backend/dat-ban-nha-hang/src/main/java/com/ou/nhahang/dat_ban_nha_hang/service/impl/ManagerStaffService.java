package com.ou.nhahang.dat_ban_nha_hang.service.impl;

import com.ou.nhahang.dat_ban_nha_hang.dto.request.ManagerStaffRequestDTO;
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

@Service
public class ManagerStaffService implements IManagerStaffService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RestaurantRepository restaurantRepository;
    private final PasswordEncoder passwordEncoder;

    public ManagerStaffService(UserRepository userRepository, RoleRepository roleRepository,
            RestaurantRepository restaurantRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.restaurantRepository = restaurantRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private Restaurant getRestaurantIfManager(Long restaurantId, Long managerId) {
        return restaurantRepository.findByIdAndManagerId(restaurantId, managerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "Bạn không có quyền quản lý nhà hàng này"));
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
}

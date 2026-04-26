package com.ou.nhahang.dat_ban_nha_hang.service.impl;

import com.ou.nhahang.dat_ban_nha_hang.dto.request.AdminUserRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.AdminUserDetailResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.AdminUserResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.entity.Restaurant;
import com.ou.nhahang.dat_ban_nha_hang.entity.Role;
import com.ou.nhahang.dat_ban_nha_hang.entity.User;
import com.ou.nhahang.dat_ban_nha_hang.exception.BusinessException;
import com.ou.nhahang.dat_ban_nha_hang.exception.ResourceNotFoundException;
import com.ou.nhahang.dat_ban_nha_hang.repository.RestaurantRepository;
import com.ou.nhahang.dat_ban_nha_hang.repository.RoleRepository;
import com.ou.nhahang.dat_ban_nha_hang.repository.UserRepository;
import com.ou.nhahang.dat_ban_nha_hang.service.IAdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminUserService implements IAdminUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RestaurantRepository restaurantRepository;
    private final PasswordEncoder passwordEncoder;

    private AdminUserResponseDTO mapToDTO(User user) {
        List<AdminUserResponseDTO.RoleResponse> roles = user.getRoles().stream()
                .map(r -> new AdminUserResponseDTO.RoleResponse(r.getId(), "ROLE_" + r.getName()))
                .collect(Collectors.toList());

        return AdminUserResponseDTO.builder()
                .userId(user.getId())
                .fullName(user.getFullName())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .roles(roles)
                .status(user.getStatus().name())
                .build();
    }

    private AdminUserDetailResponseDTO mapToDetailDTO(User user) {
        List<AdminUserResponseDTO.RoleResponse> roles = user.getRoles().stream()
                .map(r -> new AdminUserResponseDTO.RoleResponse(r.getId(), "ROLE_" + r.getName()))
                .collect(Collectors.toList());

        AdminUserDetailResponseDTO.WorkplaceDTO workplace = null;
        if (user.getWorkplace() != null) {
            Restaurant r = user.getWorkplace();
            workplace = AdminUserDetailResponseDTO.WorkplaceDTO.builder()
                    .restaurantId(r.getId())
                    .name(r.getName())
                    .status(r.getStatus() != null ? r.getStatus().name() : null)
                    .avatar(r.getLogo())
                    .build();
        }

        return AdminUserDetailResponseDTO.builder()
                .userId(user.getId())
                .fullName(user.getFullName())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .roles(roles)
                .status(user.getStatus().name())
                .address(user.getAddress())
                .avatar(user.getAvatar())
                .workplace(workplace)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AdminUserResponseDTO> getUsers(int page, int limit, String role, Long restaurantId, String status,
            String search) {
        Pageable pageable = PageRequest.of(page, limit);
        String roleName = (role == null || role.isBlank()) ? null : role.replace("ROLE_", "").toUpperCase();
        User.UserStatus st = (status == null || status.isBlank()) ? null : User.UserStatus.valueOf(status.toUpperCase());
        String q = (search == null || search.isBlank()) ? null : search;

        return userRepository.adminSearchUsers(roleName, restaurantId, st, q, pageable).map(this::mapToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public AdminUserDetailResponseDTO getUserDetail(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));
        return mapToDetailDTO(user);
    }

    @Override
    @Transactional
    public AdminUserResponseDTO createUser(AdminUserRequestDTO.Create request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new BusinessException("Tên đăng nhập đã tồn tại");
        }
        if (request.email() != null && !request.email().isBlank() && userRepository.existsByEmail(request.email())) {
            throw new BusinessException("Email đã được sử dụng");
        }
        if (userRepository.existsByPhone(request.phone())) {
            throw new BusinessException("Số điện thoại đã được sử dụng");
        }

        Role role = roleRepository.findById(request.roleId())
                .orElseThrow(() -> new BusinessException("Role không tồn tại"));
        HashSet<Role> roles = new HashSet<>();
        roles.add(role);

        String email = (request.email() == null || request.email().isBlank())
                ? request.username() + "@dummy.local"
                : request.email();

        User user = User.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .fullName(request.fullName())
                .email(email)
                .phone(request.phone())
                .status(User.UserStatus.ACTIVE)
                .address("")
                .avatar(null)
                .workplace(null)
                .roles(roles)
                .build();

        User saved = userRepository.save(user);
        return mapToDTO(saved);
    }

    @Override
    @Transactional
    public AdminUserResponseDTO updateUser(Long userId, AdminUserRequestDTO.Update request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));

        if (!user.getEmail().equals(request.email()) && userRepository.existsByEmail(request.email())) {
            throw new BusinessException("Email đã được sử dụng cho tài khoản khác");
        }
        if (!user.getPhone().equals(request.phone()) && userRepository.existsByPhone(request.phone())) {
            throw new BusinessException("Số điện thoại đã được sử dụng cho tài khoản khác");
        }

        Role role = roleRepository.findById(request.roleId())
                .orElseThrow(() -> new BusinessException("Role không tồn tại"));

        user.setFullName(request.fullName());
        user.setEmail(request.email());
        user.setPhone(request.phone());
        user.setStatus(User.UserStatus.valueOf(request.status().toUpperCase()));

        HashSet<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);

        User saved = userRepository.save(user);
        return mapToDTO(saved);
    }

    @Override
    @Transactional
    public void updateUserWorkplace(Long userId, AdminUserRequestDTO.UpdateWorkplace request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));

        Role role = roleRepository.findById(request.roleId())
                .orElseThrow(() -> new BusinessException("Role không tồn tại"));

        HashSet<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);

        if (request.restaurantId() == null) {
            user.setWorkplace(null);
            userRepository.save(user);
            return;
        }

        Restaurant restaurant = restaurantRepository.findById(request.restaurantId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhà hàng"));
        user.setWorkplace(restaurant);
        userRepository.save(user);
    }
}


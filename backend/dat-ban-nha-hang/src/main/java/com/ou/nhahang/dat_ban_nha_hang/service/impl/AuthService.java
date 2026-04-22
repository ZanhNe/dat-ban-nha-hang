package com.ou.nhahang.dat_ban_nha_hang.service.impl;

import com.ou.nhahang.dat_ban_nha_hang.dto.request.LoginRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.request.RegisterRequestDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.AuthResponseDTO;
import com.ou.nhahang.dat_ban_nha_hang.dto.response.UserDTO;
import com.ou.nhahang.dat_ban_nha_hang.entity.Role;
import com.ou.nhahang.dat_ban_nha_hang.entity.User;
import com.ou.nhahang.dat_ban_nha_hang.repository.RoleRepository;
import com.ou.nhahang.dat_ban_nha_hang.repository.UserRepository;
import com.ou.nhahang.dat_ban_nha_hang.security.JwtUtil;
import com.ou.nhahang.dat_ban_nha_hang.service.IAuthService;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ou.nhahang.dat_ban_nha_hang.exception.BusinessException;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService implements IAuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Override
    @Transactional
    public AuthResponseDTO register(RegisterRequestDTO request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new BusinessException("Tên đăng nhập đã tồn tại");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessException("Email đã được sử dụng");
        }
        if (userRepository.existsByPhone(request.phone())) {
            throw new BusinessException("Số điện thoại đã được sử dụng");
        }

        User user = User.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .fullName(request.fullName())
                .email(request.email())
                .phone(request.phone())
                .address(request.address() != null ? request.address() : "")
                .status(User.UserStatus.ACTIVE)
                .build();

        Role userRole = roleRepository.findByName("CUSTOMER").orElseGet(() -> {
            Role role = new Role();
            role.setName("CUSTOMER");
            return roleRepository.save(role);
        });

        HashSet<Role> roles = new HashSet<>();
        roles.add(userRole);
        user.setRoles(roles);

        user = userRepository.save(user);

        return buildAuthResponse(user);

    }

    @Override
    public AuthResponseDTO login(LoginRequestDTO request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        } catch (org.springframework.security.core.AuthenticationException e) {
            throw new BusinessException("Sai tên đăng nhập hoặc mật khẩu");
        }

        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new BusinessException("Sai tên đăng nhập hoặc mật khẩu"));

        return buildAuthResponse(user);
    }

    private AuthResponseDTO buildAuthResponse(User user) {
        List<String> rolesWithPrefix = user.getRoles().stream()
                .map(r -> "ROLE_" + r.getName())
                .collect(Collectors.toList());

        String accessToken = jwtUtil.generateToken(user.getUsername(), user.getId(),
                user.getWorkplace() != null ? user.getWorkplace().getId() : null,
                rolesWithPrefix);
        String refreshToken = jwtUtil.generateRefreshToken(user.getUsername(), user.getId(),
                user.getWorkplace() != null ? user.getWorkplace().getId() : null,
                rolesWithPrefix);

        UserDTO userDTO = UserDTO.builder()
                .userId(user.getId())
                .restaurantId(user.getWorkplace() != null ? user.getWorkplace().getId() : null)
                .username(user.getUsername())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .address(user.getAddress())
                .avatar(user.getAvatar())
                .status(user.getStatus().name())
                .roles(user.getRoles().stream().map(Role::getName).collect(Collectors.toList()))
                .createdAt(user.getCreatedAt() != null ? user.getCreatedAt().toString() : null)
                .build();

        return AuthResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(3600L)
                .user(userDTO)
                .build();
    }
}

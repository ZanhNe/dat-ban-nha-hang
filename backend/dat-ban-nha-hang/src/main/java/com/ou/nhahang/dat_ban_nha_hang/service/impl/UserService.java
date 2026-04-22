package com.ou.nhahang.dat_ban_nha_hang.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ou.nhahang.dat_ban_nha_hang.entity.User;
import com.ou.nhahang.dat_ban_nha_hang.repository.UserRepository;
import com.ou.nhahang.dat_ban_nha_hang.service.IUserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final UserRepository userRepository;

    @Override
    public List<User> getAllReceptionists() {
        return userRepository.findAllByRoleName("RECEPTIONIST");
    }
}

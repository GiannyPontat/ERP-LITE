package com.gp_dev.erp_lite.services.impl;

import com.gp_dev.erp_lite.dtos.UserDto;
import com.gp_dev.erp_lite.exceptions.AppException;
import com.gp_dev.erp_lite.models.User;
import com.gp_dev.erp_lite.repositories.UserRepo;
import com.gp_dev.erp_lite.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepo userRepo;

    @Override
    public User findByEmail(String email) {
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepo.findAll().stream()
                .map(User::dto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(Long id) {
        return userRepo.findById(id)
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND))
                .dto();
    }

    @Override
    public UserDto updateUser(Long id, UserDto userDto) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));

        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setEnabled(userDto.getEnabled());

        return userRepo.save(user).dto();
    }

    @Override
    public void deleteUser(Long id) {
        userRepo.deleteById(id);
    }
}

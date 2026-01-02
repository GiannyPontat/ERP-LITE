package com.gp_dev.erp_lite.services;

import com.gp_dev.erp_lite.dtos.UserDto;
import com.gp_dev.erp_lite.models.User;

import java.util.List;

public interface UserService {
    User findByEmail(String email);
    List<UserDto> getAllUsers();
    UserDto getUserById(Long id);
    UserDto updateUser(Long id, UserDto userDto);
    void deleteUser(Long id);
}

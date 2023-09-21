package com.javatechie.redis.repository;

import com.javatechie.redis.entity.User;

import java.util.List;

public interface UserDao {
    boolean saveUser(User user);

    List<User> getAllUsers();

    User getUserById(Long id);

    boolean deleteUser(Long id);

    boolean updateUser(Long id, User user);
}

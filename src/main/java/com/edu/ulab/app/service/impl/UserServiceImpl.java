package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.entity.Person;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.exception.UserServiceException;
import com.edu.ulab.app.mapper.UserMapper;
import com.edu.ulab.app.repository.UserRepository;
import com.edu.ulab.app.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

@Slf4j
@Service
@Validated
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository,
                           UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public UserDto createUser(@Valid UserDto userDto) {
        Person user = userMapper.userDtoToPerson(userDto);
        log.info("Mapped user: {}", user);
        Person savedUser = userRepository.save(user);
        log.info("Saved user: {}", savedUser);
        return userMapper.personToUserDto(savedUser);
    }

    @Override
    public UserDto updateUser(@Valid UserDto userDto) {
        Person currentUser = userMapper.userDtoToPerson(userDto);
        log.info("Mapped user: {}", currentUser);
        Person userByCurrentIdFromDB = userRepository.findById(currentUser.getUserId())
                .orElseThrow(() -> new UserServiceException("Can't update. User with such id not found"));
        log.info("Got user by id from DB for update: {}", userByCurrentIdFromDB);

        currentUser.setUserId(userByCurrentIdFromDB.getUserId());
        Person updatedUser = userRepository.save(currentUser);
        log.info("Updated user: {}", updatedUser);

        return userMapper.personToUserDto(updatedUser);
    }

    @Override
    public UserDto getUserById(Long id) {
        log.info("Got user id #{} to look for", id);
        UserDto foundUserById = userMapper.personToUserDto(userRepository.findById(id)
                .orElseThrow(() -> new UserServiceException("User not found"))
        );
        log.info("Got user by id from DB: {}", foundUserById);

        return foundUserById;
    }

    @Override
    public void deleteUserById(Long id) {
        log.info("Got user id #{} for delete", id);
        userRepository.deleteById(id);
        log.info("Deleted user by its id #{}", id);
    }
}

package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.entity.Person;
import com.edu.ulab.app.exception.BookServiceException;
import com.edu.ulab.app.exception.UserServiceException;
import com.edu.ulab.app.mapper.BookRowMapper;
import com.edu.ulab.app.mapper.UserMapper;
import com.edu.ulab.app.mapper.UserRowMapper;
import com.edu.ulab.app.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.util.Objects;

@Slf4j
@Service
public class UserServiceImplTemplate implements UserService {

    private final JdbcTemplate jdbcTemplate;
    private final UserMapper userMapper;

    private final String INSERT_SQL = "INSERT INTO PERSON(FULL_NAME, TITLE, AGE) VALUES (?,?,?)";
    private final String UPDATE_SQL = "UPDATE PERSON SET FULL_NAME = ?, TITLE = ?, AGE = ? WHERE ID = ?";
    private final String SELECT_USER_BY_ID_SQL = "SELECT * FROM PERSON WHERE ID = ?";
    private final String DELETE_SQL = "DELETE FROM PERSON WHERE ID = ?";

    public UserServiceImplTemplate(JdbcTemplate jdbcTemplate, UserMapper userMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.userMapper = userMapper;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(INSERT_SQL, new String[]{"id"});
                    ps.setString(1, userDto.getFullName());
                    ps.setString(2, userDto.getTitle());
                    ps.setLong(3, userDto.getAge());
                    return ps;
                }, keyHolder);

        userDto.setUserId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        log.info("Created user: {}", userDto);

        return userDto;
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        Person currentUser = userMapper.userDtoToPerson(userDto);
        log.info("Mapped user: {}", currentUser);
        UserDto foundUserFromDB = getUserById(currentUser.getUserId());

        jdbcTemplate.update(UPDATE_SQL, currentUser.getFullName(),
                currentUser.getTitle(),
                currentUser.getAge(),
                foundUserFromDB.getUserId());

        userDto.setUserId(foundUserFromDB.getUserId());
        log.info("Updated user: {}", userDto);

        return userDto;
    }

    @Override
    public UserDto getUserById(Long id) {
        UserDto foundUser = jdbcTemplate.query(SELECT_USER_BY_ID_SQL, new UserRowMapper(), id)
                .stream()
                .findFirst()
                .map(user -> userMapper.personToUserDto(user))
                .orElseThrow(() -> new UserServiceException("User with id #" + id + " not found"));
        log.info("Found user by id #{}: {}", id, foundUser);

        return foundUser;
    }

    @Override
    public void deleteUserById(Long id) {
        int deletedUsersCount = jdbcTemplate.update(DELETE_SQL, id);
        log.info("How many users deleted: {}", deletedUsersCount);
    }
}

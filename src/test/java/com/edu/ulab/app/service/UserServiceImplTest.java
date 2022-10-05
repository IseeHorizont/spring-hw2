package com.edu.ulab.app.service;

import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.entity.Person;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.exception.UserServiceException;
import com.edu.ulab.app.mapper.UserMapper;
import com.edu.ulab.app.repository.UserRepository;
import com.edu.ulab.app.service.impl.UserServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

/**
 * Тестирование функционала {@link com.edu.ulab.app.service.impl.UserServiceImpl}.
 */
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@DisplayName("Testing user functionality.")
public class UserServiceImplTest {
    @InjectMocks
    UserServiceImpl userService;

    @Mock
    UserRepository userRepository;

    @Mock
    UserMapper userMapper;

    @Test
    @DisplayName("Creating new user. Should be successful.")
    void savePerson_success_Test() {
        //given

        UserDto userDto = new UserDto();
        userDto.setFullName("test name");
        userDto.setTitle("test title");
        userDto.setAge(11);

        Person user = new Person();
        user.setFullName("test name");
        user.setTitle("test title");
        user.setAge(11);

        Person savedUser  = new Person();
        savedUser.setUserId(1L);
        savedUser.setFullName("test name");
        savedUser.setTitle("test title");
        savedUser.setAge(11);

        UserDto resultUser = new UserDto();
        resultUser.setUserId(1L);
        resultUser.setFullName("test name");
        resultUser.setTitle("test title");
        resultUser.setAge(11);

        //when

        when(userMapper.userDtoToPerson(userDto)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(savedUser);
        when(userMapper.personToUserDto(savedUser)).thenReturn(resultUser);

        //then

        UserDto userDtoResult = userService.createUser(userDto);

        assertNotNull(userDtoResult);
        assertEquals(1L, userDtoResult.getUserId());
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Updating user. Should be successful.")
    void updateUser_success_Test() {
        // given

        Person userForUpdate = new Person();
        userForUpdate.setUserId(1L);
        userForUpdate.setFullName("name");
        userForUpdate.setTitle("title");
        userForUpdate.setAge(11);

        UserDto userDtoForUpdate = new UserDto();
        userDtoForUpdate.setUserId(1L);
        userDtoForUpdate.setFullName("name");
        userDtoForUpdate.setTitle("title");
        userDtoForUpdate.setAge(11);

        Person updatedUser = new Person();
        updatedUser.setUserId(1L);
        updatedUser.setFullName("updated name");
        updatedUser.setTitle("updated title");
        updatedUser.setAge(21);

        UserDto updatedUserDto = new UserDto();
        updatedUserDto.setUserId(1L);
        updatedUserDto.setFullName("updated name");
        updatedUserDto.setTitle("updated title");
        updatedUserDto.setAge(21);

        // when

        when(userMapper.userDtoToPerson(userDtoForUpdate)).thenReturn(userForUpdate);
        when(userRepository.findByIdForUpdate(userForUpdate.getUserId()))
                .thenReturn(Optional.ofNullable(updatedUser));
        when(userMapper.personToUserDto(updatedUser)).thenReturn(updatedUserDto);

        // then

        UserDto userDtoResult = userService.updateUser(userDtoForUpdate);

        assertNotNull(userDtoResult);
        assertEquals(updatedUserDto, userDtoResult);
        verify(userRepository).save(userForUpdate);
    }

    @Test
    @DisplayName("Getting user by its id. Should be successful.")
    void getUserById_Test() {
        // given

        Person userWhoLookFor = new Person();
        userWhoLookFor.setUserId(1l);
        userWhoLookFor.setFullName("test name for search");
        userWhoLookFor.setTitle("test title");
        userWhoLookFor.setAge(21);

        UserDto resultUserDto = new UserDto();
        resultUserDto.setUserId(1l);
        resultUserDto.setFullName("test name for search");
        resultUserDto.setTitle("test title");
        resultUserDto.setAge(21);

        // when

        when(userRepository.findById(userWhoLookFor.getUserId()))
                .thenReturn(Optional.ofNullable(userWhoLookFor));
        when(userMapper.personToUserDto(userWhoLookFor)).thenReturn(resultUserDto);

        // then

        UserDto resultUserDtoFromService = userService.getUserById(userWhoLookFor.getUserId());
        assertNotNull(resultUserDtoFromService);
        assertEquals(resultUserDto, resultUserDtoFromService);
        verify(userRepository).findById(userWhoLookFor.getUserId());
    }

    @Test
    @DisplayName("Getting user by id. Expect NotFoundException.class exception.")
    void getUserById_whenUserNotFound_Test() {
        // given

        Person userWhoLookFor = new Person();
        userWhoLookFor.setUserId(11L);
        userWhoLookFor.setFullName("test name for search");
        userWhoLookFor.setTitle("test title");
        userWhoLookFor.setAge(21);

        // when

        UserServiceException userNotFoundException = new UserServiceException("User not found");
        doThrow(userNotFoundException).when(userRepository)
                .deleteById(userWhoLookFor.getUserId());

        // then

        assertThatThrownBy(() -> userService.getUserById(userWhoLookFor.getUserId()))
                .isInstanceOf(UserServiceException.class)
                .hasMessage("User not found");
        verify(userRepository).findById(userWhoLookFor.getUserId());
    }

    @Test
    @DisplayName("Deleting user. Should be successful.")
    void deleteUserById_success_Test() {
        // given

        Person userForDelete = new Person();
        userForDelete.setUserId(2l);
        userForDelete.setFullName("name for delete");
        userForDelete.setTitle("title");
        userForDelete.setAge(33);

        UserDto userDtoForDelete = userMapper.personToUserDto(userForDelete);
        userService.createUser(userDtoForDelete);

        // when

        when(userRepository.findById(userForDelete.getUserId()))
                .thenReturn(Optional.ofNullable(userForDelete));

        // then

        userService.deleteUserById(userForDelete.getUserId());
        verify(userRepository).deleteById(userForDelete.getUserId());
    }

    @Test
    @DisplayName("Deleting user. Expect NotFoundException.class exception.")
    void deleteUserById_whenUserNotFound_Test() {
        // given

        Person userForDelete = new Person();
        userForDelete.setUserId(22L);

        // when

        NotFoundException exception = new NotFoundException("Incorrect user id for deleting");
        doThrow(exception).when(userRepository)
                .deleteById(userForDelete.getUserId());

        // then

        assertThatThrownBy(() -> userService.deleteUserById(userForDelete.getUserId()))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Incorrect user id for deleting");
        verify(userRepository).deleteById(userForDelete.getUserId());
    }
}

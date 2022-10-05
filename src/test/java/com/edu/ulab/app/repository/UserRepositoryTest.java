package com.edu.ulab.app.repository;

import com.edu.ulab.app.config.SystemJpaTest;
import com.edu.ulab.app.entity.Person;
import com.vladmihalcea.sql.SQLStatementCountValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;

import static com.vladmihalcea.sql.SQLStatementCountValidator.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Тесты репозитория {@link UserRepository}.
 */
@SystemJpaTest
public class UserRepositoryTest {
    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void setUp() {
        SQLStatementCountValidator.reset();
    }

    @DisplayName("Saving user")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void insertUser() {
        //Given
        Person person = new Person();
        person.setFullName("Test name");
        person.setTitle("reader");
        person.setAge(30);

        //When
        Person result = userRepository.save(person);

        //Then
        assertNotNull(result);
        assertThat(result.getFullName()).isEqualTo("Test name");
        assertThat(result.getTitle()).isEqualTo("reader");
        assertThat(result.getAge()).isEqualTo(30);

        assertSelectCount(0);
        assertInsertCount(1);
        assertUpdateCount(0);
        assertDeleteCount(0);
    }

    @DisplayName("Updating user")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void updateUser() {
        // Given
        Person createdUser = new Person();
        createdUser.setFullName("Test name");
        createdUser.setTitle("reader");
        createdUser.setAge(111);
        userRepository.save(createdUser);

        Person updatedUser = new Person();
        updatedUser.setFullName("Test name");
        updatedUser.setTitle("reader");
        updatedUser.setAge(30);

        // When
        Person result = userRepository.save(updatedUser);

        // Then
        assertNotNull(result);
        assertThat(result.getAge()).isEqualTo(updatedUser.getAge());

        assertSelectCount(0);
        assertInsertCount(2);
        assertUpdateCount(0);
        assertDeleteCount(0);
    }

    @DisplayName("Getting user by id")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void getUserById() {
        // Given
        Person createdUser = new Person();
        createdUser.setFullName("Test name");
        createdUser.setTitle("reader");
        createdUser.setAge(111);
        userRepository.save(createdUser);

        // When
        Person result = userRepository.findById(createdUser.getUserId()).get();

        // Then
        assertNotNull(result);
        assertThat(result.getFullName()).isEqualTo(createdUser.getFullName());
        assertThat(result.getTitle()).isEqualTo(createdUser.getTitle());
        assertThat(result.getAge()).isEqualTo(createdUser.getAge());

        assertInsertCount(1);
        assertUpdateCount(0);
        assertDeleteCount(0);
    }

    @DisplayName("Deleting user")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void deleteUserById() {
        // Given
        Person user = new Person();
        user.setFullName("Test name");
        user.setTitle("reader");
        user.setAge(31);
        userRepository.save(user);

        // When
        userRepository.deleteById(user.getUserId());

        // Then
        // count = 1, потому что в репо остался ещё один юзер
        assertThat(userRepository.count()).isEqualTo(1);

        assertSelectCount(1);
        assertInsertCount(1);
        assertUpdateCount(0);
        assertDeleteCount(1);
    }
}

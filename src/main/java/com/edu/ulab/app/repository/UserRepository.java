package com.edu.ulab.app.repository;

import com.edu.ulab.app.entity.Person;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import javax.persistence.LockModeType;
import java.util.Optional;

public interface UserRepository extends CrudRepository<Person, Long> {

    /*
    User has books - book - started - comited status - other logic
    User has books - book - in progress
    User has books - book - finished
     */

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Person p where p.userId = :id")
    Optional<Person> findByIdForUpdate(@Param("id") Long id);
}

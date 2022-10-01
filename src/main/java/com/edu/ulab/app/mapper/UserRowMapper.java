package com.edu.ulab.app.mapper;

import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.entity.Person;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRowMapper implements RowMapper<Person> {

    @Override
    public Person mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Person user = new Person();
        user.setUserId(resultSet.getLong("id"));
        user.setFullName(resultSet.getString("full_name"));
        user.setFullName(resultSet.getString("title"));
        user.setAge(resultSet.getInt("age"));

        return user;
    }
}

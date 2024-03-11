package com.econoMe.gestorgastosback.repository;

import com.econoMe.gestorgastosback.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class UserRepositoryTests {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void whenFindAll_thenReturnUsers() {
        List<User> users = userRepository.findAll();
        System.out.println("AAAAAAAAAAAAAAAAAAAAAA");
        users.forEach(user -> System.out.println(user.getName()));
    }
}

package com.econoMe.gestorgastosback;

import com.econoMe.gestorgastosback.model.User;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Autowired;

import com.econoMe.gestorgastosback.repository.UserRepository;
import java.util.List;

@SpringBootApplication
public class GestorGastosBackApplication {


    public static void main(String[] args) {
        SpringApplication.run(GestorGastosBackApplication.class, args);
    }

}

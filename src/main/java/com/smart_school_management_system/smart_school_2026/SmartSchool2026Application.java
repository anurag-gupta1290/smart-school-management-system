package com.smart_school_management_system.smart_school_2026;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SmartSchool2026Application {
    public static void main(String[] args) {
        SpringApplication.run(SmartSchool2026Application.class, args);
        System.out.println("🚀 Smart School Management System Started!");
        System.out.println("📚 Visit: http://localhost:8083");
    }
}
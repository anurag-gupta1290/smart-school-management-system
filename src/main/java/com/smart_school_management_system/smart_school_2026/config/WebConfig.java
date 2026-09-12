package com.smart_school_management_system.smart_school_2026.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // ✅ NEW: CORS Configuration add ki
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")   // ✅ Spring Boot 3.x compatible
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD")
                .allowedHeaders("*")
                .exposedHeaders("Authorization", "Content-Disposition")
                .allowCredentials(true)
                .maxAge(3600);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Static resources (HTML, CSS, JS, Images) serve karein
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/")
                .setCachePeriod(3600);

        registry.addResourceHandler("/*.html")
                .addResourceLocations("classpath:/static/");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // URL mapping to HTML pages
        registry.addViewController("/").setViewName("redirect:/login.html");
        registry.addViewController("/login").setViewName("redirect:/login.html");
        registry.addViewController("/signup").setViewName("redirect:/signup.html");
        registry.addViewController("/admin").setViewName("redirect:/admin_dashboard.html");
        registry.addViewController("/teacher").setViewName("redirect:/teacher_dashboard.html");
        registry.addViewController("/student").setViewName("redirect:/student_dashboard.html");

        // Dashboard shortcuts
        registry.addViewController("/admin-dashboard").setViewName("redirect:/admin_dashboard.html");
        registry.addViewController("/teacher-dashboard").setViewName("redirect:/teacher_dashboard.html");
        registry.addViewController("/student-dashboard").setViewName("redirect:/student_dashboard.html");
    }
}
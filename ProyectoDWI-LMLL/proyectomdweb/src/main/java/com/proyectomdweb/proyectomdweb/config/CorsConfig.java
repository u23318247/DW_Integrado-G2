package com.proyectomdweb.proyectomdweb.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:4200", "http://127.0.0.1:4200", "http://localhost:8080")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }

    @Override
    public void addResourceHandlers(org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/angular/**")
                .addResourceLocations("classpath:/static/angular/");
    }

    @Override
    public void addViewControllers(org.springframework.web.servlet.config.annotation.ViewControllerRegistry registry) {
        registry.addViewController("/angular").setViewName("forward:/angular/index.html");
        registry.addViewController("/angular/").setViewName("forward:/angular/index.html");
    }
}

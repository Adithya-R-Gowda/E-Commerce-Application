package com.ecommerce.project.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * AppConfig serves as a configuration class for the e-commerce application.
 * It declares and configures beans required throughout the application.
 *
 * The primary purpose of this class is to manage application-wide settings
 * and provide beans like {@link ModelMapper} that facilitate object mapping.
 *
 * @author Adithya R
 */
@Configuration
public class AppConfig {

    /**
     * Creates and returns a {@link ModelMapper} bean.
     *
     * ModelMapper is a utility that simplifies object-to-object mapping, often used
     * for converting between DTOs and entity classes.
     *
     * @return a configured {@link ModelMapper} instance
     */
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}

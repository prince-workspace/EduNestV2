package com.edunest.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path parentFrontend = Paths.get("../frontend").toAbsolutePath().normalize();
        Path rootFrontend = Paths.get("frontend").toAbsolutePath().normalize();
        Path frontendDir = java.nio.file.Files.exists(parentFrontend) ? parentFrontend : rootFrontend;
        String frontendLocation = frontendDir.toUri().toString();

        registry.addResourceHandler("/**")
                .addResourceLocations(frontendLocation, "classpath:/static/");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("forward:/index.html");
    }
}

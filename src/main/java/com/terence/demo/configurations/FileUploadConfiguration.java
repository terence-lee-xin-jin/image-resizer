package com.terence.demo.configurations;

import jakarta.servlet.MultipartConfigElement;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;
import org.springframework.util.unit.DataUnit;

/**
 * Configuration to increase the file upload size limit to 2MB
 * */
@Configuration
public class FileUploadConfiguration {

    @Bean
    public MultipartConfigElement multipartConfigElement(){

        int MAX_FILE_SIZE_IN_MEGABYTE = 2;
        DataSize TWO_MEGABYTE = DataSize.of(MAX_FILE_SIZE_IN_MEGABYTE,
                DataUnit.MEGABYTES);
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize(TWO_MEGABYTE);
        factory.setMaxRequestSize(TWO_MEGABYTE);

        return factory.createMultipartConfig();
    }
}

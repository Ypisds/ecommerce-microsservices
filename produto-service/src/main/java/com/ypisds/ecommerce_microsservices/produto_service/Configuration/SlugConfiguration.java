package com.ypisds.ecommerce_microsservices.produto_service.Configuration;

import java.util.Locale;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.slugify.Slugify;

@Configuration 
public class SlugConfiguration {
    
    @Bean
    public Slugify slugify()
    {
        return Slugify.builder()
                .lowerCase(true)
                .locale(Locale.ENGLISH)
                .build();
    }
}

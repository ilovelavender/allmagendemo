package com.olegrubin.allmagendemo.config;

import com.olegrubin.allmagendemo.config.converter.AggregationFieldConverter;
import com.olegrubin.allmagendemo.config.converter.StatsPeriodConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new AggregationFieldConverter());
        registry.addConverter(new StatsPeriodConverter());
    }
}

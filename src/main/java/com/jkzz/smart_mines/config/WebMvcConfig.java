package com.jkzz.smart_mines.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOriginPattern("*");
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsFilter(urlBasedCorsConfigurationSource);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String path = System.getProperty("user.dir") + "\\Application\\map\\";
        path = path.replace("\\\\", "/");
        registry.addResourceHandler("/map/**").addResourceLocations("file:" + path);
        WebMvcConfigurer.super.addResourceHandlers(registry);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor(handle -> StpUtil.checkLogin()))
                .addPathPatterns("/alarm/**")
                .addPathPatterns("/device/**")
                .addPathPatterns("/deviceParameter/**")
                .addPathPatterns("/deviceType/**")
                .addPathPatterns("/hik/**")
                .addPathPatterns("/log/**")
                .addPathPatterns("/monitor/**")
                .addPathPatterns("/system/**")
                .addPathPatterns("/user/**")
                .excludePathPatterns("/system/verify")
                .excludePathPatterns("/system/register")
                .excludePathPatterns("/system/query")
                .excludePathPatterns("/system/shutdown")
                .excludePathPatterns("/user/loginPC")
                .excludePathPatterns("/user/loginAPP");

        WebMvcConfigurer.super.addInterceptors(registry);
    }

}

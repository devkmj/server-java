package kr.hhplus.be.server.config;

import kr.hhplus.be.server.infrastructure.filter.MDCFilter;
import kr.hhplus.be.server.infrastructure.interceptor.AuthInterceptor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Bean
    public FilterRegistrationBean<MDCFilter> mdcFilterRegistration(MDCFilter filter) {
        FilterRegistrationBean<MDCFilter> reg = new FilterRegistrationBean<>(filter);
        reg.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return reg;
    }

//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(new AuthInterceptor())
//                .addPathPatterns("/orders");
//    }
}
package org.y.notepad.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.y.notepad.web.converter.StringToDateConverter;
import org.y.notepad.web.converter.StringToEnumConverterFactory;
import org.y.notepad.web.interceptor.LoginInterceptor;

@Configuration
public class WebMvcConfigure implements WebMvcConfigurer {

    private final LoginInterceptor loginInterceptor;

    public WebMvcConfigure(LoginInterceptor loginInterceptor) {
        this.loginInterceptor = loginInterceptor;
    }

    // @Autowired
    // private SingleLoginInterceptor singleLoginInterceptor;

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToDateConverter());
        registry.addConverterFactory(new StringToEnumConverterFactory());
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor).addPathPatterns("/**");
        // registry.addInterceptor(singleLoginInterceptor).addPathPatterns("/**");
    }
}

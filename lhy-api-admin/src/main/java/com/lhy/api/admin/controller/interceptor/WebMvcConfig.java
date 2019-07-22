package com.lhy.api.admin.controller.interceptor;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.annotation.Resource;

/**
 * web mvc config
 *
 * @author liuhaiyuan
 */
@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {

    @Resource
    private PermissionInterceptor permissionInterceptor;
    @Resource
    private CookieInterceptor cookieInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(permissionInterceptor).addPathPatterns("/**");
        registry.addInterceptor(cookieInterceptor).addPathPatterns("/**");
        super.addInterceptors(registry);
    }
    /**
     * 表示这些配置的表示静态文件所处路径， 不用拦截
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");

        registry.addResourceHandler("/templates/**")
                .addResourceLocations("classpath:/templates/");
    }
}
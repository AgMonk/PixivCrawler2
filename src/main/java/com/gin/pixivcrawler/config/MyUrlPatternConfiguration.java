package com.gin.pixivcrawler.config;

import com.gin.pixivcrawler.service.ConfigService;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author bx002
 */
@Configuration
public class MyUrlPatternConfiguration implements WebMvcConfigurer {
    private final String rootPath;

    public MyUrlPatternConfiguration(ConfigService configService) {
        this.rootPath = configService.getConfig().getRootPath();
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //硬盘文件目录
        registry.addResourceHandler("/img/**").addResourceLocations("file:" + rootPath + (rootPath.endsWith("/") ? "" : "/"));

        //配置静态资源处理
        registry.addResourceHandler("/**", "/")
                .addResourceLocations(
                        "classpath:/resources/"
                        , "classpath:/static/"
                        , "classpath:/public/"
                        , "classpath:/META-INF/resources/"
                );
//        registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
    }
}

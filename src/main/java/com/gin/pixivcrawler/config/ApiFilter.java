package com.gin.pixivcrawler.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import javax.servlet.http.HttpServletRequest;

/**
 * Api前缀过滤
 *
 * @author Gin
 * @date 2021/2/14 13:19
 */
@Configuration
public class ApiFilter {

    @Bean
    public Filter apiFilter(){
        return (request, response, filterChain) -> {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            String path = httpRequest.getRequestURI();
            //前缀数组
            String[] prefixArr = new String[]{"/api"};
            for (String prefix : prefixArr) {
                //以前缀开头的请求
                if (path.startsWith(prefix)) {
                    //替换掉前缀
                    path = path.replace(prefix, "");
                    //转发
                    httpRequest.getRequestDispatcher(path).forward(request, response);
                    return;
                }
            }
            httpRequest.getRequestDispatcher(path).forward(request, response);
        };
    }
}

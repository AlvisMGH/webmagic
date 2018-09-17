package com.alvism.webmagic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Created by Administrator on 2018/9/13.
 */
@SpringBootApplication
@ComponentScans({
        @ComponentScan("springfox")
})
@EnableJpaRepositories(basePackages = "com.alvism.webmagic.repository")
@EntityScan(basePackages = "com.alvism.webmagic.model.entity")
@EnableCaching
public class WebMagicApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebMagicApplication.class, args);
    }

}

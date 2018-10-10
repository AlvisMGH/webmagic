package com.alvism.webmagic.runner;

import com.alvism.webmagic.processor.JdLoginProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
public class JdLoginRunner implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) {
        log.info("开始模拟登录京东...");
        long begin = Clock.systemUTC().millis();
        JdLoginProcessor.init().run();
        long end = Clock.systemUTC().millis();
        log.info("模拟登录京东结束：{}，消耗{}秒", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), (end - begin) / 1000);
    }

}

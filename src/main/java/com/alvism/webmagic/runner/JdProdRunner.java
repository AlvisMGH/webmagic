package com.alvism.webmagic.runner;

import com.alvism.webmagic.processor.JdProdPageProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class JdProdRunner implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) throws Exception {

        List<String> keyWords = new ArrayList<>(30);
        keyWords.add("手机");
        /*keyWords.add("平板电视");
        keyWords.add("燃气灶");
        keyWords.add("料理机");
        keyWords.add("取暖电器");
        keyWords.add("剃须刀");
        keyWords.add("仪器仪表");
        keyWords.add("数码相机");
        keyWords.add("运动相机");
        keyWords.add("存储卡");
        keyWords.add("闪光灯");
        keyWords.add("智能手环");
        keyWords.add("耳机/耳麦");
        keyWords.add("便携/无线音箱");
        keyWords.add("学生平板");
        keyWords.add("床单被罩");
        keyWords.add("节能灯");
        keyWords.add("保暖防护");
        keyWords.add("相框/照片墙");
        keyWords.add("宠物主粮");
        keyWords.add("笔记本");
        keyWords.add("主板");
        keyWords.add("SSD固态硬盘");
        keyWords.add("移动硬盘");
        keyWords.add("游戏机");
        keyWords.add("路由器");
        keyWords.add("投影机");
        keyWords.add("墨盒");
        keyWords.add("压力锅");
        keyWords.add("刀具套装");*/

        long size = 0;

        log.info("开始爬取京东商品列表数据...");
        long begin = Clock.systemUTC().millis();
        log.info("开始时间为：" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        for(String keyWord : keyWords){
            size += JdProdPageProcessor.init(keyWord, 100).run();
        }
        long end = Clock.systemUTC().millis();
        log.info("爬取结束：{}，共{}条数据，消耗{}秒", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), size, (end - begin) / 1000);
    }

}

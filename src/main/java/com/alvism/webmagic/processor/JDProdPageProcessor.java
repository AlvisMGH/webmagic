package com.alvism.webmagic.processor;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 京东商品列表数据爬虫
 */
@Slf4j
@Component
public class JDProdPageProcessor implements PageProcessor, ApplicationRunner {

    private static final String URL = "https://search.jd.com/Search?keyword=%E9%9B%B6%E9%A3%9F&enc=utf-8&wq=%E9%9B%B6%E9%A3%9F&page=";

    @Override
    public void process(Page page) {
        ChromeDriver driver = new ChromeDriver();
        /*try{
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        driver.get(page.getUrl().get());
        //driver.manage().window().maximize();
        //driver.manage().window().fullscreen();
        //driver.manage().window().maximize();
        //driver.executeScript("window.scrollTo(0, 10000)");
        //driver.manage().window().setSize(new Dimension(20000, 10000));
        /*try{
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        driver.executeScript("window.scrollTo(0, document.body.scrollHeight)");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        WebElement webElement = driver.findElement(By.xpath("/html/body"));
        String htmlStr = webElement.getAttribute("outerHTML");
        //获取html结构
        //Html html = page.getHtml();
        Html html = new Html(htmlStr);
        //获取商品列表结构
        List<Selectable> goodsList = html.xpath("//div[@id='J_goodsList']/ul/li[@class='gl-item']").nodes();
        //遍历商品列表结构
        for (Selectable item : goodsList) {
            //System.out.println(item.get());
            System.out.println(item.xpath("div[@class='gl-i-wrap']/div[@class='p-img']/a/@title") + ">>>>"
                    + item.xpath("div[@class='gl-i-wrap']/div[@class='p-price']/strong/i/text()") + ">>>>"
                    + item.xpath("div[@class='gl-i-wrap']/div[@class='p-img']/a/img/@src"));
        }
        /*//获取当前页码
        int pageNum = Integer.valueOf(URLUtil.resolve(page.getUrl().toString()).getValue("page"));
        //爬取100页商品数据，“100 * 2 - 1” 和 “pageNum + 2” 是根据京东分页算法得出
        if (pageNum < 100 * 2 - 1) {
            page.addTargetRequest(URL + (pageNum + 2));
        }*/
        driver.quit();
    }

    @Override
    public Site getSite() {
        return Site.me().setRetryTimes(10).setSleepTime(1000)
                .setUserAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.106 Safari/537.36");
    }

    @Override
    public void run(ApplicationArguments args) {
        log.info("开始爬取京东商品列表数据...");
        log.info("开始时间为：" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        Spider.create(new JDProdPageProcessor())
                .addUrl(URL + 1)
                .thread(5).run();
        log.info("爬取京东商品列表数据结束");
        log.info("结束时间为：" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }

}

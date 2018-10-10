package com.alvism.webmagic.processor;

import com.alvism.webmagic.util.ThreadUtil;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

/**
 * 模拟天猫登录，基于WebMagic爬虫框架
 * 该类需要借助Chrome浏览器的ChromeDriver驱动
 * 可根据操作系统到网站http://npm.taobao.org/mirrors/chromedriver/下载对应的最新驱动
 * Windows操作系统默认加载“C:\Windows\System32\chromedriver.exe”
 * Linux操作系统默认加载“/usr/bin/chromedriver.sh”
 */
@Slf4j
public class TmLoginProcessor implements PageProcessor {

    //谷歌浏览器参数
    private static ChromeOptions options;
    //private static FirefoxOptions options;

    static {
        //设置无头模式
        options = new ChromeOptions();
        //options = new FirefoxOptions();
        //options.addArguments("headless", "disable-gpu");
    }

    @Override
    public void process(Page page) {
        //创建谷歌浏览器驱动
        ChromeDriver driver = new ChromeDriver(options);
        //FirefoxDriver driver = new FirefoxDriver(options);
        String url = page.getUrl().get();
        //打开URL
        driver.get(url);
        //浏览器窗口最大化
        driver.manage().window().maximize();
        //执行滚动脚本，模拟浏览器滚动，完成图片懒加载
        //driver.executeScript("window.scrollTo(0, document.body.scrollHeight)");
        //滚动后，停留0.5s
        driver.findElementByXPath(".//*[@id=\"J_QRCodeLogin\"]/div[5]/a[1]").submit();
        driver.findElementByXPath(".//*[@id=\"TPL_username_1\"]").sendKeys("13570038865");
        driver.findElementByXPath(".//*[@id=\"TPL_password_1\"]").sendKeys("Jiaming2218");
        ThreadUtil.sleep(20000);
        //获取HTML结构
        //Html html = new Html(driver.getPageSource());


        //driver.quit();
    }

    @Override
    public Site getSite() {
        return Site.me()
                .setRetryTimes(5)
                .setSleepTime(100)
                .setTimeOut(10000)
                .setUserAgent(UserAgentSupport.random());
    }

    public static TmLoginProcessor init() {
        return new TmLoginProcessor();
    }


    public void run() {
        Spider.create(this)
                .addUrl("https://login.tmall.com/")
                .thread(6)
                .run();
    }

}

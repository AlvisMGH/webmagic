package com.alvism.webmagic.processor;

import com.alvism.webmagic.util.ThreadUtil;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

/**
 * 模拟京东登录，基于WebMagic爬虫框架
 * 该类需要借助Chrome浏览器的ChromeDriver驱动
 * 可根据操作系统到网站http://npm.taobao.org/mirrors/chromedriver/下载对应的最新驱动
 * Windows操作系统默认加载“C:\Windows\System32\chromedriver.exe”
 * Linux操作系统默认加载“/usr/bin/chromedriver.sh”
 */
@Slf4j
public class JdLoginProcessor implements PageProcessor {

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
        driver.findElementByXPath("//*[@id='content']/div[2]/div[1]/div/div[3]/a").click();
        driver.findElementByXPath("//*[@id='loginname']").sendKeys("13570038865");
        driver.findElementByXPath("//*[@id='nloginpwd']").sendKeys("AlvisM2218");
        driver.findElementByXPath("//*[@id='loginsubmit']").click();
        ThreadUtil.sleep(200000);
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

    public static JdLoginProcessor init() {
        return new JdLoginProcessor();
    }

    public void run() {
        Spider.create(this)
                .addUrl("https://passport.jd.com/new/login.aspx")
                .thread(6)
                .run();
    }

}

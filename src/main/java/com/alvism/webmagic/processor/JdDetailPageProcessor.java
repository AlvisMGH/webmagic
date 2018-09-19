package com.alvism.webmagic.processor;

import com.alvism.webmagic.util.ThreadUtil;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;

import java.util.List;

@Data
@NoArgsConstructor
@Accessors
public class JdDetailPageProcessor implements PageProcessor {

    private String url;

    @Override
    public void process(Page page) {

        ChromeOptions options = new ChromeOptions();
        options.addArguments("headless", "disable-gpu");

        ChromeDriver driver = new ChromeDriver(options);
        driver.get(page.getUrl().get());
        /*ThreadUtil.sleep(1000);*/
        Html html = new Html(driver.getPageSource());

        String selects = html.xpath("//*[@class=\"sku-name\"]/text()").get();
        System.out.println("商品名称：>>>" + selects);
        System.out.println("店铺名称：>>>" + html.xpath("//*[@id=\"crumb-wrap\"]/div/div[2]/div[2]/div[1]/div/a/@title").get());
        System.out.println("单价：>>>" + html.xpath("//*[@class='p-price']/span[2]/text()").get());
        //List<String> sku1Nmae = html.xpath("//div[@id='propertyDiv']/div[@id='sku1']/div/span/text()").all();

        /*----------------------颜色------------------------------------------------------*/
        String sku1 = html.xpath("//*[@id=\"choose-attr-1\"]/div[1]/text()").get();
        List<String> sku1src = html.xpath("//*[@id=\"choose-attr-1\"]/div[2]/div/a/img/@src").all();
        List<String> sku1Nmae = html.xpath("//*[@id=\"choose-attr-1\"]/div[2]/div/a/img/@alt").all();
        System.out.println(sku1 + "：>>>" + sku1src + sku1Nmae);
        /*------选择版本-----------------*/
        String banName = html.xpath("//*[@id=\"choose-attr-2\"]/div[1]/text()").get();
        List<String> ban = html.xpath("//*[@id=\"choose-attr-2\"]/div[2]/div/a/text()").all();
        System.out.println(banName + "：>>>" + ban);

        /*---------------------购买方式--------------------------------------------------*/
        String sku2Name = html.xpath("//*[@id=\"choose-attr-3\"]/div[1]/text()").get();
        List<String> sku2 = html.xpath("//*[@id=\"choose-attr-3\"]/div[2]/div/a/text()").all();
        System.out.print(sku2Name + "：>>>" + sku2);
        /*---------------------购买方式--------------------------------------------------*/
        String sku3Name = html.xpath("//*[@id=\"choose-attr-3\"]/div[2]/text()").get();
        List<String> sku3 = html.xpath("//*[@id=\"choose-type\"]/div[2]/div/a/text()").all();
        System.out.println(sku3Name + "：>>>" + sku3);
        /*---------------------------套装-----------------------------------------------*/
        String sku4Name = html.xpath("//*[@id=\"choose-suits\"]/div[1]/text()").get();
        List<String> sku4 = html.xpath("//*[@id=\"choose-suits\"]/div[2]/div/a/text()").all();
        System.out.println(sku4Name + "：>>>" + sku4);


//        String modeNmae = html.xpath("//*[@id=\"skuAttach1\"]/h3/text()").get();
//        List<String> mode = html.xpath("//*[@id=\"skuAttach1\"]/div/span/text()").all();
//        System.out.println(modeNmae + "：>>>" + mode);
//
//
//
//        System.out.println("链接：>>> https://item.m.jd.com/product/7694047.html");
        String imgurl = html.xpath("//*[@id=\"spec-img\"]/@src").get();
        System.out.println("图片：>>>" + imgurl);
        List<String> imgurlList = html.xpath("//*[@id=\"spec-list\"]/ul/li/img/@src").all();
        System.out.println("图片缩略图：>>>" + imgurlList);
//        //*[@id="commDesc"]/div/div[1]/img
//        //System.out.println("详情：>>>" + html.xpath("//*[@id='commDesc']//img/@src").all());
        //System.out.println("详情：>>>" + html.xpath("//*[@id=\"__01\"]/tbody/tr/td/img/@data-lazyload").all());
        System.out.println("详情：>>>" + html.xpath("//*[@id=\"J-detail-content\"]//img/@data-lazyload").all());

        driver.quit();
        // 部分二：定义如何抽取页面信息，并保存下来
//        page.addTargetRequests(page.getHtml().links().regex("(https://github\\.com/\\w+/\\w+)").all());
//        page.putField("author", page.getUrl().regex("https://github\\.com/(\\w+)/.*").toString());
//        page.putField("name", page.getHtml().xpath("//h1[@class='entry-title public']/strong/a/text()").toString());
//        if (page.getResultItems().get("name")==null){
//            //skip this page
//            page.setSkip(true);
//        }
        // 部分三：从页面发现后续的url地址来抓取
        // page.putField("readme", page.getHtml().xpath("//div[@id='readme']/tidyText()"));
        //String selects = page.getHtml().xpath("//div[@class='fn_wrap']/h1/div/text()").toString();
        //System.out.println("商品名称：>>>" + selects);
        // System.out.println("店铺名称：>>>" + page.getHtml().xpath("//div[@id='shopInfo']/div/span/text()"));
        //System.out.println("店铺名称：>>>" + page.getJson().jsonPath("").all());

//        System.out.println("单价：>>>" + page.getHtml().xpath("//*[@id='priceSaleChoice1']/text()").all());
//
//        System.out.println("规格：>>>" + page.getHtml().xpath("//div[@class='sku_wrap']/tidyText()").all());
//        System.out.println("链接：>>>" + page.getHtml().xpath("//span[@id='priceSale']/em/text()").all());
//        System.out.println("图片：>>>" + page.getHtml().xpath("//*[@id='loopImgUl']/li/img/@src").all());
//        List<String> imgurlList=page.getHtml().xpath("//*[@id='loopImgUl']/li/tidyText()").all();
//        System.out.println("imgurlList" + imgurlList);
        //System.out.println("详情：>>>" + page.getHtml().xpath("//span[@id='priceSale']/em/text()").all());
        // page.addTargetRequests(page.getHtml().links().regex("(https://item.jd\\.com/\\w+/\\w+)").all());
        //System.out.println("数据：>>>" + page.getHtml().xpath("//a[@class='ui-category-item']/text()").all());
        //System.out.println(page.getHtml().get());
    }

    @Override
    public Site getSite() {
        //爬虫配置，重试次数，等待时间，超时时间，浏览器模拟
        return Site.me().setRetryTimes(10).setSleepTime(1000).setTimeOut(3000)
                .setUserAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.106 Safari/537.36");
    }

    static JdDetailPageProcessor init(String url) {
        JdDetailPageProcessor processor = new JdDetailPageProcessor();
        processor.url = url;
        return processor;
    }

    void run() {
        Spider.create(this)
                .addUrl(this.url)
                .thread(1)
                .run();
    }

}

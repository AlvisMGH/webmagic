package com.alvism.webmagic.processor;

import com.alvism.webmagic.util.ThreadUtil;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;

import java.util.List;

/**
 * 京东商品列表数据爬虫，基于WebMagic爬虫框架
 * 该类需要借助Chrome浏览器的ChromeDriver驱动
 * 可根据操作系统到网站http://npm.taobao.org/mirrors/chromedriver/下载对应的最新驱动
 * Windows操作系统默认加载“C:\Windows\System32\chromedriver.exe”
 * Linux操作系统默认加载“/usr/bin/chromedriver.sh”
 */
@Slf4j
@Data
@NoArgsConstructor
@Accessors
public class JdProdPageProcessor implements PageProcessor {

    private static final String BASE_URL = "https://search.jd.com/Search?keyword=#KEYWORD&enc=utf-8&wq=#WQ&page=#PAGE";

    private String keyWord;
    private String url;
    private long size;
    private ChromeOptions options;
    private ChromeDriver driver;

    {
        options = new ChromeOptions();
        options.addArguments("headless", "disable-gpu");

        //创建Chrome浏览器驱动
        driver = new ChromeDriver(options);
    }

    @Override
    public void process(Page page) {

        //打开URL
        driver.get(page.getUrl().get());
        //屏幕最大化
        driver.manage().window().maximize();
        //获取浏览器可视化大小
        Dimension dimension = driver.manage().window().getSize();
        log.info("浏览器可视化宽度为：{}", dimension.getWidth());
        log.info("浏览器可视化高度为：{}", dimension.getHeight());
        ThreadUtil.sleep(2000);
        Html html = new Html(driver.getPageSource());

        //处理列表页
        if (page.getUrl().get().startsWith("https://search.jd.com/")) {


            /*//获取html文档高度
            long htmlHeight = (long) driver.executeScript("return document.documentElement.scrollHeight || document.body.scrollHeight;");
            log.info("html文档高度为：{}", htmlHeight);
            //声明当前滚动高度
            long currentScroll = 0;
            //设置滚动步长
            long step = (long) dimension.getHeight() - 20L;
            while (currentScroll < htmlHeight) {
                //执行滚动
                driver.executeScript("window.scrollTo(" + currentScroll + "," + (currentScroll + step) + ")");
                log.info("滚动事件执行：{} - {}", currentScroll, currentScroll + step);
                ThreadUtil.sleep(500);
                currentScroll += step;
            }*/

            //获取商品列表结构
            /*List<Selectable> goodsList = html.xpath("//div[@id='J_goodsList']/ul/li[@class='gl-item']").nodes();*/
            //遍历商品列表结构
            /*for (Selectable item : goodsList) {
                System.out.println(prodNameResolver(item));
                System.out.println(prodPriceResolver(item));
                System.out.println(coverPicResolver(item));
                JdDetailPageProcessor.init(prodDetailResolver(item)).run();
                System.out.println();
                System.out.println();
                System.out.println();
            }*/

            List<String> details = html.xpath(
                    "//div[@id='J_goodsList']/ul/li[@class='gl-item']/div[@class='gl-i-wrap']/div[@class='p-name p-name-type-2']/a/@href"
            ).all();

            this.size += details.size();

            for (String detail : details) {
                if (!detail.startsWith("https:")) {
                    detail = "https:" + detail;
                }
                page.addTargetRequest(detail);
            }


        /*//获取当前页码
        int pageNum = Integer.valueOf(URLUtil.resolve(page.getUrl().toString()).getValue("page"));
        //爬取100页商品数据，“100 * 2 - 1” 和 “pageNum + 2” 是根据京东分页算法得出
        if (pageNum < 100 * 2 - 1) {
            page.addTargetRequest(URL + (pageNum + 2));
        }*/

        } else { //处理详情页


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

            /*driver.quit();*/
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

        /*driver.close();
        driver.quit();*/
    }

    @Override
    public Site getSite() {
        //爬虫配置，重试次数，等待时间，超时时间，浏览器模拟
        return Site.me().setRetryTimes(10).setSleepTime(1000).setTimeOut(3000)
                .setUserAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.106 Safari/537.36");
    }

    /**
     * 商品名称解析器
     * 判断商品名称的Selectable中是否已图片开头（有可能包含京东精选等图片），若已图片开头，则去除图片
     * 去除搜索关键字高亮的标签
     *
     * @param prod
     * @return
     */
    private String prodNameResolver(Selectable prod) {
        String prodName = prod.xpath("div[@class='gl-i-wrap']/div[@class='p-name p-name-type-2']/a/em/html()").get();
        if (prodName.startsWith("<img")) {
            prodName = prodName.substring(prodName.indexOf(">") + 1);
        }
        return prodName.replace("<font class=\"skcolor_ljg\">", "")
                .replace("</font>", "");
    }

    /**
     * 商品价格解析器
     *
     * @param prod
     * @return
     */
    private String prodPriceResolver(Selectable prod) {
        return prod.xpath("div[@class='gl-i-wrap']/div[@class='p-price']/strong/i/text()").get();
    }

    /**
     * 封面图片解析器
     *
     * @param prod
     * @return
     */
    private String coverPicResolver(Selectable prod) {
        String result = prod.xpath("div[@class='gl-i-wrap']/div[@class='p-img']/a/img/@src").get();
        if (!result.startsWith("https:")) {
            result = "https:" + result;
        }
        return result;
    }

    /**
     * 商品详情超链接解析器
     *
     * @param prod
     * @return
     */
    private String prodDetailResolver(Selectable prod) {
        String result = prod.xpath("div[@class='gl-i-wrap']/div[@class='p-name p-name-type-2']/a/@href").get();
        if (!result.startsWith("https:")) {
            result = "https:" + result;
        }
        return result;
    }

    public static JdProdPageProcessor init(String keyWord) {
        JdProdPageProcessor processor = new JdProdPageProcessor();
        processor.keyWord = keyWord;
        processor.url = BASE_URL;
        processor.url = processor.url
                .replace("#KEYWORD", keyWord)
                .replace("#WQ", keyWord)
                .replace("#PAGE", "1");
        return processor;
    }

    public long run() {
        log.info("开始爬取：{}", this.url);
        Spider.create(this)
                .addUrl(this.url)
                .thread(5)
                .run();
        return this.size;
    }

}

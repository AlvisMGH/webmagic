package com.alvism.webmagic.processor;

import com.alvism.webmagic.util.URLUtil;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 淘宝商品数据爬虫，基于WebMagic爬虫框架
 * 该类需要借助Chrome浏览器的ChromeDriver驱动
 * 可根据操作系统到网站http://npm.taobao.org/mirrors/chromedriver/下载对应的最新驱动
 * Windows操作系统默认加载“C:\Windows\System32\chromedriver.exe”
 * Linux操作系统默认加载“/usr/bin/chromedriver.sh”
 */
@Slf4j
public class TbProdPageProcessor implements PageProcessor, BasePageProcessor {

    //搜索域名
    private static final String SEARCH_DOMAIN = "https://s.taobao.com/";
    //详情域名（淘宝）
    private static final String DETAIL_DOMAIN_TB = "https://detail.tmall.com/";
    //详情域名（天猫）
    private static final String DETAIL_DOMAIN_TM = "https://item.taobao.com/";
    //目标URL
    private static final String TARGET_URL = "https://s.taobao.com/search?app=mainSrp&cd=false&q=#Q&s=#S";
    //关键字
    private String keyWord;
    //谷歌浏览器参数
    private static ChromeOptions options;
    //谷歌浏览器驱动
    private ChromeDriver driver;
    //当前数量
    private long current;
    //爬取数量
    private long size;

    static {
        //设置无头模式
        options = new ChromeOptions();
        options.addArguments("headless", "disable-gpu");
    }

    {
        //创建驱动
        driver = new ChromeDriver(options);
    }

    @Override
    public void process(Page page) {
        String url = page.getUrl().get();
        //打开URL
        driver.get(url);
        //浏览器窗口最大化
        driver.manage().window().maximize();
        //执行滚动脚本，模拟浏览器滚动，完成图片懒加载
        //driver.executeScript("window.scrollTo(0, document.body.scrollHeight)");
        //滚动后，停留0.5s
        //ThreadUtil.sleep(500);
        //获取HTML结构
        Html html = new Html(driver.getPageSource());

        System.out.println(html.get());

        if (url.startsWith(DETAIL_DOMAIN_TB)) { //判断是否为淘宝详情页

            //商品编号
            String prodNum = html.xpath("*[@id='detail']/div[2]/div[1]/div[1]/ul[3]/li[2]/@title").get();
            //商品名称
            String prodName = html.xpath("//*[@class='sku-name']/text()").get();
            //商品价格
            String prodPrice = html.xpath("//*[@class='p-price']/span[2]/text()").get();
            //店铺名称
            String storeName = html.xpath("//*[@id='crumb-wrap']/div/div[2]/div[2]/div[1]/div/a/@title").get();
            System.out.println(prodNum);
            System.out.println(prodName);
            System.out.println(prodPrice);
            System.out.println(storeName);

            //商品属性
            List<Selectable> prodAttrs = html.xpath("//*div[@id='choose-attrs']/div[@class='li p-choose']").nodes();
            if (prodAttrs != null && prodAttrs.size() > 0) {
                for (Selectable prodAttr : prodAttrs) {
                    System.out.println(prodAttr.xpath("//div[@class='dt']/text()").get() + "：" + prodAttr.xpath("//div[@class='dd']/div[@class='item']/@data-value").all());
                }
            }

            //购买方式
            Selectable prodType = html.xpath("//div[@id='choose-type']");
            String prodTypeStyle = html.xpath("//div[@id='choose-type']/@style").get();
            if (prodType != null && prodTypeStyle != null && !prodTypeStyle.contains("display:none")) {
                System.out.println(prodType.xpath("//div[@class='dt']/text()").get() + "：" + prodType.xpath("//div[@class='dd']/div[@class='item']/a/text()").all());
            }

            //优惠套装
            Selectable prodSuit = html.xpath("//div[@id='choose-suits']");
            String prodSuitStyle = html.xpath("//div[@id='choose-suits']/@style").get();
            if (prodSuit != null && prodSuitStyle != null && !prodSuitStyle.contains("display:none")) {
                System.out.println(prodSuit.xpath("//div[@class='dt']/text()").get() + "：" + prodSuit.xpath("//div[@class='dd']/div[@class='item']/a/text()").all());
            }

            //商品图片
            //缩略图
            List<String> thumb = html.xpath("//*[@id='spec-list']/ul/li/img/@src").all();
            System.out.println("缩略图片：" + thumb);
            //标准图片
            List<String> img = thumb.parallelStream()
                    .map(str -> str.replace("n5", "n1").replace("s54x54_jfs", "s450x450_jfs")).collect(Collectors.toList());
            System.out.println("标准图片：" + img);
            //放大图片
            List<String> bigImg = thumb.parallelStream()
                    .map(str -> str.replace("n5", "n0").replace("s54x54_jfs", "jfs")).collect(Collectors.toList());
            System.out.println("放大图片：" + bigImg);
            //详情图片
            List<String> detail = html.xpath("//*[@id='J-detail-content']//img/@data-lazyload").all();
            System.out.println("详情图片：" + detail);

            System.out.println();

        } else if (url.startsWith(DETAIL_DOMAIN_TM)) { ////判断是否为天猫详情页
            //商品编号
            String prodNum = html.xpath("*[@id='detail']/div[2]/div[1]/div[1]/ul[3]/li[2]/@title").get();
            //商品名称
            String prodName = html.xpath("//*[@class='sku-name']/text()").get();
            //商品价格
            String prodPrice = html.xpath("//*[@class='p-price']/span[2]/text()").get();
            //店铺名称
            String storeName = html.xpath("//*[@id='crumb-wrap']/div/div[2]/div[2]/div[1]/div/a/@title").get();
            System.out.println(prodNum);
            System.out.println(prodName);
            System.out.println(prodPrice);
            System.out.println(storeName);

            //商品属性
            List<Selectable> prodAttrs = html.xpath("//*div[@id='choose-attrs']/div[@class='li p-choose']").nodes();
            if (prodAttrs != null && prodAttrs.size() > 0) {
                for (Selectable prodAttr : prodAttrs) {
                    System.out.println(prodAttr.xpath("//div[@class='dt']/text()").get() + "：" + prodAttr.xpath("//div[@class='dd']/div[@class='item']/@data-value").all());
                }
            }

            //购买方式
            Selectable prodType = html.xpath("//div[@id='choose-type']");
            String prodTypeStyle = html.xpath("//div[@id='choose-type']/@style").get();
            if (prodType != null && prodTypeStyle != null && !prodTypeStyle.contains("display:none")) {
                System.out.println(prodType.xpath("//div[@class='dt']/text()").get() + "：" + prodType.xpath("//div[@class='dd']/div[@class='item']/a/text()").all());
            }

            //优惠套装
            Selectable prodSuit = html.xpath("//div[@id='choose-suits']");
            String prodSuitStyle = html.xpath("//div[@id='choose-suits']/@style").get();
            if (prodSuit != null && prodSuitStyle != null && !prodSuitStyle.contains("display:none")) {
                System.out.println(prodSuit.xpath("//div[@class='dt']/text()").get() + "：" + prodSuit.xpath("//div[@class='dd']/div[@class='item']/a/text()").all());
            }

            //商品图片
            //缩略图
            List<String> thumb = html.xpath("//*[@id='spec-list']/ul/li/img/@src").all();
            System.out.println("缩略图片：" + thumb);
            //标准图片
            List<String> img = thumb.parallelStream()
                    .map(str -> str.replace("n5", "n1").replace("s54x54_jfs", "s450x450_jfs")).collect(Collectors.toList());
            System.out.println("标准图片：" + img);
            //放大图片
            List<String> bigImg = thumb.parallelStream()
                    .map(str -> str.replace("n5", "n0").replace("s54x54_jfs", "jfs")).collect(Collectors.toList());
            System.out.println("放大图片：" + bigImg);
            //详情图片
            List<String> detail = html.xpath("//*[@id='J-detail-content']//img/@data-lazyload").all();
            System.out.println("详情图片：" + detail);

            System.out.println();

        } else if (url.startsWith(SEARCH_DOMAIN)) { //判断是否为搜索页
            List<String> detailUrls = html.xpath("//div[@class='grid g-clearfix']/div[1]/div[2]/div[2]/a/@href").all();
            if (detailUrls != null && detailUrls.size() > 0) {
                //先将详情加入到待爬取列表中
                for (String detailUrl : detailUrls) {
                    if (!detailUrl.startsWith("https:")) {
                        detailUrl = "https:" + detailUrl;
                    }
                    System.out.println(detailUrl);
                    if (this.current < this.size) {
                        current += 1;
                        /*page.addTargetRequest(detailUrl);*/
                    }
                }
                //判断是否爬取了目标数量，否则开启下一页爬取
                if (current < this.size) {
                    //获取当前偏移量
                    int offset = Integer.valueOf(URLUtil.resolve(page.getUrl().toString()).getValue("s"));
                    //最大爬取100页数据，“offset + 44 > 100 * 44” 是根据淘宝分页算法得出
                    if (offset + 44 < 100 * 44) {
                        page.addTargetRequest(TARGET_URL.replace("#Q", this.keyWord).replace("#S", String.valueOf(offset + 44)));
                    }
                }
            }
        }

    }

    @Override
    public Site getSite() {
        return Site.me()
                .setRetryTimes(5)
                .setSleepTime(100)
                .setTimeOut(10000)
                .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36");
    }

    /**
     * 初始化爬虫
     *
     * @param keyWord
     * @return
     */
    public static TbProdPageProcessor init(String keyWord) {
        return init(keyWord, 100);
    }

    /**
     * 初始化爬虫（指定爬取限制数量）
     *
     * @param keyWord
     * @return
     */
    public static TbProdPageProcessor init(String keyWord, long size) {
        TbProdPageProcessor processor = new TbProdPageProcessor();
        processor.keyWord = keyWord;
        processor.size = size;
        return processor;
    }

    /**
     * 开始爬虫
     */
    @Override
    public long run() {
        Spider.create(this)
                .addUrl(TARGET_URL.replace("#Q", this.keyWord).replace("#S", String.valueOf(0)))
                .thread(6)
                .run();
        driver.quit();
        return current;
    }

}

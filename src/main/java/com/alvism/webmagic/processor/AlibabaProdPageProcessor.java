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
 * 阿里巴巴商品数据爬虫，基于WebMagic爬虫框架
 * 该类需要借助Chrome浏览器的ChromeDriver驱动
 * 可根据操作系统到网站http://npm.taobao.org/mirrors/chromedriver/下载对应的最新驱动
 * Windows操作系统默认加载“C:\Windows\System32\chromedriver.exe”
 * Linux操作系统默认加载“/usr/bin/chromedriver.sh”
 */
@Slf4j
public class AlibabaProdPageProcessor implements PageProcessor, BasePageProcessor {

    //搜索域名
    private static final String SEARCH_DOMAIN = "https://s.1688.com/";
    //详情域名
    private static final String DETAIL_DOMAIN = "https://detail.1688.com/";
    //目标URL
    private static final String TARGET_URL = "https://s.1688.com/selloffer/offer_search.htm?keywords=#KEYWORD&beginPage=#PAGE";
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

        if (url.startsWith(DETAIL_DOMAIN)) { //判断是否为详情页

            //商品编号
            //String prodNum = URLUtil.resolve(page.getUrl().toString()).getValue("id");
            //商品名称
            String prodName = html.xpath("//*[@id='mod-detail-title']/h1/text()").get();
            //商品价格
            String prodPrice = URLUtil.resolve(page.getUrl().toString()).getValue("prodPrice");
            //店铺名称
            String storeName = URLUtil.resolve(page.getUrl().toString()).getValue("storeName");
            //System.out.println(prodNum);
            System.out.println(prodName);
            System.out.println(prodPrice);
            System.out.println(storeName);

            /*//商品属性
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
            System.out.println("详情图片：" + detail);*/

            System.out.println();

        } else if (url.startsWith(SEARCH_DOMAIN)) { //判断是否为搜索页
            List<String> prodNums = html.xpath("//*[@id='sm-offer-list']/li[@t-offer-id]/@t-offer-id").all();
            List<String> detailUrls = html.xpath("//*[@class='imgofferresult-mainBlock']/div[3]/a/@href").all();
            List<String> prodPrices = html.xpath("//*[@class='imgofferresult-mainBlock']/div[2]/span[1]/text()").all();
            List<String> storeNames = html.xpath("//*[@class='imgofferresult-mainBlock']/div[4]/a[1]/text()").all();
            if (detailUrls != null && detailUrls.size() > 0) {
                //先将详情加入到待爬取列表中
                String detailUrl;
                for (int i = 0; i < detailUrls.size(); i++) {
                    detailUrl = detailUrls.get(i);
                    if (!detailUrl.startsWith("https:")) {
                        detailUrl = "https:" + detailUrl;
                    }
                    if(this.current < this.size){
                        current += 1;
                        if(detailUrl.contains("html?")){
                            page.addTargetRequest(detailUrl + "&prodPrice=" + prodPrices.get(i) + "&storeName=" + storeNames.get(i) + "&storeName=" + prodNums.get(i));
                        }else {
                            page.addTargetRequest(detailUrl + "?prodPrice=" + prodPrices.get(i) + "&storeName=" + storeNames.get(i) + "&storeName=" + prodNums.get(i));
                        }
                    }
                }
                //判断是否爬取了目标数量，否则开启下一页爬取
                if(current < this.size){
                    //获取当前页码
                    int pageNum = Integer.valueOf(URLUtil.resolve(page.getUrl().toString()).getValue("beginPage"));
                    //最大爬取100页数据，“pageNum + 1” 是根据阿里巴巴分页算法得出
                    if (pageNum < 100) {
                        page.addTargetRequest(TARGET_URL.replace("#KEYWORD", this.keyWord).replace("#PAGE", String.valueOf(pageNum + 1)));
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
                .setUserAgent(UserAgentSupport.random());
    }

    /**
     * 初始化爬虫
     *
     * @param keyWord
     * @return
     */
    public static AlibabaProdPageProcessor init(String keyWord) {
        return init(keyWord, 100);
    }

    /**
     * 初始化爬虫（指定爬取限制数量）
     *
     * @param keyWord
     * @return
     */
    public static AlibabaProdPageProcessor init(String keyWord, long size) {
        AlibabaProdPageProcessor processor = new AlibabaProdPageProcessor();
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
                .addUrl(TARGET_URL.replace("#KEYWORD", this.keyWord).replace("#PAGE", String.valueOf(1)))
                .thread(6)
                .run();
        driver.quit();
        return current;
    }

}

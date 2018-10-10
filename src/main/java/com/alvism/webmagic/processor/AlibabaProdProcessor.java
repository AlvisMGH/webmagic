package com.alvism.webmagic.processor;

import com.alvism.webmagic.util.URLUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
public class AlibabaProdProcessor implements PageProcessor, BaseProcessor {

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
    //当前数量
    private long current;
    //爬取数量
    private long size;

    static {
        //设置无头模式
        options = new ChromeOptions();
        options.addArguments("headless", "disable-gpu");
    }

    @Override
    public void process(Page page) {
        //创建谷歌浏览器驱动
        ChromeDriver driver = new ChromeDriver(options);
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
            String prodNum = URLUtil.resolve(page.getUrl().toString()).getValue("prodNum");
            //商品名称
            //*[@id="mod-detail-title"]/h1
            String prodName = html.xpath("//*[@id='mod-detail-title']/h1/text()").get();
            //商品价格
            String prodPrice = URLUtil.resolve(page.getUrl().toString()).getValue("prodPrice");
            //店铺名称
            String storeName = URLUtil.resolve(page.getUrl().toString()).getValue("storeName");
            System.out.println(prodNum);
            System.out.println(prodName);
            System.out.println(prodPrice);
            System.out.println(storeName);

            //商品属性
            //obj-leading
            Selectable leading = html.xpath("//div[@class='d-content']/div[@class='obj-leading']");
            if (leading != null && !StringUtils.isEmpty(leading.get())) {
                System.out.println(leading.xpath("//div[@class='obj-header']/span/text()").get() + "：" + leading.xpath("//div[@class='obj-content']/ul/li/div/a/span/text()").all());
            }
            //obj-sku
            Selectable sku = html.xpath("//div[@class='d-content']/div[@class='obj-sku']");
            if (sku != null && !StringUtils.isEmpty(sku.get())) {
                //这里判断属性值显示的是文本还是图片，问题则直接取文本，否则取span标签的title属性
                if (!StringUtils.isEmpty(sku.xpath("//div[@class='obj-content']/table/tbody/tr[1]/td[1]/span/text()").get())) {
                    System.out.println(sku.xpath("//div[@class='obj-header']/span/text()").get() + "：" + sku.xpath("//div[@class='obj-content']/table/tbody/tr/td[1]/span/text()").all());
                } else {
                    System.out.println(sku.xpath("//div[@class='obj-header']/span/text()").get() + "：" + sku.xpath("//div[@class='obj-content']/table/tbody/tr/td[1]/span/@title").all());
                }
            }

            //商品图片
            //缩略图
            List<String> thumb = html.xpath("//div[@id='dt-tab']/div/ul/li/div/a/img/@src").all();
            System.out.println("缩略图片：" + thumb);
            //标准图片
            List<String> img = thumb.parallelStream()
                    .map(str -> str.replace("60x60", "400x400")).collect(Collectors.toList());
            System.out.println("标准图片：" + img);
            //放大图片
            List<String> bigImg = thumb.parallelStream()
                    .map(str -> str.replace(".60x60", "")).collect(Collectors.toList());
            System.out.println("放大图片：" + bigImg);

            //详情图片
            String detailUrl = html.xpath("//div[@id='mod-detail-description']/div[1]/div[1]/@data-tfs-url").get();
            System.out.println("详情图片：" + detailUrl);

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
                    if (this.current < this.size) {
                        current += 1;
                        if (detailUrl.contains("html?")) {
                            page.addTargetRequest(detailUrl + "&prodPrice=" + prodPrices.get(i) + "&storeName=" + storeNames.get(i) + "&prodNum=" + prodNums.get(i));
                        } else {
                            page.addTargetRequest(detailUrl + "?prodPrice=" + prodPrices.get(i) + "&storeName=" + storeNames.get(i) + "&prodNum=" + prodNums.get(i));
                        }
                    }
                }
                //判断是否爬取了目标数量，否则开启下一页爬取
                if (current < this.size) {
                    //获取当前页码
                    int pageNum = Integer.valueOf(URLUtil.resolve(page.getUrl().toString()).getValue("beginPage"));
                    //最大爬取100页数据，“pageNum + 1” 是根据阿里巴巴分页算法得出
                    if (pageNum < 100) {
                        page.addTargetRequest(TARGET_URL.replace("#KEYWORD", this.keyWord).replace("#PAGE", String.valueOf(pageNum + 1)));
                    }
                }
            }
        }
        driver.quit();

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
    public static AlibabaProdProcessor init(String keyWord) {
        return init(keyWord, 100);
    }

    /**
     * 初始化爬虫（指定爬取限制数量）
     *
     * @param keyWord
     * @return
     */
    public static AlibabaProdProcessor init(String keyWord, long size) {
        AlibabaProdProcessor processor = new AlibabaProdProcessor();
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
        return current;
    }

}

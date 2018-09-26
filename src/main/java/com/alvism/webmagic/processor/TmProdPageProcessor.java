package com.alvism.webmagic.processor;

import com.alvism.webmagic.util.ThreadUtil;
import com.alvism.webmagic.util.URLUtil;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 天猫商品数据爬虫，基于WebMagic爬虫框架
 * 该类需要借助Chrome浏览器的ChromeDriver驱动
 * 可根据操作系统到网站http://npm.taobao.org/mirrors/chromedriver/下载对应的最新驱动
 * Windows操作系统默认加载“C:\Windows\System32\chromedriver.exe”
 * Linux操作系统默认加载“/usr/bin/chromedriver.sh”
 */
@Slf4j
public class TmProdPageProcessor implements PageProcessor, BasePageProcessor {

    //搜索域名
    private static final String SEARCH_DOMAIN = "https://list.tmall.com/";
    //详情域名（天猫）
    private static final String DETAIL_DOMAIN = "https://detail.tmall.com/";
    //目标URL
    private static final String TARGET_URL = "https://list.tmall.com/search_product.htm?type=p&q=#Q&s=#S";
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
            String prodNum = URLUtil.resolve(page.getUrl().toString()).getValue("id");
            //商品名称
            String prodName = html.xpath("//*[@id='J_DetailMeta']/div[1]/div[1]/div/div[1]/h1/a/text()").get();
            //商品价格
            String prodPrice = URLUtil.resolve(page.getUrl().toString()).getValue("prodPrice");
            //店铺名称
            String storeName = URLUtil.resolve(page.getUrl().toString()).getValue("storeName");
            System.out.println(prodNum);
            System.out.println(prodName);
            System.out.println(prodPrice);
            System.out.println(storeName);

            //商品属性
            List<Selectable> prodAttrs = html.xpath("//*[@id='J_DetailMeta']/div[1]/div[1]/div/div[4]/div/div/dl[@class='tm-sale-prop']").nodes();
            if (prodAttrs != null && prodAttrs.size() > 0) {
                for (Selectable prodAttr : prodAttrs) {
                    System.out.println(prodAttr.xpath("//dt/text()").get() + "：" + prodAttr.xpath("//dd/ul/li/a/span/text()").all());
                }
            }

            //商品图片
            //缩略图
            List<String> thumb = html.xpath("//*[@id='J_UlThumb']/li/a/img/@src").all();
            System.out.println("缩略图片：" + thumb);
            //标准图片
            List<String> img = thumb.parallelStream()
                    .map(str -> str.replace("60x60", "430x430")).collect(Collectors.toList());
            System.out.println("标准图片：" + img);
            //放大图片
            List<String> bigImg = thumb.parallelStream()
                    .map(str -> str.replace("_60x60q90.jpg", "")).collect(Collectors.toList());
            System.out.println("放大图片：" + bigImg);

            //详情图片
            List<String> detail = html.xpath("//*[@id='description']/div/p[2]/img/@src").all();
            System.out.println("详情图片：" + detail);

            System.out.println();

        } else if (url.startsWith(SEARCH_DOMAIN)) { //判断是否为搜索页
            System.out.println(driver.getPageSource());
            //*[@id="J_ItemList"]/div[1]/div/div[3]/a
            //*[@id="J_ItemList"]/div[3]/div/div[3]/a[1]
            //*[@id="J_ItemList"]/div[4]/div/div[3]/a
            //*[@id="J_ItemList"]/div[5]/div/div[3]/a[1]
            List<String> detailUrls = html.xpath("//*[@id='J_ItemList']/div/div/*[@class='productImg-wrap']/a/@href").all();
            List<String> prodPrices = html.xpath("//*[@id='J_ItemList']/div/div/*[@class='productPrice']/em/text()").all();
            List<String> storeNames = html.xpath("//*[@id='J_ItemList']/div/div/*[@class='productShop']/a/text()").all();
            System.out.println("detailUrl:" + detailUrls.size());
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
                        page.addTargetRequest(detailUrl + "&prodPrice=" + prodPrices.get(i) + "&storeName=" + storeNames.get(i));
                    }
                }
                //判断是否爬取了目标数量，否则开启下一页爬取
                if (current < this.size) {
                    //获取当前偏移量
                    int offset = Integer.valueOf(URLUtil.resolve(page.getUrl().toString()).getValue("s"));
                    //最大爬取100页数据，“offset + 60 > 100 * 60” 是根据天猫分页算法得出
                    if (offset + 60 < 100 * 60) {
                        page.addTargetRequest(TARGET_URL.replace("#Q", this.keyWord).replace("#S", String.valueOf(offset + 60)));
                    }
                }
            }
        }
        driver.quit();
    }

    @Override
    public Site getSite() {
        return Site.me()
                .setRetryTimes(10)
                .setSleepTime(100)
                .setTimeOut(10000)
                .setUserAgent(UserAgentSupport.random())
                .setDisableCookieManagement(true);
    }

    /**
     * 初始化爬虫
     *
     * @param keyWord
     * @return
     */
    public static TmProdPageProcessor init(String keyWord) {
        return init(keyWord, 100);
    }

    /**
     * 初始化爬虫（指定爬取限制数量）
     *
     * @param keyWord
     * @return
     */
    public static TmProdPageProcessor init(String keyWord, long size) {
        TmProdPageProcessor processor = new TmProdPageProcessor();
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
                .thread(5)
                .run();
        return current;
    }

}

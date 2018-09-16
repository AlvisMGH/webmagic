package com.alvism.webmagic.processor;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

/**
 * Created by Administrator on 2018/9/13.
 */
public class JDProductPageProcessor implements PageProcessor {

    private Site site =
            Site.me().setRetryTimes(3).setSleepTime(1000)
                    .setUserAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.106 Safari/537.36");

    @Override
    public void process(Page page) {
        System.out.println(page.getHtml().get());
        /*page.addTargetRequests(
                page.getHtml().links().regex("(https://search.jd.com/Search?keyword=手机&enc=utf-8&wq=手机)")
                        .all()
        );*/
        /*page.putField("author", page.getUrl().regex("https://github\\.com/(\\w+)/.*").toString());
        page.putField("name", page.getHtml().xpath("//h1[@class='entry-title public']/strong/a/text()").toString());
        if (page.getResultItems().get("name")==null){
            //skip this page
            page.setSkip(true);
        }*/
        page.putField("sku-name", page.getHtml().xpath("//div[@class='sku-name']/text()").toString());
        page.putField("price", page.getHtml().xpath("//span[@class='price J-p-5089253']/text()").toString());
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
        Spider
                .create(new JDProductPageProcessor())
                .addUrl("https://detail.1688.com/offer/576865078308.html?spm=b26110380.sw1688.mof001.1.1c7a30e1Daf3OU&tracelog=p4p&clickid=f800fb2389514839b1fdd8f694d0201f&sessionid=c7f18c7d0804dd477f566a26c5e834c4")
                .thread(5).run();
    }

}

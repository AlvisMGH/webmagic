package com.alvism.webmagic.util;

public class URLUtil {

    /**
     * 解析url字符串为URL解析器
     * @param url
     * @return
     */
    public static URLResolver resolve(String url){
        return new URLResolver(url);
    }

}

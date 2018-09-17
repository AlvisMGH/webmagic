package com.alvism.webmagic.util;

import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

@Data
@Accessors(chain = true)
public class URLResolver {

    private String url;

    private Map<String, String> params;

    /**
     * 解析url，封装url参数到map集合中
     * @param url
     */
    URLResolver(String url) {
        this.url = url;
        this.params = new HashMap<>();
        if (!StringUtils.isEmpty(url)) {
            int paramsIndx = StringUtils.indexOf(url, "?");
            int pointIndx = StringUtils.lastIndexOf(url, "#");
            if (paramsIndx != -1 && paramsIndx + 1 <= StringUtils.length(url) - 1) {
                url = StringUtils.substring(url, paramsIndx + 1,
                        pointIndx == -1 ? StringUtils.length(url)
                                : pointIndx);
                String[] params = url.split("&");
                for (String param : params) {
                    String[] kv = param.split("=");
                    if (kv.length > 1) {
                        this.params.put(kv[0], kv[1]);
                    }
                }
            }
        }
    }

    /**
     * 根据参数key获取对应的value
     * @param key
     * @return
     */
    public String getValue(String key) {
        if (StringUtils.isEmpty(key)) {
            return null;
        }
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (StringUtils.equals(key, entry.getKey())) {
                return entry.getValue();
            }
        }
        return null;
    }

}

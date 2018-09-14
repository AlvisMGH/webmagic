package com.alvism.webmagic.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by Administrator on 2018/9/13.
 */
@Controller
@RequestMapping("/api/product/magic")
public class ProductMagicApi {

    @ResponseBody
    @RequestMapping("/detail")
    public String detail() {
        return "";
    }

}

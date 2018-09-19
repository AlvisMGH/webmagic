package com.alvism.webmagic.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ThreadUtil {

    public static void sleep(long millis){
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            log.error("线程休眠异常：", e);
        }
    }

}

package com.aliyun.sms.config;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * @description:
 * @author: zheng-fx
 * @time: 2020/3/21 0021 19:14
 */
public class DateUtils {
    
    //获取当前时间--秒数时间戳
    public static Long getMilli(){
        return LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8"));
    }

    //获取当前时间--毫秒数时间戳
    public static Long getMilliss(){
        return LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli();
    }
    
    
    //获取两个时间戳的间隔时长（单位秒）
    public static long getBetween(Long end,Long start){
        return (end-start)/(1000*60);
    }
    public static void main(String[] args) {
       
    }
}

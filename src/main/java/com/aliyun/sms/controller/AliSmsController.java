package com.aliyun.sms.controller;

import com.aliyun.sms.config.CommonResult;
import com.aliyun.sms.service.AliSmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @description:
 * @author: zheng-fx
 * @time: 2020/3/22 0022 15:24
 */
@RestController
public class AliSmsController {
    
    @Autowired
    AliSmsService aliSmsService;

    @PostMapping("/sendMsg")
    public CommonResult sendMsg(HttpServletRequest req){
        
        return aliSmsService.sendMsg(req);
    }
}

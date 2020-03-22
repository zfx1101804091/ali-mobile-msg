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

    /*
     * 功能描述: 
     *  TODO 发送短信验证码
     * @Param: [req]
     * @Return: com.aliyun.sms.config.CommonResult
     * @Author: Administrator
     * @Date: 2020/3/22 0022 19:25
     */
    @PostMapping("/sendMsg")
    public CommonResult sendMsg(HttpServletRequest req){
        
        return aliSmsService.sendMsg(req);
    }
}

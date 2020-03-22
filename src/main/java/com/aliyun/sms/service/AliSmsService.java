package com.aliyun.sms.service;

import com.aliyun.sms.config.CommonResult;

import javax.servlet.http.HttpServletRequest;

public interface AliSmsService {

    CommonResult sendMsg(HttpServletRequest req);
}

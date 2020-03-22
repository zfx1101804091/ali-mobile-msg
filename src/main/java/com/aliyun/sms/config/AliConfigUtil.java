package com.aliyun.sms.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @author: zheng-fx
 * @time: 2020/3/22 0022 01:52
 */
@Component
public class AliConfigUtil {

    @Value("${ali.config.regionid}")
    public String regionid;

    @Value("${ali.config.accesskeyid}")
    public String accesskeyid;

    @Value("${ali.config.accesssecret}")
    public String accesssecret;

    @Value("${ali.mobilemsg.signName}")
    public String signName;

    @Value("${ali.mobilemsg.templateCode}")
    public String templateCode;

    @Value("${ali.mobilemsg.effectiveTime}")
    public String timeOut;
    
}

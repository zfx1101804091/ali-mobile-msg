package com.aliyun.sms.model;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @author: zheng-fx
 * @time: 2020/3/21 0021 14:29
 */
@Component
@ConfigurationProperties(prefix = "ali.config")
@PropertySource("classpath:/application.properties")
public class AliConfigBean {

    private String regionid;
    private String accesskeyid;
    private String accesssecret;
    

    @Override
    public String toString() {
        return "AliConfigBean{" +
                "regionid='" + regionid + '\'' +
                ", accesskeyid='" + accesskeyid + '\'' +
                ", accesssecret='" + accesssecret + '\'' +
                '}';
    }

    public String getRegionid() {
        return regionid;
    }

    public void setRegionid(String regionid) {
        this.regionid = regionid;
    }

    public String getAccesskeyid() {
        return accesskeyid;
    }

    public void setAccesskeyid(String accesskeyid) {
        this.accesskeyid = accesskeyid;
    }

    public String getAccesssecret() {
        return accesssecret;
    }

    public void setAccesssecret(String accesssecret) {
        this.accesssecret = accesssecret;
    }
}

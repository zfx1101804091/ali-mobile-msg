package com.aliyun.sms;

import com.aliyun.sms.model.AliConfigBean;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;


@RunWith(SpringRunner.class)
@SpringBootTest
public class AliConfigAppTest {

    @Autowired
    AliConfigBean aliConfigBean;
    
    @Autowired
    StringRedisTemplate redisTemplate;
    
    @Test
    public void test(){

        String regionid = aliConfigBean.getRegionid();
        String accesskeyid = aliConfigBean.getAccesskeyid();
        String accesssecret = aliConfigBean.getAccesssecret();

        DefaultProfile profile = DefaultProfile.getProfile(regionid,accesskeyid,accesssecret);
        IAcsClient client = new DefaultAcsClient(profile);

        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain("dysmsapi.aliyuncs.com");
        request.setSysVersion("2017-05-25");
        request.setSysAction("SendSms");
        request.putQueryParameter("RegionId", "cn-hangzhou");
        request.putQueryParameter("PhoneNumbers", "18549823359");
        request.putQueryParameter("SignName", "华丽服饰特卖");
        request.putQueryParameter("TemplateCode", "SMS_186575948");
        request.putQueryParameter("TemplateParam", "{\"code\":10085}");
        try {
            CommonResponse response = client.getCommonResponse(request);
            System.out.println(response.getData());
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test2(){
        redisTemplate.opsForValue().set("name", "{\"name\":\"zhangsan\"}");
    }
}
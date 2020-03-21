package com.aliyun.sms.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.sms.config.CommonResult;
import com.aliyun.sms.config.DateUtils;
import com.aliyun.sms.model.AliConfigBean;
import com.aliyun.sms.model.AliSmsConfig;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.SecureRandom;
import java.util.Random;

import static com.aliyun.sms.config.DateUtils.*;

/**
 * 阿里发送短信接口实现
 * 
 */

@RestController
public class SendSms {

    @Autowired
    AliConfigBean aliConfigBean;
    
    @Autowired
    StringRedisTemplate redisTemplate;
    
    private static final ThreadLocal<String> threadLocal = new ThreadLocal();
    
    @PostMapping("message/sendMsg")
    public CommonResult sendMsg(HttpServletRequest req){

        String regionid = aliConfigBean.getRegionid();
        String accesskeyid = aliConfigBean.getAccesskeyid();
        String accesssecret = aliConfigBean.getAccesssecret();

        DefaultProfile profile = DefaultProfile.getProfile(regionid,accesskeyid,accesssecret);
        IAcsClient client = new DefaultAcsClient(profile);
        
        try {
        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain("dysmsapi.aliyuncs.com");
        request.setSysVersion("2017-05-25");
        request.setSysAction("SendSms");
//        request.putQueryParameter("RegionId", regionid);
        request.putQueryParameter("PhoneNumbers", getPostbody(req));
        request.putQueryParameter("SignName", AliSmsConfig.SignName);
        request.putQueryParameter("TemplateCode", AliSmsConfig.TemplateCode);
        //将验证码放到全局变量里
        threadLocal.set(getNonce_str());
        request.putQueryParameter("TemplateParam", "{\"code\":"+threadLocal.get()+"}");

            String str = redisTemplate.opsForValue().get("msg-code");
            JSONObject object = JSON.parseObject(str);
            if(!StringUtils.isEmpty(object)) {
                long timeBe = getBetween(getMilliss(),object.getLongValue("timestamp"));
                //验证码获取时间不足1min，不去获取验证码
                if (timeBe < AliSmsConfig.timeOut) {
                    return new CommonResult().fail();
                }
            }
            CommonResponse response = client.getCommonResponse(request);
//            System.out.println(response.getData());
            JSONObject jsonObject = JSON.parseObject(response.getData());
            //判断验证码是否发送成功
            if("OK".equals(jsonObject.get("Code"))){
                //说明验证码发送成功了 存redis
                redisTemplate.opsForValue().
                        set("msg-code", 
                                "{\"code\":"+threadLocal.get()+",\"timestamp\":"+ getMilliss()+"}");

                System.out.println("hahah"+redisTemplate.opsForValue().get("msg-code"));
            }
            return new CommonResult().ok(threadLocal.get(), getMilliss(),jsonObject); 
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 获取post请求体body数据
     * @return
     */
    public static String getPostbody(HttpServletRequest request) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));

        String str = "";
        String wholeStr = "";
        while((str = reader.readLine()) != null){//一行一行的读取body体里面的内容；
            wholeStr += str;
        }
        JSONObject t=JSON.parseObject(wholeStr);//转化成json对象
        String phone = (String) t.get("phone");//得到想要的参数
        return phone;

    }
    
    

    /**
     * 获取长度为 6 的随机数字
     * @return 随机数字
     */
    public static String getNonce_str() {

        String SYMBOLS = "0123456789"; // 数字
        // 字符串
        // private static final String SYMBOLS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"; 
        Random RANDOM = new SecureRandom();
        
        // 如果需要4位，那 new char[4] 即可，其他位数同理可得
        char[] nonceChars = new char[6];

        for (int index = 0; index < nonceChars.length; ++index) {
            nonceChars[index] = SYMBOLS.charAt(RANDOM.nextInt(SYMBOLS.length()));
        }

        return new String(nonceChars);
    }

}
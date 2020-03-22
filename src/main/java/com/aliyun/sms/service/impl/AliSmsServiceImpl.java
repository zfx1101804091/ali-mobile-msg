package com.aliyun.sms.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.sms.config.AliConfigUtil;
import com.aliyun.sms.config.CommonResult;
import com.aliyun.sms.service.AliSmsService;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import lombok.extern.slf4j.Slf4j;
import org.jasypt.util.text.BasicTextEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.SecureRandom;
import java.util.Random;

import static com.aliyun.sms.config.DateUtils.getBetween;
import static com.aliyun.sms.config.DateUtils.getMilliss;

/**
 * 阿里发送短信接口实现
 * 
 */
@Slf4j
@Service
public class AliSmsServiceImpl implements AliSmsService {
    
    @Autowired
    StringRedisTemplate redisTemplate;
    
    @Autowired
    AliConfigUtil aliConfigUtil;
    
    private static final ThreadLocal<String> threadLocal = new ThreadLocal();
    
    @Override
    public CommonResult sendMsg(HttpServletRequest req){

        BasicTextEncryptor encryptor = new BasicTextEncryptor();
        encryptor.setPassword("zheng1314");

        DefaultProfile profile = DefaultProfile.getProfile(
                encryptor.decrypt(aliConfigUtil.regionid),
                encryptor.decrypt(aliConfigUtil.accesskeyid),
                encryptor.decrypt(aliConfigUtil.accesssecret)
        );
        
        IAcsClient client = new DefaultAcsClient(profile);


        try {

            JSONObject postbody = JSON.parseObject(getPostbody(req));
            String timeOut = postbody.getString("effectiveTime");//验证码过期时间
            String phone = postbody.getString("phone");//手机号

            CommonRequest request = new CommonRequest();
            request.setSysMethod(MethodType.POST);
            request.setSysDomain("dysmsapi.aliyuncs.com");
            request.setSysVersion("2017-05-25");
            request.setSysAction("SendSms");
    //        request.putQueryParameter("RegionId", regionid);
            request.putQueryParameter("PhoneNumbers", phone);
            request.putQueryParameter("SignName",encryptor.decrypt(aliConfigUtil.signName));
            request.putQueryParameter("TemplateCode",encryptor.decrypt(aliConfigUtil.templateCode));
            //将验证码放到全局变量里
            threadLocal.set(getNonce_str());
            //发送验证码和验证码有效期
            request.putQueryParameter("TemplateParam", getJsonString());
    
                String str = redisTemplate.opsForValue().get("msg:"+phone+":code");
                JSONObject object = JSON.parseObject(str);
                if(!StringUtils.isEmpty(object)) {
                    long timeBe = getBetween(getMilliss(),object.getLongValue("timestamp"));
                    //验证码获取时间不足5min，不去获取验证码
                    if (timeBe < Long.parseLong(timeOut)) {
                        log.info("验证码获取时间不足{}min，不去重复获取验证码",timeOut);
                        return new CommonResult().fail(Long.parseLong(timeOut));
                    }
                }

                log.info("手机号为：{}用户，开始申请验证码！",phone);
                CommonResponse response = client.getCommonResponse(request);
    //            System.out.println(response.getData());
                JSONObject jsonObject = JSON.parseObject(response.getData());
                //判断验证码是否发送成功
                if("OK".equals(jsonObject.get("Code"))){
                    log.info("手机号为：{}用户，申请验证码成功！开始存入redis",phone);
                    //说明验证码发送成功了 存redis
                    redisTemplate.opsForValue().
                            set("msg:"+phone+":code", 
                                    "{\"code\":"+threadLocal.get()+",\"timestamp\":"+ getMilliss()+"}");
    
                    log.info("获取redis中手机号为{}：的验证码是：{}",phone,redisTemplate.opsForValue().get("msg:"+phone+":code"));
                    return new CommonResult().ok(threadLocal.get(), getMilliss(),jsonObject);
                }else{
                    log.error("手机号为：{}用户，申请验证码异常！异常原因为：{}",phone,jsonObject.get("Message"));
                    return new CommonResult().fail(Long.parseLong(timeOut));
                }
            
            } catch (ServerException e) {
                e.printStackTrace();
                log.error("ServerException异常详情：{}",e.getMessage());
            } catch (ClientException e) {
                e.printStackTrace();
            log.error("ClientException异常详情：{}",e.getMessage());
            }catch (IOException e){
                e.printStackTrace();
            log.error("IOException异常详情：{}",e.getMessage());
            }
            return null;
    }


    /**
     * 获取post请求体body数据
     *  condition 条件
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
        String phone = (String) t.get("phone");//得到手机号
        String effectiveTime = (String) t.get("effectiveTime");//得到验证码有效期时间
       
        return "{\"phone\":"+phone+",\"effectiveTime\":"+effectiveTime+"}";
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

    //解决阿里短信接口第一位为 0 时 ，在手机上不显示0的问题  必须使用标准json格式
    public static String getJsonString(){
        //{\"code\":"+threadLocal.get()+"}
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code",threadLocal.get());
//        jsonObject.put("code","001234");
        return JSONObject.toJSONString(jsonObject);
    }
}
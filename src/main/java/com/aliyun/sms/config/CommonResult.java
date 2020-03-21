package com.aliyun.sms.config;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * @description:
 * @author: zheng-fx
 * @time: 2020/3/21 0021 19:23
 */

@Data
@NoArgsConstructor
public class CommonResult {
    
    private int status;
    private String codeNum;
    private Long CodeTime;
    private Object result;
    

    public CommonResult(int status, String codeNum, Long codeTime, Object result) {
        this.status = status;
        this.codeNum = codeNum;
        CodeTime = codeTime;
        this.result = result;
    }

    public CommonResult ok(String codeNum, Long codeTime, Object result) {
        this.status=200;
        this.codeNum = codeNum;
        this.CodeTime = codeTime;
        this.result = result;
        return this;
    }

    public CommonResult fail(long timOut) {
        this.status=405;
        this.codeNum = "";
        this.CodeTime = DateUtils.getMilliss();
        /*
        * {"Message":"OK","RequestId":"00055DF3-1773-43EB-8FF1-989707DD8FA4","BizId":"787821084781582242^0","Code":"OK"}
        * */
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Message","申请短信验证码不足"+timOut+"分钟");
        jsonObject.put("Code","fail");
        this.result = jsonObject;
        return this;
    }
}

package com.myself.best.warehousing.utils;

import com.alibaba.fastjson.JSONException;
import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;
import com.github.qcloudsms.httpclient.HTTPException;

import java.io.IOException;

/**
 * @Description: 发送短信工具类
 * @Date Created in 14:49 2022/5/23
 * @Author: Chen_zhuo
 * @Modified By
 */
public class SMSUtils {
    /**
     * 发送短信
     * @param phoneNumber
     * @param templateId
     * @param params
     * @param signName
     */
    public static void sendMessage(String phoneNumber, Integer templateId, String[] params, String signName) {

        try {

            SmsSingleSender sSender = new SmsSingleSender(1400682244, "539c8d827c453e512e4867913ebda339");
            SmsSingleSenderResult result = sSender.sendWithParam("86",
                    phoneNumber, templateId, params, signName , "", "");

            System.out.println(result);
            System.out.println("发送短信成功");

        } catch (HTTPException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {

        }
    }
}

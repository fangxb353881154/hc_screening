package com.thinkgem.jeesite.common.utils;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * Created by asus on 2016/11/7.
 */
public class ResultUtils {
    public static String SUCCESS_STATE = "1";
    public static String FAILURE_STATE = "0";

    public static Map<String,Object> getSuccess(){
        return getSuccess("请求成功！");
    }

    public static Map<String,Object> getFailure(){
        return getFailure("请求失败！");
    }

    public static Map<String, Object> getSuccess(String message) {
        return getResult(SUCCESS_STATE, message);
    }

    public static Map<String, Object> getFailure(String message) {
        return getResult(FAILURE_STATE, message);
    }

    public static Map<String, Object> getResult(String flag, String message) {
        Map<String, Object> result = Maps.newHashMap();
        result.put("flag", flag);
        result.put("message", message);
        return result;
    }
}

package com.github.quartz.examples.util;


import com.alibaba.fastjson.JSONObject;

/**
 * 请求响应结果进行统一封装
 */
public class JsonUtil {
    private static final Boolean SUCCESS = true;
    private static final Boolean FAILURE = false;
    private static final String IS_SUCCESS = "isSuccess";
    private static final String ERROR_MESSAGE = "errorMessage";

    /**
     * 请求成功
     *
     * @param resultData
     * @return
     */
    public static JSONObject success(Object resultData) {

        JSONObject resultJSON = new JSONObject();

        resultJSON.put("data", resultData);
        resultJSON.put(IS_SUCCESS, SUCCESS);

//        return resultJSON.toJSONString();
        return resultJSON;
    }

    /**
     * 请求成功  无返回数据
     *
     * @return
     */
    public static JSONObject success() {

        JSONObject resultJSON = new JSONObject();

        resultJSON.put("data", "");
        resultJSON.put(IS_SUCCESS, SUCCESS);

        return resultJSON;
    }

    /**
     * 请求失败
     *
     * @param resultData
     * @return
     */
    public static JSONObject failure(Object resultData) {

        JSONObject resultJSON = new JSONObject();

        resultJSON.put("data", resultData);
        resultJSON.put(IS_SUCCESS, FAILURE);

        return resultJSON;
    }

    /**
     * 请求失败
     *
     * @param errorMessage
     * @return
     */
    public static JSONObject failure(String errorMessage) {

        JSONObject resultJSON = new JSONObject();

        resultJSON.put(IS_SUCCESS, FAILURE);
        resultJSON.put(ERROR_MESSAGE, errorMessage);

        return resultJSON;

    }

    public static JSONObject failure(String errorCode, String errorMessage) {

        JSONObject resultJSON = new JSONObject();

        resultJSON.put(IS_SUCCESS, errorCode);
        resultJSON.put(ERROR_MESSAGE, errorMessage);

        return resultJSON;

    }
}

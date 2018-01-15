package com.github.quartz.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * Created by 陈敏 on 2017/8/25.
 */
public class JSONUtil {
    private static final Logger logger = LoggerFactory.getLogger(JSONUtil.class);

    public static JSONObject jsonObject(String jsonFile) {
        return jsonObject(new File(jsonFile));
    }

    public static JSONObject jsonObject(File jsonFile) {
        if (!jsonFile.exists()) {
            return null;
        }
        return JSON.parseObject(json(jsonFile));
    }

    public static JSONArray jsonArray(String jsonFile) {
        return jsonArray(new File(jsonFile));
    }

    public static JSONArray jsonArray(File jsonFile) {
        if (!jsonFile.exists()) {
            return null;
        }
        return JSON.parseArray(json(jsonFile));
    }

    public static String json(File jsonFile) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(jsonFile))) {
            String jsonTxt = null;
            while ((jsonTxt = br.readLine()) != null) {
                sb.append(jsonTxt);
            }
        } catch (Exception e) {
            logger.error("Failed to read file", e);
        }
        return sb.toString();
    }

    public static String getJSONString(String jsonFile, String keyPath) {
        return getJSONString(new File(jsonFile), keyPath);
    }

    public static String getJSONString(File jsonFile, String keyPath) {
        JSONObject jsonObject = JSON.parseObject(json(jsonFile));
        return getJSONString(jsonObject, keyPath);
    }

    public static String getJSONString(JSONObject jsonObject, String keyPath) {
        JSONObject json = jsonObject;
        String[] keys = keyPath.split("/");
        String result = "";
        for (int i = 0; i < keys.length; i++) {
            if (i == keys.length - 1) {
                result = json.getString(keys[i]);
                continue;
            }
            json = json.getJSONObject(keys[i]);
        }
        return result;
    }

    public static JSONObject getJSONObject(String jsonFile, String keyPath) {
        return getJSONObject(new File(jsonFile), keyPath);
    }

    public static JSONObject getJSONObject(File jsonFile, String keyPath) {
        JSONObject jsonObject = JSON.parseObject(json(jsonFile));
        return getJSONObject(jsonObject, keyPath);
    }

    public static JSONObject getJSONObject(JSONObject jsonObject, String keyPath) {
        JSONObject json = jsonObject;
        String[] keys = keyPath.split("/");
        JSONObject result = new JSONObject();
        for (int i = 0; i < keys.length; i++) {
            if (i == keys.length - 1) {
                result = json.getJSONObject(keys[i]);
                continue;
            }
            json = json.getJSONObject(keys[i]);
        }
        return result;
    }

    public static JSONArray getJSONArray(String jsonFile, String keyPath) {
        return getJSONArray(new File(jsonFile), keyPath);
    }

    public static JSONArray getJSONArray(File jsonFile, String keyPath) {
        JSONObject jsonObject = JSON.parseObject(json(jsonFile));
        return getJSONArray(jsonObject, keyPath);
    }

    public static JSONArray getJSONArray(JSONObject jsonObject, String keyPath) {
        JSONObject json = jsonObject;
        String[] keys = keyPath.split("/");
        JSONArray result = new JSONArray();
        for (int i = 0; i < keys.length; i++) {
            if (i == keys.length - 1) {
                result = json.getJSONArray(keys[i]);
                continue;
            }
            json = json.getJSONObject(keys[i]);
        }
        return result;
    }
}

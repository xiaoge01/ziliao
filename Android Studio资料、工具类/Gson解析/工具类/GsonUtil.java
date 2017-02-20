package com.spro.dailynews.util;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/12/6.
 */

public class GsonUtil {
                          //Gson解析 需要一个Json字符串和实体类对象
    public static <T> List<T> gson(String jsonresult, Class<T> tclass) {
        ArrayList<T> list = new ArrayList<>();

        try {

            JSONObject jsonObject = new JSONObject(jsonresult);
            if (jsonObject.getInt("status") == 0) {
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject01 = jsonArray.getJSONObject(i);
                    Gson gson = new Gson();
                    T result = gson.fromJson(jsonObject01.toString(), tclass);
                    list.add(result);
                }
            } else {
                //Toast.makeText()
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}

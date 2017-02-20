package com.spro.retrofit;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by gqq on 2017/1/9.
 */
// Retrofit接口的构建
public interface RetrofitApi {

    /**
     Request request = new Request.Builder()
     .get()
     .url(url)
     .build();
     return mOkHttpClient.newCall(request);
     */

    // GET请求的构建 //{..}里面是可变,两个参数
    @GET("https://api.github.com/{search}/repositories")
    Call<ResponseBody> getData(@Path("search") String search, @Query("q")String q, @Query("page")int page);

    // post请求：注册的
    @POST("http://admin.syfeicuiedu.com/Handler/UserHandler.ashx?action=register")
    //添加请求头
    @Headers({"Accept-type:en","X-Type:xxx"})
    //动态添加请求头
    Call<UserResult> register(@Body User user, @Header("Accept-type")String type, @Header("X-Type")String aaa);
/**
 * http方面的注解
 * 1. 请求方式：@GET，@POST
 * 2. url:单独说
 * 3. 请求头:
 *      @Headers({"Accept-type:en","X-Type:xxx"})
 *      @Header() 动态修改请求头
 * 4. 请求体：
 *      @Body User user，未添加转换器：RequestBody
 */

    /**
     * Url方面会用到的注解：
     * 1. 查询的参数：一般用在Get请求方面
     *      @Query("q")String q
     *      @QueryMap
     * 2. 可替换块：把Url里面的某一个参数替换掉，形成一个完整的Url
     *      http://www.baidu.com/{user}/message/name
     *      @Path("")
     * 3. 表单提交
     *      方法上：@FormUrlEncoded 表明他是一个表单提交的方式
     *      @Field("username")String name
     *      @FieldMap
     *
     * 4. 多部分提交
     *      方法上：@Multipart 表明他是一个多部分提交
     *      @Part("user")
     */

//    String url = "http://www.baidu.com/"+user+"/message/name";

    @FormUrlEncoded
    @POST("http://wx.feicuiedu.com:9094/yitao/UserWeb?method=register")
    Call<ResponseBody> getForm(@FieldMap Map<String,String> map);


    @Multipart
    @POST("http://wx.feicuiedu.com:9094/yitao/UserWeb?method=update")
    Call<ResponseBody> getMult(@Part("user")MultUser user);

}

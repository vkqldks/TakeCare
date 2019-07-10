package com.vkqldks12.takecare.Api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by vkqld on 2019-02-07.
 */

public interface Api {

    @FormUrlEncoded
    @POST("vod_list.php")
    Call<ResponseBody> vod_list(
            @Field("username") String username
    );

    @FormUrlEncoded
    @POST("vod_chat_list.php")
    Call<ResponseBody> vod_chat_list(
            @Field("username") String username
    );
}

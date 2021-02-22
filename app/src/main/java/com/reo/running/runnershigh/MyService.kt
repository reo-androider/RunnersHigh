package com.reo.running.runnershigh

import android.telecom.Call
import okhttp3.ResponseBody
import retrofit2.http.*

interface MyService {
    @GET("posts")
    fun getRawResponseForPosts() : retrofit2.Call<ResponseBody>

    @POST("posts")
    fun postRawRequestForPosts(@Body body: ResponseBody) : retrofit2.Call<ResponseBody>

    @PUT("posts/{id}")
    fun putRawRequestForPosts(@Path("id") id:String) : retrofit2.Call<ResponseBody>

    @DELETE("posts/{id}")
    fun deletePathParam(@Path("id") id:String) : retrofit2.Call<ResponseBody>
}
package com.example.armenianairlines.data.api

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitInstance {

    companion object {
        val localURL = "http://10.0.2.2:5000/"
        val globalURL = "https://a13x.pythonanywhere.com/"


        private val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(2, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build()



        fun getRetrofitInstance(url: String): Retrofit {
            return Retrofit.Builder()
                .baseUrl(url)
                .client(okHttpClient)
                .addConverterFactory(
                    GsonConverterFactory
                        .create(GsonBuilder().create())
                )
                .build()
        }
    }
}
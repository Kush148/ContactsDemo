package kush.example.contactsdemo.api

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    var gson = GsonBuilder()
        .setLenient()
        .create()
    val client = OkHttpClient.Builder()
        .connectTimeout(100, TimeUnit.SECONDS)
        .readTimeout(100, TimeUnit.SECONDS)
        .build()
    var mRetrofit = Retrofit.Builder()
        .client(client)
        .baseUrl("http://192.168.2.24/address_book/")
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    fun getClient(): APIService {
        return mRetrofit.create<APIService>(APIService::class.java)
    }
}
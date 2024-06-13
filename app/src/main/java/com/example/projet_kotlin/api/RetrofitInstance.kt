package com.example.projet_kotlin.api

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

object RetrofitInstance {

    private const val BASE_URL = "https://restcountries.com/"

    val api: CountryApi by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val retryInterceptor = Interceptor { chain ->
            makeRequestWithRetry(chain, chain.request(), 0)
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor(retryInterceptor)
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CountryApi::class.java)
    }

    @Throws(IOException::class)
    private fun makeRequestWithRetry(chain: Interceptor.Chain, request: Request, attempt: Int): Response {
        return try {
            chain.proceed(request)
        } catch (e: IOException) {
            if (attempt < 6) {
                makeRequestWithRetry(chain, request, attempt + 1)
            } else {
                throw e
            }
        }
    }
}

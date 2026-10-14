package com.gmaldonado.taxflow_movil.data.remote

import com.gmaldonado.taxflow_movil.data.repository.TaxflowRepository
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    private const val BASE_URL = "https://8cspr7pmca.execute-api.us-east-1.amazonaws.com/"

    // Inyecta Authorization: Bearer <token> si hay sesión activa
    private val authInterceptor = Interceptor { chain ->
        val original = chain.request()
        val token = TaxflowRepository.getAuthToken()
        val builder = original.newBuilder()

        if (!token.isNullOrBlank()) {
            builder.header("Authorization", "Bearer $token")
        }

        chain.proceed(builder.build())
    }

    // Rate limiting interceptor con retroceso exponencial ante HTTP 429
    private val rateLimitRetryInterceptor = Interceptor { chain ->
        var response = chain.proceed(chain.request())
        var attempt = 0
        val maxRetries = 3

        while (response.code == 429 && attempt < maxRetries) {
            attempt++
            response.close()
            val backoffMs = (500L * (1 shl (attempt - 1))) // 500ms, 1000ms, 2000ms
            try {
                Thread.sleep(backoffMs)
            } catch (_: InterruptedException) {}
            response = chain.proceed(chain.request())
        }
        response
    }

    private val okHttpClient: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

        OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(rateLimitRetryInterceptor)
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val service: TaxflowApiService by lazy {
        retrofit.create(TaxflowApiService::class.java)
    }
}

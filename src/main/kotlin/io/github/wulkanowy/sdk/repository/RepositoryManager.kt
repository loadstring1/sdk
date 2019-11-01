package io.github.wulkanowy.sdk.repository

import io.github.wulkanowy.sdk.interceptor.SignInterceptor
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.create

class RepositoryManager(
    private val logLevel: HttpLoggingInterceptor.Level,
    private val privateKey: String,
    private val certKey: String,
    private val interceptors: MutableList<Pair<Interceptor, Boolean>>,
    private val apiBaseUrl: String,
    private val schoolSymbol: String
) {

    fun getRoutesRepository(): RoutingRulesRepository {
        return RoutingRulesRepository(getRetrofitBuilder(interceptors).baseUrl("http://komponenty.vulcan.net.pl").build().create())
    }

    fun getMobileRepository(): MobileRepository {
        return MobileRepository(getRetrofitBuilder(interceptors).baseUrl("$apiBaseUrl/$schoolSymbol/mobile-api/Uczen.v3.Uczen/").build().create())
    }

    fun getRegisterRepository(host: String, symbol: String): RegisterRepository {
        return RegisterRepository(getRetrofitBuilder(interceptors).baseUrl("$host/$symbol/mobile-api/Uczen.v3.UczenStart/").build().create())
    }

    private fun getRetrofitBuilder(interceptors: MutableList<Pair<Interceptor, Boolean>>): Retrofit.Builder {
        return Retrofit.Builder()
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(OkHttpClient().newBuilder()
                .addInterceptor(HttpLoggingInterceptor().setLevel(logLevel))
                .addInterceptor(SignInterceptor(privateKey, certKey))
                .apply {
                    interceptors.forEach {
                        if (it.second) addNetworkInterceptor(it.first)
                        else addInterceptor(it.first)
                    }
                }
                .build()
            )
    }
}
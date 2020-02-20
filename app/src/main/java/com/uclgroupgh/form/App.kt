package com.uclgroupgh.form

import android.os.StrictMode

import com.orm.SugarApp
import com.uclgroupgh.form.utils.AndroidUtils
import com.uclgroupgh.form.utils.Constants

import net.gotev.hostmonitor.HostMonitorConfig
import net.gotev.uploadservice.Logger
import net.gotev.uploadservice.UploadService
import net.gotev.uploadservice.okhttp.OkHttpStack

import java.io.IOException
import java.util.concurrent.TimeUnit

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

//import com.facebook.stetho.Stetho;
//import com.facebook.stetho.okhttp3.StethoInterceptor;
//import okhttp3.logging.HttpLoggingInterceptor;
//
//import static com.facebook.stetho.Stetho.newInitializerBuilder;

/**
 * @author gotev (Aleksandar Gotev)
 */
class App : SugarApp() {

    private val okHttpClient: OkHttpClient
        get() = OkHttpClient.Builder()
            .followRedirects(true)
            .followSslRedirects(true)
            .retryOnConnectionFailure(true)
            .connectTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .addNetworkInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("myheader", "myvalue")
                    .addHeader("mysecondheader", "mysecondvalue")

                chain.proceed(request.build())
            }

            .cache(null)
            .build()

    override fun onCreate() {
        super.onCreate()

        //initialize and create the image loader logic
        // Set your application namespace to avoid conflicts with other apps
        // using this library
        UploadService.NAMESPACE = BuildConfig.APPLICATION_ID

        // Set upload service debug log messages level
        Logger.setLogLevel(Logger.LogLevel.DEBUG)

        // Set up the Http Stack to use. If you omit this or comment it, HurlStack will be
        // used by default
        UploadService.HTTP_STACK = OkHttpStack(okHttpClient)

        // setup backoff multiplier
        UploadService.BACKOFF_MULTIPLIER = 2

        configureHostMonitor()

    }

    //method to configure host monitor
    fun configureHostMonitor() {
        try {
            HostMonitorConfig(this)
                .setBroadcastAction(BuildConfig.APPLICATION_ID)
                .add(
                    AndroidUtils.getPreferenceData(this, Constants.IPPREF, "0.0.0.0"),
                    Integer.valueOf(AndroidUtils.getPreferenceData(this, Constants.PORT, "8080"))
                )
                .setCheckIntervalInMinutes(Constants.HOST_MONITOR_INTERVAL_MINS)
                .save()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun enableStrictMode() {
        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .penaltyDialog()
                .build()
        )
        StrictMode.setVmPolicy(
            StrictMode.VmPolicy.Builder().detectAll()
                .penaltyLog()
                .build()
        )
    }
}

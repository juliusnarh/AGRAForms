package com.uclgroupgh.form.service

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.util.Log
import androidx.annotation.Nullable

import com.uclgroupgh.form.entities.FilledForms
import com.uclgroupgh.form.utils.AndroidUtils

import java.io.File

class FormService : Service() {
    private val handler = Handler()
    internal lateinit var intent: Intent
    private val sendUpdatesToUI = object : Runnable {
        override fun run() {
            DisplayLoggingInfo()
            handler.postDelayed(this, 2000) // 10 seconds
        }
    }

    @Override
    override fun onCreate() {
        super.onCreate()
        intent = Intent(BROADCAST_ACTION)
        Log.d(TAG, "SERVICE CREATED")
    }

    @Override
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d(TAG, "SERVICE STARTED")
        handler.removeCallbacks(sendUpdatesToUI)
        handler.postDelayed(sendUpdatesToUI, 5000)
        return START_NOT_STICKY
    }

    private fun DisplayLoggingInfo() {
        Log.d(TAG, "entered DisplayLoggingInfo")

        val list = FilledForms.listAll(FilledForms::class.java)
        if (list.size > 0) {
            val lastform = FilledForms.last(FilledForms::class.java)
            Log.d(
                TAG,
                list.size.toString() + AndroidUtils.dateToFormattedString(
                    lastform.getDatecreated(),
                    "MMMM dd, yyyy"
                )
            )
            intent.putExtra("totalforms", list.size.toString())
            intent.putExtra(
                "lastdate",
                AndroidUtils.dateToFormattedString(
                    lastform.getDatecreated(),
                    "MMMM dd, yyyy"
                )
            )

        } else {
            Log.d(TAG, list.size.toString() + "N/A")
            intent.putExtra("totalforms", list.size.toString())
            intent.putExtra("lastdate", "N/A")
        }

        intent.putExtra("synccount", unsyncedFiles().toString())


        sendBroadcast(intent)
    }

    fun unsyncedFiles(): Long {
        try {
            //dirpath = Environment.getExternalStorageDirectory() + File.separator  + "uploadfolder";
            return File(AndroidUtils.uploadDirectoryPath()).listFiles().size.toLong()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        return 0
    }

    @Override
    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(sendUpdatesToUI)
        Log.d(TAG, "onDestroy")
    }

    @Nullable
    @Override
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    companion object {
        val BROADCAST_ACTION = "com.uclgroupgh.form"
        private val TAG = "FORM SERVICE"
    }
}
